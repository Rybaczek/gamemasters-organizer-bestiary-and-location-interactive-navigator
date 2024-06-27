package goblin.compendium_entry.core;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import goblin.compendium_entry.infrastructure.DateTimeProviderImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompendiumEntryService {
    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;
    private final DateTimeProviderImpl dateTimeProvider;

    public CompendiumEntryService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository, DateTimeProviderImpl dateTimeProvider) {
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

    public void delete(UUID compendiumEntryId) {
        compendiumEntryInMemoryRepository.delete(compendiumEntryId);
    }
}
