package com.abdelkhalek.storehub.catalog.inventory.service;

import com.abdelkhalek.storehub.catalog.inventory.entity.Reservation;
import com.abdelkhalek.storehub.catalog.inventory.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository reservationRepository;

    /**
     * Called when order created event is received, It links the reservations to their order
     * through assigning the {@code orderId}
     * @param reservationIds Ids of the reservation that will be linked to their order
     * @param orderId The order where the reservations belong
     */
    @Transactional
    public void setReservationsOrderId(List<UUID> reservationIds, UUID orderId) {
        Optional<Reservation> rOptional;
        for (UUID retainId : reservationIds) {
            rOptional = reservationRepository.findById(retainId);
            rOptional.ifPresentOrElse(reservation -> {
                        reservation.setOrderId(orderId);
                        reservationRepository.save(reservation);
                    },
                    () -> {
                        log.error("Could not find reservation with id {}, it is linked to the " +
                                "order {}", retainId, orderId);
                    });

        }
    }
}
