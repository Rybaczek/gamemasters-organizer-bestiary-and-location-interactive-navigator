package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;

import java.util.UUID;

public interface CompendiumEntryInMemoryRepository {

    UUID save(CompendiumEntry compendiumEntry);

    CompendiumEntry findExisting(UUID compendiumEntryId);

    void update(UUID compendiumEntryId, CompendiumEntry compendiumEntry);

    void delete(UUID compendiumEntryId);
}
