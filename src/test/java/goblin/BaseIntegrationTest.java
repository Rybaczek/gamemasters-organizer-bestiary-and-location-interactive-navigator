package goblin;

import goblin.compendium_entry.infrastructure.CompendiumEntryTestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GamemastersOrganizerBestiaryAndLocationInteractiveNavigatorApplication.class)
public abstract class BaseIntegrationTest {

    @Autowired
    private CompendiumEntryTestRepository compendiumEntryInMemoryTestRepository;

    @BeforeEach
    public void cleanInMemoryDatabaseEach() {
        compendiumEntryInMemoryTestRepository.clean();
    }
}
