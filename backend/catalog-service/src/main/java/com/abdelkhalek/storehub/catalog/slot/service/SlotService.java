package com.abdelkhalek.storehub.catalog.slot.service;

import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final DeliverySlotRepository deliverySlotRepository;

    public String extractSlotLabel(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) return null;
        String sm = startTime.getMinute() > 9 ? "" + startTime.getMinute() :
                "0" + startTime.getMinute();
        String s = startTime.getHour() + "h:" + sm;
        String em = endTime.getMinute() > 9 ? "" + endTime.getMinute() :
                "0" + endTime.getMinute();
        String e = endTime.getHour() + "h:" + em;
        return s + "-" + e;
    }

    /**
     * Returns the dates between {@code from} and {@code to} (inclusive) that have
     * active, available delivery slots.
     *
     * <p>If {@code from} is today, only the first day's slots are filtered by
     * the current time; all subsequent days include any available slot.</p>
     *
     * @param storeId the store identifier
     * @param from the start date (defaults to today if {@code null})
     * @param to the end date (defaults to 7 days from today if {@code null})
     * @return a list of dates with at least one available delivery slot
     */
    public List<LocalDate> checkDays(UUID storeId, LocalDate from, LocalDate to) {
        if (from == null) from = LocalDate.now();
        if (to == null) to = LocalDate.now().plusDays(7);
        List<LocalDate> dates = new ArrayList<>();
        LocalDate cursor = from;
        while (cursor.isBefore(to.plusDays(1))) {
            if (from.isEqual(LocalDate.now()) && cursor.isEqual(from)) {
                if (deliverySlotRepository.existsByStoreIdAndSlotDateAndStartTimeAfter(storeId,
                        cursor, LocalDateTime.now()))
                    dates.add(cursor);
            } else {
                if (deliverySlotRepository.existsByStoreIdAndSlotDate(storeId, cursor))
                    dates.add(cursor);
            }
            cursor = cursor.plusDays(1);
        }
        return dates;
    }

}
