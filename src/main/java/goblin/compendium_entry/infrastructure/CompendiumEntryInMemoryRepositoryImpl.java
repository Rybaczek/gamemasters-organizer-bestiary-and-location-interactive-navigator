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
        Optional<CompendiumEntry> entry = Optional.ofNullable(compendiumEntries.get(compendiumEntryId));

        if (entry.isPresent() && entry.get().getIsSoftDeleted().equals(Boolean.TRUE)) {
            return Optional.empty();
        }
        return Optional.ofNullable(compendiumEntries.get(compendiumEntryId));
    }

    @Override
    public CompendiumEntry findExisting(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return findById(compendiumEntryId).orElseThrow(() ->
                new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id " + compendiumEntryId)
        );
    }

    @Override
    public Optional<CompendiumEntry> findSoftDeletedById(UUID compendiumEntryId) {
        return Optional.ofNullable(compendiumEntries.get(compendiumEntryId));
    }
    @Override
    public CompendiumEntry findSoftDeleted(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return findSoftDeletedById(compendiumEntryId).orElseThrow(() ->
                new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id " + compendiumEntryId)
        );
    }

    @Override
    public List<CompendiumEntry> findAll() {
        return compendiumEntries.values()
                .stream()
                .filter(compendiumEntry -> compendiumEntry.getIsSoftDeleted().equals(Boolean.FALSE))
                .toList();
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
