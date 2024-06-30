package goblin.compendium_entry.core;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CompendiumEntryService {
    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;

    public CompendiumEntryService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository) {
        this.compendiumEntryInMemoryRepository = compendiumEntryInMemoryRepository;
    }

    @Transactional
    public UUID save(CompendiumEntry compendiumEntry) {
        return compendiumEntryInMemoryRepository.save(compendiumEntry);
    }

    public CompendiumEntry find(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return compendiumEntryInMemoryRepository.findExisting(compendiumEntryId);
    }

    @Transactional
    public void update(UUID compendiumEntryId, CompendiumEntry updatedEntry) {
        compendiumEntryInMemoryRepository.update(compendiumEntryId, updatedEntry);
    }

    @Transactional
    public void delete(UUID compendiumEntryId) {
        compendiumEntryInMemoryRepository.delete(compendiumEntryId);
    }
}
