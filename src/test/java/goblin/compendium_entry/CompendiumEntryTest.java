package goblin.compendium_entry;

import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.CompendiumEntryService;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryTestRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class CompendiumEntryTest {

    @BeforeEach
    public void cleanInMemoryDatabaseEach() {
        compendiumEntryInMemoryTestRepository.clean();
    }

    @Autowired
    private CompendiumEntryService compendiumEntryService;
    @Autowired
    private CompendiumEntryInMemoryTestRepository compendiumEntryInMemoryTestRepository;

    @Test
    void shouldFindSavedCompendiumEntryFromDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", "Monster");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Dragon", "Monster");
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldUpdateCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Sword", "Item");
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Axe", "Item");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.update(id, expectedCompendiumEntry);

        //THEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);
        assertAreEqual(actualEntry, expectedCompendiumEntry);
    }

    @Test
    void shouldDeleteCompendiumEntryInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Jan", "NPC");
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