package com.abdelkhalek.storehub.catalog.slot.service;


import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Materializes bookable delivery_slot rows from the recurring slot_config
 * rules, one rolling day at a time. Never touches a row that already exists
 * (unique constraint on store_id + slot_date + start_time makes this safe
 * to re-run / retry without duplicating slots).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SlotGenerationService {

    private static final int ROLLING_WINDOW_DAYS = 7;

    private final SlotConfigRepository slotConfigRepository;
    private final DeliverySlotRepository deliverySlotRepository;

    // Runs daily just after midnight; extends the window by exactly one day
    // so we always keep ROLLING_WINDOW_DAYS of future slots available.
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void generateDailyWindow() {
        LocalDate targetDate = LocalDate.now().plusDays(ROLLING_WINDOW_DAYS);
        log.info("Generating delivery slots for {}", targetDate);

        // In a real system, iterate distinct tenant_ids (from a Tenant table).
        // Kept generic here since tenant enumeration is outside this module's scope.
        for (Long tenantId : distinctTenantIds()) {
            generateForTenantAndDate(tenantId, targetDate);
        }
    }

    @Transactional
    public void generateForTenantAndDate(UUID storeId, LocalDate targetDate) {
        int dayOfWeek = targetDate.getDayOfWeek().getValue() % 7; // 0=Sun ... 6=Sat

        List<SlotConfig> configs =
                slotConfigRepository.findByStoreIdAndActiveTrueAndDayOfWeek(storeId, dayOfWeek);

        for (SlotConfig config : configs) {
            materializeSlotsForConfig(config, targetDate);
        }
    }

    private void materializeSlotsForConfig(SlotConfig config, LocalDate date) {
        LocalDateTime cursor = LocalDateTime.of(date, config.getStartTime());
        LocalDateTime dayEnd = LocalDateTime.of(date, config.getEndTime());

        while (cursor.plusMinutes(config.getSlotDurationMin()).isBefore(dayEnd.plusSeconds(1))) {
            LocalDateTime slotStart = cursor;
            LocalDateTime slotEnd = cursor.plusMinutes(config.getSlotDurationMin());

            boolean exists = deliverySlotRepository.existsByTenantIdAndSlotDateAndStartTime(
                    config.getTenantId(), date, slotStart);

            if (!exists) {
                DeliverySlot slot = new DeliverySlot();
                slot.setTenantId(config.getTenantId());
                slot.setSlotConfigId(config.getId());
                slot.setSlotDate(date);
                slot.setStartTime(slotStart);
                slot.setEndTime(slotEnd);
                slot.setMaxCapacity(config.getMaxCapacity());
                slot.setBookedCount(0);
                slot.setStatus(DeliverySlot.Status.OPEN);
                slot.setManualOverride(false);
                deliverySlotRepository.save(slot);
            }
            // If it already exists we skip silently - this is what protects
            // manually-overridden or already-booked slots from generation re-runs.

            cursor = slotEnd;
        }
    }

    private java.util.List<Long> distinctTenantIds() {
        // Replace with a real TenantRepository.findAllActiveIds() in production.
        throw new UnsupportedOperationException(
                "Wire this to the store table - stubbed out for this example"); // See if I
        // should keep the store table in order service or move to this service
    }
}

