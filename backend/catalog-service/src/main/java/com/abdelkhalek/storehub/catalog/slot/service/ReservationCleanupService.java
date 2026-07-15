package com.abdelkhalek.storehub.catalog.slot.service;

import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Releases capacity held by abandoned checkouts (user submitted a checkout form,
 * never completed payment). Runs frequently since the TTL( see RESERVATION_TTL_MINUTES in
 * SlotBookingService) is short (10 min).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupService {

    private final SlotReservationRepository slotReservationRepository;
    private final DeliverySlotRepository deliverySlotRepository;

    @Scheduled(fixedDelay = 60_000) // every minute
    @Transactional
    public void releaseExpiredReservations() {
        List<SlotReservation> expired = slotReservationRepository
                .findByStatusAndExpiresAtBefore(SlotReservation.Status.RESERVED, LocalDateTime.now());

        for (SlotReservation reservation : expired) {
            deliverySlotRepository.decrementBooking(reservation.getSlotId(), reservation.getStoreId());
            reservation.setStatus(SlotReservation.Status.EXPIRED);
        }
        slotReservationRepository.saveAll(expired);

        if (!expired.isEmpty()) {
            log.info("Released {} expired slot reservation(s)", expired.size());
        }
    }
}

