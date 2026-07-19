package com.abdelkhalek.storehub.catalog.slot.service;


import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotConfigRepository;
import com.abdelkhalek.storehub.catalog.store.StoreShadow;
import com.abdelkhalek.storehub.catalog.store.StoreShadowRepository;
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
    private final StoreShadowRepository storeShadowRepository;

    // Runs daily just after midnight; extends the window by exactly one day
    // so we always keep ROLLING_WINDOW_DAYS of future slots available.
    @Scheduled(cron = "0 10 0 * * *")
    @Transactional
    public void generateDailyWindow() {
        LocalDate targetDate = LocalDate.now().plusDays(ROLLING_WINDOW_DAYS);
        log.info("Generating delivery slots for {}", targetDate);


        for (UUID storeId : storeIds()) {
            generateForTenantAndDate(storeId, targetDate);
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

            boolean exists = deliverySlotRepository.existsByStoreIdAndSlotDateAndStartTime(
                    config.getStoreId(), date, slotStart);

            if (!exists) {
                DeliverySlot slot = DeliverySlot.builder()
                        .storeId(config.getStoreId())
                        .slotConfigId(config.getId())
                        .slotDate(date)
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .maxCapacity(config.getMaxCapacity())
                        .bookedCount(0)
                        .status(DeliverySlot.Status.OPEN)
                        .manualOverride(false)
                        .build();
                deliverySlotRepository.save(slot);
            } else {
                // If it already exists we skip silently, this is what protects
                // manually-overridden or already-booked slots from generation re-runs.
                log.debug("Slot already exists: {}, skipping generation for this slot", slotStart);
            }

            cursor = slotEnd;
        }
    }

    private java.util.List<UUID> storeIds() {
        return storeShadowRepository.findAllByStatus("ACTIVE").stream().map(StoreShadow::getId)
                .toList();

    }
}

