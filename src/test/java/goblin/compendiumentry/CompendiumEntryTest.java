package goblin.compendiumentry;

import goblin.BaseIntegrationTest;
import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.CompendiumEntryDto;
import goblin.compendium_entry.core.CompendiumEntryService;
import goblin.compendium_entry.core.EntryType;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepositoryImpl;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryTestRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;


class CompendiumEntryTest extends BaseIntegrationTest {

    @Autowired
    private CompendiumEntryService compendiumEntryService;
    @Autowired
    private CompendiumEntryInMemoryRepositoryImpl compendiumEntryInMemoryRepository;
    @Autowired
    private CompendiumEntryInMemoryTestRepository compendiumEntryInMemoryTestRepository;

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
    void shouldDeleteCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry(ownerId, "Jan", EntryType.NPC);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.delete(id);

        //THEN
        Assertions.assertThat(compendiumEntryInMemoryTestRepository.findByOwnerId(ownerId)).isEmpty();
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

    private void assertAreEqual(CompendiumEntry actualCompendiumEntry, CompendiumEntry expectedCompendiumEntry) {
        Assertions.assertThat(actualCompendiumEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }
}