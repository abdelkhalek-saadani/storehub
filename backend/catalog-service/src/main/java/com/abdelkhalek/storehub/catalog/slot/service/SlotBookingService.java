package com.abdelkhalek.storehub.catalog.slot.service;


import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.exception.SlotUnavailableException;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlotBookingService {

    private static final int RESERVATION_TTL_MINUTES = 10;

    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotReservationRepository slotReservationRepository;

    /**
     * Called when the user submit checkout form, by the order-creation flow.
     * The atomic UPDATE (tryIncrementBooking) is what actually enforces
     * capacity under concurrency - if two users race for the last spot,
     * only one UPDATE affects a row (rowsAffected == 1); the loser gets 0
     * and we throw immediately.
     */
    @Transactional
    public SlotReservation reserveSlot(UUID storeId, UUID slotId, UUID cartId) {
        DeliverySlot slot = deliverySlotRepository.findByIdAndStoreId(slotId, storeId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + slotId));

        // The atomic update already handle the status constraint, These tests exist just as
        // first protection
        if (slot.getStatus() == DeliverySlot.Status.CLOSED
                || slot.getStatus() == DeliverySlot.Status.CANCELLED) {
            throw new SlotUnavailableException("Slot is not open for booking");
        }

        if (slot.getStatus() == DeliverySlot.Status.FULL) {
            throw new SlotUnavailableException("Slot is full");
        }

        int rowsAffected = deliverySlotRepository.tryIncrementBooking(slotId, storeId);
        if (rowsAffected == 0) {
            throw new SlotUnavailableException("Slot is full or no longer available");
        }

        SlotReservation reservation = new SlotReservation();
        reservation.setStoreId(storeId);
        reservation.setSlotId(slotId);
        reservation.setCartId(cartId);
        reservation.setStatus(SlotReservation.Status.RESERVED);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TTL_MINUTES));

        return slotReservationRepository.save(reservation);
    }

    /**
     * Called by the order-creation flow once payment(or the order) is confirmed.
     */
    @Transactional
    public void confirmReservation(UUID storeId, UUID reservationId, UUID orderId) {
        SlotReservation reservation = slotReservationRepository.findByIdAndStoreId(reservationId,
                        storeId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() != SlotReservation.Status.RESERVED) {
            throw new SlotUnavailableException("Reservation is not in a confirmable state");
        }
        reservation.setStatus(SlotReservation.Status.CONFIRMED);
        reservation.setOrderId(orderId);
        slotReservationRepository.save(reservation);
        // bookedCount stays incremented - the slot capacity was already committed at reserve time.
    }

    /**
     * Called on order-creation flow rollback, explicit cancellation or payment abandonment.
     */
    @Transactional
    public void releaseReservation(UUID storeId, UUID reservationId) {
        SlotReservation reservation = slotReservationRepository.findByIdAndStoreId(reservationId,
                        storeId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found: " + reservationId));

        if (reservation.getStatus() == SlotReservation.Status.RELEASED
                || reservation.getStatus() == SlotReservation.Status.EXPIRED) {
            return; // already released, idempotent no-op
        }

        deliverySlotRepository.decrementBooking(reservation.getSlotId(), storeId);
        reservation.setStatus(SlotReservation.Status.RELEASED);
        slotReservationRepository.save(reservation);
    }
}

