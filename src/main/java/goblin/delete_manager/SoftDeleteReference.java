package goblin.delete_manager;

import goblin.compendium_entry.EntryType;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
public class SoftDeleteReference {
    private final UUID softDeletedEntryId;
    private final EntryType softDeletedEntryType;
    private final LocalDate softDeleteDate;

    public SoftDeleteReference(UUID softDeletedEntryId, EntryType softDeletedEntryType, LocalDate softDeleteDate) {
        this.softDeletedEntryId = softDeletedEntryId;
        this.softDeletedEntryType = softDeletedEntryType;
        this.softDeleteDate = softDeleteDate;
    }
}
