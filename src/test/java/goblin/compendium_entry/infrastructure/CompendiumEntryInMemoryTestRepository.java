package goblin.compendium_entry.infrastructure;

import goblin.compendium_entry.core.CompendiumEntry;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestComponent
@Primary
public class CompendiumEntryInMemoryTestRepository extends CompendiumEntryInMemoryRepositoryImpl {

    public List<CompendiumEntry> findAll() {
        return compendiumEntries.values().stream().toList();
    }

    public void clean() {
        compendiumEntries.clear();
    }
}
