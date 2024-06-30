package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.UUID;

@Primary
@TestComponent
public class CompendiumEntryInMemoryTestRepository extends CompendiumEntryInMemoryRepositoryImpl {

    public CompendiumEntryInMemoryTestRepository(FixedDateTimeProvider fixedDateTimeProvider) {
        super(fixedDateTimeProvider);
    }

    public void clean() {
        compendiumEntries.clear();
    }

    public List<CompendiumEntry> findByOwnerIdIncludingSoftDeletedCompendiumEntries(UUID ownerId) {
        return compendiumEntries.values()
                .stream()
                .filter(compendiumEntry -> compendiumEntry.getOwnerId().equals(ownerId))
                .toList();
    }
}