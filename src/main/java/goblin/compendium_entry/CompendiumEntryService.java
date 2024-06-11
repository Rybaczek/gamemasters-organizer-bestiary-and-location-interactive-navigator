package goblin.compendium_entry;

import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import goblin.delete_manager.DeleteManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CompendiumEntryService {
    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;
    private final DeleteManagerService deleteManagerService;

    public CompendiumEntryService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository, DeleteManagerService deleteManagerService) {
        this.compendiumEntryInMemoryRepository = compendiumEntryInMemoryRepository;
        this.deleteManagerService = deleteManagerService;
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

    public CompendiumEntry findSoftDeleted(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return compendiumEntryInMemoryRepository.findSoftDeletedById(compendiumEntryId).orElseThrow(() ->
                new CompendiumEntryNotFoundException("Couldn't find a Compendium Entry with id " + compendiumEntryId)
        );
    }

    public List<CompendiumEntry> findALl() {
        return compendiumEntryInMemoryRepository.findAll();
    }

    @Transactional
    public void update(UUID compendiumEntryId, CompendiumEntry dataToUpdate) throws CompendiumEntryNotFoundException {
        CompendiumEntry entryToUpdate = compendiumEntryInMemoryRepository.findExisting(compendiumEntryId);
        entryToUpdate.setName(dataToUpdate.getName());
        entryToUpdate.setEntryType(dataToUpdate.getEntryType());
        compendiumEntryInMemoryRepository.update(compendiumEntryId, entryToUpdate);
    }

    @Transactional
    public UUID softDelete(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        return deleteManagerService.markAsSoftDeleted(compendiumEntryId);
    }

    public void restore(UUID compendiumEntryId) throws CompendiumEntryNotFoundException {
        deleteManagerService.restore(compendiumEntryId);
    }

    @Transactional
    public void hardDelete(UUID compendiumEntryId) {
        compendiumEntryInMemoryRepository.delete(compendiumEntryId);
    }
}
