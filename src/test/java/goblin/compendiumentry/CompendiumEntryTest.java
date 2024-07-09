package goblin.compendiumentry;

import goblin.BaseIntegrationTest;
import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.CompendiumEntryDto;
import goblin.compendium_entry.core.CompendiumEntryService;
import goblin.compendium_entry.core.EntryType;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryTestRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


class CompendiumEntryTest extends BaseIntegrationTest {

    @Autowired
    private CompendiumEntryService compendiumEntryService;
    @Autowired
    private CompendiumEntryInMemoryTestRepository compendiumEntryInMemoryTestRepository;

    @Test
    void shouldFindSavedCompendiumEntryFromDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldUpdateCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Sword", EntryType.ITEM);
        CompendiumEntryDto updatedCompendiumData = new CompendiumEntryDto("Axe");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.update(id, updatedCompendiumData);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Axe", EntryType.ITEM);
        CompendiumEntry actualEntry = compendiumEntryService.find(id);
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldDeleteCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Jan", EntryType.NPC);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.delete(id);

        //THEN
        Assertions.assertThat(compendiumEntryInMemoryTestRepository.findAll()).isEmpty();
    }

    private void assertAreEqual(CompendiumEntry actualCompendiumEntry, CompendiumEntry expectedCompendiumEntry) {
        Assertions.assertThat(actualCompendiumEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }
}