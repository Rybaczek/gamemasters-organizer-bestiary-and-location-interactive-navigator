package goblin.delete_manager;

import goblin.compendium_entry.CompendiumEntry;
import goblin.compendium_entry.CompendiumEntryService;
import goblin.compendium_entry.EntryType;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class DeleteManagerTest {

    @BeforeEach
    public void cleanInMemoryDatabaseEach() {
        compendiumEntryService.findALl().forEach(
                compendiumEntry -> {
                    UUID id = compendiumEntry.getId();
                    compendiumEntryService.hardDelete(id);
                }
        );

        deleteManagerService.findALl().forEach(
                compendiumEntry -> {
                    UUID id = compendiumEntry.getSoftDeletedEntryId();
                    deleteManagerService.delete(id);
                }
        );
    }

    @Autowired
    private CompendiumEntryService compendiumEntryService;
    @Autowired
    private DeleteManagerService deleteManagerService;

    @Test
    void shouldMarkEntryAsSoftDeleted() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);

        //WHEN
        compendiumEntryService.softDelete(id);
        CompendiumEntry actualEntry = compendiumEntryService.findSoftDeleted(id);

        //THEN
        CompendiumEntry expectedCompendiumEntry = createExpectedCompendiumEntry("Dragon", EntryType.MONSTER, Boolean.TRUE);
        assertAreEqual(actualEntry,expectedCompendiumEntry);
    }

    @Test
    void shouldNotShowRecordsMarkedWithSoftDelete() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        CompendiumEntry compendiumEntryToBeDeleted = new CompendiumEntry("Beholder", EntryType.MONSTER);
        compendiumEntryService.save(compendiumEntry);
        UUID idToBeDeleted = compendiumEntryService.save(compendiumEntryToBeDeleted);
        compendiumEntryService.softDelete(idToBeDeleted);

        //WHEN
        List<CompendiumEntry> compendiumEntries = compendiumEntryService.findALl();

        //THEN
        Assertions.assertThat(compendiumEntries).hasSize(1);
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry("Dragon", EntryType.MONSTER);
        assertAreEqual(compendiumEntries.get(0),expectedCompendiumEntry);
    }

    @Test
    void shouldNotShowRecordsMarkedAsSoftDelete() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntryToBeDeleted = new CompendiumEntry("Beholder", EntryType.MONSTER);
        UUID idToBeDeleted = compendiumEntryService.save(compendiumEntryToBeDeleted);
        compendiumEntryService.softDelete(idToBeDeleted);

        //WHEN && THEN
        Assertions.assertThatThrownBy(() -> compendiumEntryService.find(idToBeDeleted))
                .isInstanceOf(CompendiumEntryNotFoundException.class)
                .hasMessage("Couldn't find a Compendium Entry with id " + idToBeDeleted);

    }

    @Test
    void shouldShowRestoredCompendiumEntry() throws CompendiumEntryNotFoundException {
        //GIVEN
        CompendiumEntry compendiumEntry = new CompendiumEntry("Goblin", EntryType.MONSTER);
        UUID id = compendiumEntryService.save(compendiumEntry);
        compendiumEntryService.softDelete(id);

        //WHEN
        compendiumEntryService.restore(id);

        //THEN
        Assertions.assertThat(deleteManagerService.findALl()).size().isEqualTo(0);
        Assertions.assertThat(compendiumEntryService.findALl()).size().isEqualTo(1);
        assertAreEqual(compendiumEntryService.find(id),createExpectedCompendiumEntry("Goblin", EntryType.MONSTER, Boolean.FALSE));
    }

    private CompendiumEntry createExpectedCompendiumEntry(String name, EntryType entryType, Boolean isSoftDeleted) {
        CompendiumEntry expectedCompendiumEntry = new CompendiumEntry(name, entryType);
        expectedCompendiumEntry.setIsSoftDeleted(isSoftDeleted);
        return expectedCompendiumEntry;
    }

    private void assertAreEqual(CompendiumEntry actualCompendiumEntry, CompendiumEntry expectedCompendiumEntry) {
        Assertions.assertThat(actualCompendiumEntry)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedCompendiumEntry);
    }
}
