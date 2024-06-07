package goblin.compendium_entry;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        return compendiumEntryInMemoryRepository.findById(compendiumEntryId).orElseThrow(() ->
                new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id " + compendiumEntryId)
        );
    }

    public List<CompendiumEntry> findALl() {
        return compendiumEntryInMemoryRepository.findAll();
    }

    @Transactional
    public void update(UUID compendiumEntryId, CompendiumEntry updatedEntry) throws CompendiumEntryNotFoundException {
        CompendiumEntry entryToUpdate = compendiumEntryInMemoryRepository.findExisting(compendiumEntryId);
        entryToUpdate.setName(updatedEntry.getName());
        compendiumEntryInMemoryRepository.update(compendiumEntryId,updatedEntry);
    }

    @Transactional
    public void delete(UUID compendiumEntryId) {
        compendiumEntryInMemoryRepository.delete(compendiumEntryId);
    }
}
