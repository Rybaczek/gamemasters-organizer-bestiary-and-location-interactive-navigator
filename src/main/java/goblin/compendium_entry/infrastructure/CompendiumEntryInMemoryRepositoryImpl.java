package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.CompendiumEntry;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CompendiumEntryInMemoryRepositoryImpl implements CompendiumEntryInMemoryRepository {

    private final Map<UUID, CompendiumEntry> compendiumEntries = new HashMap<>();

    @Override
    public UUID save(CompendiumEntry compendiumEntry) {
        compendiumEntries.put(compendiumEntry.getId(), compendiumEntry);
        return compendiumEntry.getId();
    }

    @Override
    public Optional<CompendiumEntry> findById(UUID compendiumEntryId) {
        return Optional.ofNullable(compendiumEntries.get(compendiumEntryId));
    }

    @Override
    public CompendiumEntry findExisting(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return Optional.ofNullable(compendiumEntries.get(compendiumEntryId))
                .orElseThrow(() -> new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id " + compendiumEntryId));
    }

    @Override
    public List<CompendiumEntry> findAll() {
        return compendiumEntries.values().stream().toList();
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
