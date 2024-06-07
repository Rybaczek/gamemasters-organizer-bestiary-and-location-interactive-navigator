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
                    compendiumEntryService.delete(id);
                }
        );
    }

    @Autowired
    private CompendiumEntryService compendiumEntryService;

    @Test
    void shouldSaveRecordInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", "Monster");
        //WHEN
        compendiumEntryService.save(compendiumEntry);

        //THEN
        Assertions.assertThat(compendiumEntryService.findALl()).isNotEmpty();
    }

    @Test
    void shouldFindRecordInDatabase() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", "Monster");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Dragon", "Monster");
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
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", "Monster");
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Goblin", "Monster");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.update(id, expectedCompendiumEntry);

        //THEN
        CompendiumEntry actualEntry = compendiumEntryService.find(id);
        Assertions.assertThat(actualEntry).isEqualTo(expectedCompendiumEntry);
    }

    @Test
    void shouldDeleteRecordInDatabase() {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", "Monster");
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.delete(id);

        //THEN
        Assertions.assertThat(compendiumEntryService.findALl()).isEmpty();
    }
}
