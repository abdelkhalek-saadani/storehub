package com.abdelkhalek.storehub.catalog.slot.service;

import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotConfigService {

    private final SlotConfigRepository slotConfigRepository;
    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotGenerationService slotGenerationService;

    public SlotConfig create(SlotConfig config) {
        SlotConfig slotConfig = slotConfigRepository.save(config);
        slotGenerationService.generateForConfigAcrossWindow(slotConfig);
        return slotConfig;
    }

    /**
     * Updates the rule AND propagates the change to already-generated future
     * slots - but only the ones it's safe to touch:
     * - not manually overridden by the owner
     * - not yet booked (bookedCount == 0)
     * Booked or manually-edited slots are left exactly as they are.
     * <p>
     * Done synchronously in the same transaction: the rolling window is small
     * (days, not years) so this is a handful to a few hundred rows - no need
     * for an async job at this scale.
     * TODO: add retry logic (see StockService)
     */
    @Transactional
    public SlotConfig update(UUID configId, UUID storeId, SlotConfig updated) {
        SlotConfig existing = slotConfigRepository.findByIdAndStoreId(configId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Slot config not found: " + configId));

        LocalTime oldStartTime = existing.getStartTime();

        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setSlotDurationMin(updated.getSlotDurationMin());
        existing.setMaxCapacity(updated.getMaxCapacity());
        existing.setCutoffMinutes(updated.getCutoffMinutes());
        existing.setExtraFee(updated.getExtraFee());
        existing.setActive(updated.isActive());
        slotConfigRepository.save(existing);

        syncFutureSlots(existing, oldStartTime);
        return existing;
    }

    private void syncFutureSlots(SlotConfig config, LocalTime oldStartTime) {
        List<DeliverySlot> safeToUpdate = deliverySlotRepository
                .findBySlotConfigIdAndStoreIdAndSlotDateGreaterThanEqualAndManualOverrideFalseAndBookedCount(
                        config.getId(), config.getStoreId(), LocalDate.now(), 0);

        deliverySlotRepository.deleteAll(safeToUpdate);

        int generatedSlotsCount = slotGenerationService.generateForConfigAcrossWindow(config);

        // No longer Needed
/*        int durationMin = config.getSlotDurationMin();

        for (DeliverySlot slot : safeToUpdate) {
            // Re-derive this slot's position within the day rather than assuming
            // slot index alignment, safest for simple 1:1 rebuild after edits.
            slot.setStartTime(recalculateStart(slot, config, oldStartTime));
            slot.setEndTime(slot.getStartTime().plusMinutes(durationMin));
            slot.setMaxCapacity(config.getMaxCapacity());
        }
        deliverySlotRepository.saveAll(safeToUpdate);*/

        log.info("Deleted {} slot(s) out of sync for config {} and added {} slot(s) after update",
                safeToUpdate.size(),
                config.getId(),
                generatedSlotsCount);
    }

    // No longer needed
    // Keeps the slot's relative position in the day if duration changed,
    // otherwise this is a straightforward same-slot-index recompute.
    private LocalDateTime recalculateStart(DeliverySlot slot, SlotConfig config, LocalTime oldStartTime) {
        log.debug("Recalculating start time for slot {} for config {}", slot, config);
        LocalDateTime dayStart = LocalDateTime.of(slot.getSlotDate(), config.getStartTime());
        LocalDateTime oldDayStart = LocalDateTime.of(slot.getSlotDate(), oldStartTime);
        log.debug("Day start: {}", dayStart);
        long originalDurationMin = Duration.between(slot.getStartTime(), slot.getEndTime())
                .toMinutes();
        if (originalDurationMin <= 0) originalDurationMin = config.getSlotDurationMin();
        long minutesFromDayStart = Duration.between(oldDayStart, slot.getStartTime()).toMinutes();
        log.debug("Minutes from day start: {}", minutesFromDayStart);
        long index = minutesFromDayStart / Math.max(originalDurationMin, 1);
        LocalDateTime startTime = dayStart.plusMinutes(index * config.getSlotDurationMin());
        log.debug("New index {}, new start time {}", index, startTime);
        return startTime;
    }
}
