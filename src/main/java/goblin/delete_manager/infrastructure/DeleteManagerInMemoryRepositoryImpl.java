package goblin.delete_manager.infrastructure;

import goblin.delete_manager.SoftDeleteReference;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DeleteManagerInMemoryRepositoryImpl implements DeleteManagerInMemoryRepository {

    private final Map<UUID, SoftDeleteReference> softDeleteReferences = new HashMap<>();

    @Override
    public UUID save(SoftDeleteReference softDeleteReference) {
        softDeleteReferences.put(softDeleteReference.getSoftDeletedEntryId(), softDeleteReference);
        return softDeleteReference.getSoftDeletedEntryId();
    }

    @Override
    public SoftDeleteReference findExisting(UUID softDeleteReferenceId) throws DeletedEntryReferenceNotFoundException {
        return Optional.ofNullable(softDeleteReferences.get(softDeleteReferenceId))
                .orElseThrow(() -> new DeletedEntryReferenceNotFoundException("Couldn't find a Deleted entry with id " + softDeleteReferenceId));
    }

    @Override
    public Optional<SoftDeleteReference> find(UUID softDeleteReferenceId) {
        return Optional.ofNullable(softDeleteReferences.get(softDeleteReferenceId));
    }

    @Override
    public List<SoftDeleteReference> findAll() {
        return softDeleteReferences.values().stream().toList();
    }

    @Override
    public void delete(UUID softDeleteReferenceId) {
        softDeleteReferences.remove(softDeleteReferenceId);
    }
}
