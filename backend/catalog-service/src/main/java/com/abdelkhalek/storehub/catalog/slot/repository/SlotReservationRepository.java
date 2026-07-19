package com.abdelkhalek.storehub.catalog.slot.repository;


import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlotReservationRepository extends JpaRepository<SlotReservation, Long> {

    Optional<SlotReservation> findByIdAndStoreId(UUID id, UUID storeId);

    List<SlotReservation> findByStatusAndExpiresAtBefore(
            SlotReservation.Status status, LocalDateTime cutoff);
}
