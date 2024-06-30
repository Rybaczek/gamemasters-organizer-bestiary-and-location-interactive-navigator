package goblin.compendium_entry.core;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class CompendiumEntryService {
    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;
    private final DateTimeProvider dateTimeProvider;

    @Value("${goblin.compendium-entry.days-to-hard-delete}")
    private Integer daysToHardDelete;

    public CompendiumEntryService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository, DateTimeProvider dateTimeProvider) {
        this.compendiumEntryInMemoryRepository = compendiumEntryInMemoryRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    public UUID save(CompendiumEntry compendiumEntry) {
        return compendiumEntryInMemoryRepository.save(compendiumEntry);
    }

    public CompendiumEntry find(UUID compendiumEntryId) {
        return compendiumEntryInMemoryRepository.findExisting(compendiumEntryId);
    }

    public void update(UUID compendiumEntryId, CompendiumEntryDto compendiumEntryDto) {
        CompendiumEntry compendiumEntry = find(compendiumEntryId);
        compendiumEntry.setName(compendiumEntryDto.name());
        compendiumEntryInMemoryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void markForSoftDelete(UUID compendiumEntryId) {
        CompendiumEntry compendiumEntry = compendiumEntryInMemoryRepository.findExisting(compendiumEntryId);

        compendiumEntry.setSoftDeleteDate(dateTimeProvider.currentDate());
        compendiumEntryInMemoryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void restoreSoftDeleted(UUID compendiumEntryId) {
        CompendiumEntry compendiumEntry = compendiumEntryInMemoryRepository.findSoftDeletedById(compendiumEntryId);

        compendiumEntry.setSoftDeleteDate(null);
        compendiumEntryInMemoryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void cleanupDeletedCompendiumEntries() {
        Instant softDeletedEntryDeleteDate = dateTimeProvider.currentDate().minus(daysToHardDelete, ChronoUnit.DAYS);

        compendiumEntryInMemoryRepository.findAllExceedingSoftDeleteDate(softDeletedEntryDeleteDate)
                .forEach(compendiumEntry ->
                        compendiumEntryInMemoryRepository.delete(compendiumEntry.getId())
                );
    }
}
