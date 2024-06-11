package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.CompendiumEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompendiumEntryInMemoryRepository {

    UUID save(CompendiumEntry compendiumEntry);

    Optional<CompendiumEntry> findById(UUID compendiumEntryId);
    Optional<CompendiumEntry> findSoftDeletedById(UUID compendiumEntryId);

    CompendiumEntry findExisting(UUID compendiumEntryId) throws CompendiumEntryNotFoundException;
    CompendiumEntry findSoftDeleted(UUID compendiumEntryId) throws CompendiumEntryNotFoundException;

    List<CompendiumEntry> findAll();

    void update(UUID compendiumEntryId, CompendiumEntry compendiumEntry);

    void delete(UUID compendiumEntryId);
}
