package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.DateTimeProvider;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Primary
@TestComponent
public class FixedDateTimeProvider implements DateTimeProvider {
    private Clock clock = Clock.fixed(Instant.parse("2024-06-20T09:20:00.000Z"), ZoneId.of("Europe/Warsaw"));

    @Override
    public Instant currentDate() {
        return Instant.now(clock);
    }

    public void setClockTime(Instant time){
        clock = Clock.fixed(time,ZoneId.of("Europe/Warsaw"));
    }
}
