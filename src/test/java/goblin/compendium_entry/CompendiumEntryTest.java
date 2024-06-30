package goblin.compendium_entry;

import goblin.BaseIntegrationTest;
import goblin.compendium_entry.api.scheduler.Scheduler;
import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.CompendiumEntryDto;
import goblin.compendium_entry.core.CompendiumEntryService;
import goblin.compendium_entry.core.EntryType;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepositoryImpl;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryTestRepository;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import goblin.compendium_entry.infrastructure.FixedDateTimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
class CompendiumEntryTest extends BaseIntegrationTest {

    @Autowired
    @SpyBean
    private CompendiumEntryService compendiumEntryService;
    @Autowired
    private CompendiumEntryInMemoryRepositoryImpl compendiumEntryInMemoryRepository;
    @Autowired
    private CompendiumEntryInMemoryTestRepository compendiumEntryInMemoryTestRepository;
    @Autowired
    private FixedDateTimeProvider fixedDateTimeProvider;
    @Autowired
    @SpyBean
    private Scheduler scheduler;

    @Value("${goblin.compendium-entry.days-to-hard-delete}")
    private Integer softDeletedEntryDeleteTime;
    private final static UUID ownerId = UUID.fromString("e9c97a48-34db-4612-ba1f-5501439cc7f6");

    @Test
    void shouldFindSavedCompendiumEntryFromDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry(ownerId, "Dragon", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry(ownerId, "Dragon", EntryType.MONSTER);
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldUpdateCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry(ownerId, "Sword", EntryType.ITEM);
        UUID id = compendiumEntryService.save(compendiumEntry);

        CompendiumEntryDto updatedCompendiumData = new CompendiumEntryDto("Axe");

        //WHEN
        compendiumEntryService.update(id, updatedCompendiumData);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry(ownerId, "Axe", EntryType.ITEM);
        CompendiumEntry actualEntry = compendiumEntryService.find(id);
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldNotShowCompendiumEntriesMarkedWithSoftDelete() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry(ownerId, "Dragon", EntryType.MONSTER);
        compendiumEntryService.save(compendiumEntry);

        CompendiumEntry compendiumEntryToBeDeleted = new CompendiumEntry(ownerId, "Beholder", EntryType.MONSTER);
        UUID idToBeDeleted = compendiumEntryService.save(compendiumEntryToBeDeleted);

        //WHEN
        compendiumEntryService.markForSoftDelete(idToBeDeleted);

        //THEN
        List<CompendiumEntry> compendiumEntries = compendiumEntryInMemoryRepository.findByOwnerId(ownerId);
        Assertions.assertThat(compendiumEntries)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(new CompendiumEntry(ownerId, "Dragon", EntryType.MONSTER)));
    }

    @Test
    void shouldShowRestoredCompendiumEntry() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry(ownerId, "Goblin", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);
        compendiumEntryService.markForSoftDelete(id);

        //WHEN
        compendiumEntryService.restoreSoftDeleted(id);

        //THEN
        assertAreEqual(compendiumEntryService.find(id), new CompendiumEntry(ownerId, "Goblin", EntryType.MONSTER));
    }

    @Test
    void shouldCleanupDeletedCompendiumEntries() {
        //GIVEN && WHEN
        fixedDateTimeProvider.setClockTime(Instant.parse("2024-06-14T12:59:59.000Z"));

        prepareSoftDeletedCompendiumEntry("Dragon", EntryType.MONSTER, 0);
        prepareSoftDeletedCompendiumEntry("Goblin", EntryType.MONSTER, softDeletedEntryDeleteTime);

        UUID softDeletedCompendiumEntryToBeDeleted = prepareSoftDeletedCompendiumEntry("Sword", EntryType.ITEM, softDeletedEntryDeleteTime + 1);

        //THEN
        Awaitility.await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> Mockito.verify(scheduler, Mockito.atLeastOnce()).scheduler());

        Awaitility.await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> Mockito.verify(compendiumEntryService, Mockito.atLeastOnce()).cleanupDeletedCompendiumEntries());

        List<CompendiumEntry> compendiumEntries = compendiumEntryInMemoryTestRepository.findByOwnerIdIncludingSoftDeletedCompendiumEntries(ownerId);
        Assertions.assertThat(compendiumEntries)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(prepareExpectedCompendiumEntry()
                );

        Assertions.assertThatThrownBy(() -> compendiumEntryInMemoryTestRepository.findSoftDeletedById(softDeletedCompendiumEntryToBeDeleted))
                .isInstanceOf(CompendiumEntryNotFoundException.class)
                .hasMessage("Couldn't find a soft deleted Compendium Entry with id : " + softDeletedCompendiumEntryToBeDeleted.toString());
    }

    private UUID prepareSoftDeletedCompendiumEntry(String name, EntryType entryType, Integer integer) {
        CompendiumEntry softDeletedCompendiumEntry = new CompendiumEntry(ownerId, name, entryType);
        softDeletedCompendiumEntry.setSoftDeleteDate(fixedDateTimeProvider.currentDate().minus(integer, ChronoUnit.DAYS));
        return compendiumEntryService.save(softDeletedCompendiumEntry);
    }

    private List<CompendiumEntry> prepareExpectedCompendiumEntry() {
        CompendiumEntry a = new CompendiumEntry(ownerId, "Dragon", EntryType.MONSTER);
        a.setSoftDeleteDate(fixedDateTimeProvider.currentDate().minus(0, ChronoUnit.DAYS));
        CompendiumEntry b = new CompendiumEntry(ownerId, "Goblin", EntryType.MONSTER);
        b.setSoftDeleteDate(fixedDateTimeProvider.currentDate().minus(softDeletedEntryDeleteTime, ChronoUnit.DAYS));
        return List.of(a, b);
    }


    private void assertAreEqual(CompendiumEntry actualCompendiumEntry, CompendiumEntry expectedCompendiumEntry) {
        Assertions.assertThat(actualCompendiumEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }
}