package goblin.compendium_entry;

import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
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
        compendiumEntryService.findALl().forEach(
                compendiumEntry -> {
                    UUID id = compendiumEntry.getId();
                    compendiumEntryService.hardDelete(id);
                }
        );
    }

    @Autowired
    private CompendiumEntryService compendiumEntryService;

    @Test
    void shouldSaveRecordInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);

        //WHEN
        compendiumEntryService.save(compendiumEntry);

        //THEN
        Assertions.assertThat(compendiumEntryService.findALl()).isNotEmpty();
    }

    @Test
    void shouldFindRecordInDatabase() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        Assertions.assertThat(actualEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }

    @Test
    void shouldThrowExceptionWhenSearchedEntryIsNotPresent() {
        //GIVEN
        UUID id = UUID.randomUUID();

        //WHEN && THEN
        Assertions.assertThatThrownBy(() -> compendiumEntryService.find(id))
                .isInstanceOf(CompendiumEntryNotFoundException.class)
                .hasMessage("Couldn't find a Compendium Entry with id " + id);
    }

    @Test
    void shouldUpdateRecordInDatabase() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Goblin", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.update(id, expectedCompendiumEntry);

        //THEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);
        assertAreEqual(actualEntry,expectedCompendiumEntry);
    }

    @Test
    void shouldDeleteRecordInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.hardDelete(id);

        //THEN
        Assertions.assertThat(compendiumEntryService.findALl()).isEmpty();
    }

    private void assertAreEqual(CompendiumEntry actualCompendiumEntry, CompendiumEntry expectedCompendiumEntry) {
        Assertions.assertThat(actualCompendiumEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }
}
