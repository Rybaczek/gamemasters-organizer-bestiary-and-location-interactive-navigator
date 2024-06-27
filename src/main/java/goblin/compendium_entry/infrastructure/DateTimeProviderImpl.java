package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class DateTimeProviderImpl implements DateTimeProvider {
    @Override
    public Instant currentDate() {
        return Instant.now();
    }
}
