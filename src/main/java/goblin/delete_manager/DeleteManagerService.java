package goblin.delete_manager;

import goblin.compendium_entry.CompendiumEntry;
import goblin.compendium_entry.infrastructure.CompendiumEntryInMemoryRepository;
import goblin.compendium_entry.infrastructure.CompendiumEntryNotFoundException;
import goblin.delete_manager.infrastructure.DeleteManagerInMemoryRepository;
import goblin.delete_manager.infrastructure.DeletedEntryReferenceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteManagerService {

    private final CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository;
    private final DeleteManagerInMemoryRepository deleteManagerInMemoryRepository;

    public DeleteManagerService(CompendiumEntryInMemoryRepository compendiumEntryInMemoryRepository, DeleteManagerInMemoryRepository deleteManagerInMemoryRepository) {
        this.compendiumEntryInMemoryRepository = compendiumEntryInMemoryRepository;
        this.deleteManagerInMemoryRepository = deleteManagerInMemoryRepository;
    }


    public Optional<SoftDeleteReference> find(UUID softDeleteReferenceId) {
        return deleteManagerInMemoryRepository.find(softDeleteReferenceId);
    }

    public SoftDeleteReference findExisting(UUID softDeleteReferenceId) throws DeletedEntryReferenceNotFoundException {
        return deleteManagerInMemoryRepository.findExisting(softDeleteReferenceId);
    }

    public List<SoftDeleteReference> findALl() {
        return deleteManagerInMemoryRepository.findAll();
    }

    @Transactional
    public void restore(UUID softDeleteEntryId) throws CompendiumEntryNotFoundException {
        CompendiumEntry softDeleteEntry = compendiumEntryInMemoryRepository.findSoftDeleted(softDeleteEntryId);
        softDeleteEntry.setIsSoftDeleted(Boolean.FALSE);
        compendiumEntryInMemoryRepository.update(softDeleteEntryId, softDeleteEntry);

        deleteManagerInMemoryRepository.delete(softDeleteEntryId);
    }

    @Transactional
    public UUID markAsSoftDeleted(UUID softDeleteEntryId) throws CompendiumEntryNotFoundException {
        CompendiumEntry softDeleteEntry = compendiumEntryInMemoryRepository.findExisting(softDeleteEntryId);
        softDeleteEntry.setIsSoftDeleted(Boolean.TRUE);
        compendiumEntryInMemoryRepository.update(softDeleteEntryId, softDeleteEntry);

       return deleteManagerInMemoryRepository.save(new SoftDeleteReference(
                softDeleteEntry.getId(),
                softDeleteEntry.getEntryType(),
               LocalDate.now()
                ));
    }

    @Transactional
    public void delete(UUID softDeleteEntryId) {
        deleteManagerInMemoryRepository.delete(softDeleteEntryId);
    }
}
