package goblin.compendium_entry.core;

import goblin.compendium_entry.infrastructure.CompendiumEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompendiumEntryService {
    private final CompendiumEntryRepository compendiumEntryRepository;
    private final DateTimeProvider dateTimeProvider;

    @Value("${goblin.compendium-entry.days-to-hard-delete}")
    private Integer softDeletedThresholdDate;

    public UUID save(CompendiumEntry compendiumEntry) {
        return compendiumEntryRepository.save(compendiumEntry);
    }

    public CompendiumEntry find(UUID compendiumEntryId) {
        return compendiumEntryRepository.findExisting(compendiumEntryId);
    }

    public void update(UUID compendiumEntryId, CompendiumEntryDto compendiumEntryDto) {
        CompendiumEntry compendiumEntry = find(compendiumEntryId);
        compendiumEntry.setName(compendiumEntryDto.name());
        compendiumEntryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void markForSoftDelete(UUID compendiumEntryId) {
        CompendiumEntry compendiumEntry = compendiumEntryRepository.findExisting(compendiumEntryId);
        compendiumEntry.markForSoftDelete(dateTimeProvider);
        compendiumEntryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void restoreSoftDeleted(UUID compendiumEntryId) {
        CompendiumEntry compendiumEntry = compendiumEntryRepository.findSoftDeletedById(compendiumEntryId);
        compendiumEntry.restoreFromSoftDelete();
        compendiumEntryRepository.update(compendiumEntryId, compendiumEntry);
    }

    public void cleanupDeletedCompendiumEntries() {
        Instant softDeleteThresholdDate = dateTimeProvider.currentDate().minus(softDeletedThresholdDate, ChronoUnit.DAYS);

        compendiumEntryRepository.findAllExceedingSoftDeleteDate(softDeleteThresholdDate)
                .forEach(compendiumEntry -> {
                    log.trace("Hard deleting entry {}", compendiumEntry.getId());
                    compendiumEntryRepository.delete(compendiumEntry.getId());
                });
    }
}
