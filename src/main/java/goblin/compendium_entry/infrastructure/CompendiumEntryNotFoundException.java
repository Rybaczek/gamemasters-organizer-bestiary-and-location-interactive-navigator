package goblin.compendium_entry.infrastructure;

public class CompendiumEntryNotFoundException extends RuntimeException {
    public CompendiumEntryNotFoundException(String message) {
        super(message);
    }
}
