package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.DateTimeProvider;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

@Primary
@TestComponent
public class CompendiumEntryTestRepository extends CompendiumEntryRepositoryImpl {

    public CompendiumEntryTestRepository(DateTimeProvider dateTimeProvider) {
        super(dateTimeProvider);
    }

    public void clean() {
        compendiumEntries.clear();
    }

}