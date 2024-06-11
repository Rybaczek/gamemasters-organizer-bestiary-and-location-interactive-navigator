package goblin.delete_manager.infrastructure;

public class DeletedEntryReferenceNotFoundException extends Exception{
    public DeletedEntryReferenceNotFoundException(String message) {
        super(message);
    }
}
