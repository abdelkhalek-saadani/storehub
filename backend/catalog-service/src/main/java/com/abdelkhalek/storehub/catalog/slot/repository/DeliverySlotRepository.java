package com.abdelkhalek.storehub.catalog.slot.repository;

import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliverySlotRepository extends JpaRepository<DeliverySlot, UUID> {

    Optional<DeliverySlot> findByIdAndStoreId(UUID id, UUID tenantId);

    List<DeliverySlot> findByStoreIdAndSlotDateBetweenAndStatus(
            UUID storeId, LocalDate from, LocalDate to, DeliverySlot.Status status);

    Boolean existsByStoreIdAndSlotDateAndStartTime(UUID storeId, LocalDate slotDate,
                                                   LocalDateTime startTime);

    DeliverySlot findByStoreIdAndSlotDateAndStartTime(UUID storeId, LocalDate slotDate,
                                                      LocalDateTime startTime);

    // --- Atomic capacity increment ---
    // The WHERE clause (bookedCount < maxCapacity) is what actually prevents overbooking
    // under concurrency - two simultaneous requests can't both succeed past the limit,
    // because the DB evaluates the predicate per-row at UPDATE time (row lock), not
    // based on a stale value read earlier in application code.
    // If the query complexity grow over time, or modifying it become cumbersome, move this to
    // the service layer and use the version column protection for concurrency
    @Modifying
    @Query("""
            UPDATE DeliverySlot d
            SET d.bookedCount = d.bookedCount + 1,
                d.status = CASE WHEN d.bookedCount + 1 >= d.maxCapacity THEN 'FULL' ELSE d.status END
            WHERE d.id = :slotId
              AND d.storeId = :storeId
              AND d.bookedCount < d.maxCapacity
              AND d.status = 'OPEN'
            """)
    int tryIncrementBooking(@Param("slotId") UUID slotId, @Param("storeId") UUID storeId);

    // --- Atomic capacity release (on reservation expiry/cancel) ---
    @Modifying
    @Query("""
            UPDATE DeliverySlot d
            SET d.bookedCount = d.bookedCount - 1,
                d.status = CASE WHEN d.status = 'FULL' THEN 'OPEN' ELSE d.status END
            WHERE d.id = :slotId
              AND d.storeId = :storeId
              AND d.bookedCount > 0
            """)
    int decrementBooking(@Param("slotId") UUID slotId, @Param("storeId") UUID storeId);

    // Candidates safe for config-sync: future, untouched by owner, no bookings yet.
    List<DeliverySlot> findBySlotConfigIdAndStoreIdAndSlotDateGreaterThanEqualAndManualOverrideFalseAndBookedCount(
            UUID slotConfigId, UUID storeId, LocalDate fromDate, Integer bookedCount);
}
