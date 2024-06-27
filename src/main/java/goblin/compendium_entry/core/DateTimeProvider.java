package goblin.compendium_entry.core;

import java.time.Instant;

public interface DateTimeProvider {
    Instant currentDate();
}
