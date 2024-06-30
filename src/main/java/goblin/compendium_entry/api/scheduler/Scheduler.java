package goblin.compendium_entry.api.scheduler;

import goblin.compendium_entry.core.CompendiumEntryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Scheduler {
    private final CompendiumEntryService compendiumEntryService;

    public Scheduler(CompendiumEntryService compendiumEntryService) {
        this.compendiumEntryService = compendiumEntryService;
    }

    @Scheduled(cron = "${goblin.compendium-entry.cron.cleanup-deleted-compendium-entries}")
    public void cleanupCompendiumEntries() {
        log.trace("Executing scheduled compendium cleanup");
        compendiumEntryService.cleanupDeletedCompendiumEntries();
    }
}
