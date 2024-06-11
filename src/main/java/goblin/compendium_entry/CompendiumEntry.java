package goblin.compendium_entry;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class CompendiumEntry {

    @EqualsAndHashCode.Include
    private final UUID id;
    private String name;
    private EntryType entryType;
    private Boolean isSoftDeleted;

    public CompendiumEntry(String name, EntryType entryType) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.entryType = entryType;
        this.isSoftDeleted = Boolean.FALSE;

    }
}