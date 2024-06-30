package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CompendiumEntryInMemoryRepository {

    UUID save(CompendiumEntry compendiumEntry);

    CompendiumEntry findExisting(UUID compendiumEntryId);

    CompendiumEntry findSoftDeletedById(UUID compendiumEntryId);

    List<CompendiumEntry> findAllExceedingSoftDeleteDate(Instant date);

    List<CompendiumEntry> findByOwnerId(UUID ownerId);

    void update(UUID compendiumEntryId, CompendiumEntry compendiumEntry);

    void delete(UUID compendiumEntryId);
}
