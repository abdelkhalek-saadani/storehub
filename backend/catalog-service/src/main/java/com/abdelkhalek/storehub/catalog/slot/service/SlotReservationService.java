package com.abdelkhalek.storehub.catalog.slot.service;

import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.repository.SlotReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SlotReservationService {

    private final SlotReservationRepository slotReservationRepository;

    public void setReservationOrderId(UUID reservationId, UUID orderId) {
        Optional<SlotReservation> sROptional =
                slotReservationRepository.findById(reservationId);
        sROptional.ifPresentOrElse(slotReservation -> {
                    slotReservation.setOrderId(orderId);
                    slotReservationRepository.save(slotReservation);
                },
                () -> {
                    log.error("Could not find reservation with id {}, it is linked to the " +
                            "order {}", reservationId, orderId);
                }
        );
    }

}
