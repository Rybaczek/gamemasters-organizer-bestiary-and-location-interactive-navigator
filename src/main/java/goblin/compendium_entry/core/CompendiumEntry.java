package goblin.compendium_entry.core;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class CompendiumEntry {

    @EqualsAndHashCode.Include
    private final UUID id;
    private final UUID ownerId;
    private String name;
    private final EntryType entryType;
    private Instant softDeleteDate;

    public CompendiumEntry(UUID ownerId, String name, EntryType entryType) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.name = name;
        this.entryType = entryType;
        this.softDeleteDate = null;
    }

    public Optional<Instant> getSoftDeleteDate() {
        return Optional.ofNullable(softDeleteDate);
    }
}
