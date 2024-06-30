package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;
import goblin.compendium_entry.core.DateTimeProvider;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;

/**
 * The in memory repository is a temporary solution that gives me more time to decide
 * which database to use while still developing main goal of a project
 */
@Repository
public class CompendiumEntryRepositoryImpl implements CompendiumEntryRepository {

    protected final Map<UUID, CompendiumEntry> compendiumEntries = new HashMap<>();
    private final DateTimeProvider dateTimeProvider;

    public CompendiumEntryRepositoryImpl(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public UUID save(CompendiumEntry compendiumEntry) {
        compendiumEntries.put(compendiumEntry.getId(), compendiumEntry);
        return compendiumEntry.getId();
    }

    @Override
    public CompendiumEntry findExisting(UUID compendiumEntryId) {
        return Optional.of(compendiumEntries.get(compendiumEntryId)).filter(compendiumEntry -> compendiumEntry.getSoftDeleteDate().isEmpty())
                .orElseThrow(
                        () -> new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id : " + compendiumEntryId.toString())
                );
    }

    @Override
    public CompendiumEntry findSoftDeletedById(UUID compendiumEntryId) {
        return Optional.ofNullable(compendiumEntries.get(compendiumEntryId))
                .orElseThrow(
                        () -> new CompendiumEntryNotFoundException("Couldn't find a soft deleted Compendium Entry with id : " + compendiumEntryId.toString())
                );
    }

    @Override
    public List<CompendiumEntry> findAllExceedingSoftDeleteDate(Instant date) {
        return compendiumEntries.values().stream()
                .filter(exceedingSoftDeletedDate(date))
                .toList();
    }

    @Override
    public List<CompendiumEntry> findByOwnerId(UUID ownerId) {
        return compendiumEntries.values().stream()
                .filter(compendiumEntry -> compendiumEntry.getOwnerId().equals(ownerId))
                .filter(CompendiumEntry::isActive)
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

    private static Predicate<CompendiumEntry> exceedingSoftDeletedDate(Instant date) {
        return compendiumEntry -> compendiumEntry.getSoftDeleteDate()
                .map(softDeletedDate -> softDeletedDate.isBefore(date))
                .orElse(false);
    }
}
