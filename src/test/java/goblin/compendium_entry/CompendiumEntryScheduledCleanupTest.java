package goblin.compendium_entry;

import goblin.BaseIntegrationTest;
import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.CompendiumEntryService;
import goblin.compendium_entry.core.EntryType;
import goblin.compendium_entry.infrastructure.CompendiumEntryRepository;
import goblin.compendium_entry.infrastructure.FixedDateTimeProvider;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@TestPropertySource(properties = {
        "goblin.compendium-entry.cron.cleanup-deleted-compendium-entries=0/5 * * * * *",
        "logging.level.goblin.compendium_entry=trace",
        "logging.level.org.springframework=trace"
})
class CompendiumEntryScheduledCleanupTest extends BaseIntegrationTest {

    private static final Instant ANY_DATE = Instant.parse("2024-06-14T12:59:59.000Z");
    private static final UUID OWNER_ID = UUID.fromString("e9c97a48-34db-4612-ba1f-5501439cc7f6");

    @Autowired
    private CompendiumEntryService compendiumEntryService;

    @Autowired
    private CompendiumEntryRepository compendiumEntryRepository;

    @Autowired
    private FixedDateTimeProvider fixedDateTimeProvider;

    @Value("${goblin.compendium-entry.days-to-hard-delete}")
    private Integer softDeletedThresholdDate;

    @Test
    void shouldCleanupDeletedCompendiumEntriesOlderThan() {
        // GIVEN
        Instant dateBeforeTheThreshold = ANY_DATE;
        Instant dateAtThreshold = dateBeforeTheThreshold.minus(softDeletedThresholdDate, ChronoUnit.DAYS);
        Instant dateExceedingThreshold = dateAtThreshold.minus(1, ChronoUnit.DAYS);

        fixedDateTimeProvider.setClockTime(dateBeforeTheThreshold);

        UUID entryBeforeTheThreshold = prepareSoftDeletedCompendiumEntry(dateBeforeTheThreshold);
        UUID entryAtTheThreshold = prepareSoftDeletedCompendiumEntry(dateAtThreshold);
        prepareSoftDeletedCompendiumEntry(dateExceedingThreshold);

        // WHEN
        // cleanup executed by scheduler

        // THEN
        assertSoftDeletedEntriesExceedingThresholdAreDeleted(dateExceedingThreshold);
        assertEntriesNotExceedingThresholdArePresent(dateBeforeTheThreshold, entryBeforeTheThreshold, entryAtTheThreshold);
    }

    private void assertEntriesNotExceedingThresholdArePresent(Instant dateBeforeTheThreshold, UUID entryBeforeTheThreshold, UUID entryAtTheThreshold) {
        Awaitility.await().untilAsserted(() -> Assertions.assertThat(compendiumEntryRepository
                        .findAllExceedingSoftDeleteDate(dateBeforeTheThreshold.plus(1, ChronoUnit.DAYS))
                        .stream()
                        .map(CompendiumEntry::getId)
                        .toList()
                )
                .containsExactlyInAnyOrder(entryBeforeTheThreshold, entryAtTheThreshold));
    }

    private void assertSoftDeletedEntriesExceedingThresholdAreDeleted(Instant dateExceedingThreshold) {
        Awaitility.await().untilAsserted(() -> Assertions.assertThat(compendiumEntryRepository
                .findAllExceedingSoftDeleteDate(dateExceedingThreshold.plus(1, ChronoUnit.DAYS))).isEmpty());
    }

    private UUID prepareSoftDeletedCompendiumEntry(Instant instant) {
        CompendiumEntry softDeletedCompendiumEntry = new CompendiumEntry(OWNER_ID, UUID.randomUUID().toString(), EntryType.MONSTER);
        softDeletedCompendiumEntry.setSoftDeleteDate(instant);
        return compendiumEntryService.save(softDeletedCompendiumEntry);
    }

}
