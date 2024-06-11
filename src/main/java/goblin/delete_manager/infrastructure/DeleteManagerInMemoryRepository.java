package goblin.delete_manager.infrastructure;

import goblin.delete_manager.SoftDeleteReference;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeleteManagerInMemoryRepository {

    UUID save(SoftDeleteReference softDeleteReference);

    Optional<SoftDeleteReference> find(UUID softDeleteReferenceId);

    SoftDeleteReference findExisting(UUID softDeleteReferenceId) throws DeletedEntryReferenceNotFoundException;

    List<SoftDeleteReference> findAll();

    void delete(UUID softDeleteReferenceId);
}
