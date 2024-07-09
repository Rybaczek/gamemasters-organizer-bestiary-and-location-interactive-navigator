package goblin.compendium_entry.core;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompendiumEntryService {
    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;

    public CompendiumEntryService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository) {
        this.compendiumEntryInMemoryRepository = compendiumEntryInMemoryRepository;
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

    public void delete(UUID compendiumEntryId) {
        compendiumEntryInMemoryRepository.delete(compendiumEntryId);
    }
}
