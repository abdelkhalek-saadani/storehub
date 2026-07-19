package com.abdelkhalek.storehub.catalog.slot.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delivery_slot",
        uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "slot_date", "start_time"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliverySlot {

    public enum Status { OPEN, FULL, CLOSED, CANCELLED }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "slot_config_id", nullable = false)
    private UUID slotConfigId;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "booked_count", nullable = false)
    private Integer bookedCount = 0;

    // Optimistic lock for syncFutureSlots (see SlotConfigService).
    // For other( see SlotBookingService) capacity safety comes from the atomic UPDATE in the
    // repository (see DeliverySlotRepository).
    @Version
    @Column(name = "version")
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    // Owner manually edited this specific occurrence (holiday, promo, etc).
    // Config-sync and generation jobs must never overwrite a row where this is true.
    @Column(name = "is_manual_override", nullable = false)
    private boolean manualOverride = false;
}

