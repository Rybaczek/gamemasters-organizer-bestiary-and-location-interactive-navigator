package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CompendiumEntryInMemoryRepositoryImpl implements CompendiumEntryInMemoryRepository {

    protected final Map<UUID, CompendiumEntry> compendiumEntries = new HashMap<>();

    @Override
    public UUID save(CompendiumEntry compendiumEntry) {
        compendiumEntries.put(compendiumEntry.getId(), compendiumEntry);
        return compendiumEntry.getId();
    }

    @Override
    public CompendiumEntry findExisting(UUID compendiumEntryId)  {
        return Optional.of(compendiumEntries.get(compendiumEntryId))
                .orElseThrow(
                        () -> new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id : " + compendiumEntryId.toString())
                );
    }

    @Override
    public void update(UUID compendiumEntryId, CompendiumEntry updatedCompendiumEntry) {
        compendiumEntries.replace(compendiumEntryId, updatedCompendiumEntry);
    }

    @Override
    public void delete(UUID compendiumEntryId) {
        compendiumEntries.remove(compendiumEntryId);
    }
}
