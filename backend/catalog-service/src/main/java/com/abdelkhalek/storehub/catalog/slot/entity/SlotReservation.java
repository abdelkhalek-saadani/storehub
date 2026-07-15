package com.abdelkhalek.storehub.catalog.slot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "slot_reservation")
@Getter
@Setter
public class SlotReservation {

    public enum Status {RESERVED, CONFIRMED, RELEASED, EXPIRED}

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "slot_id", nullable = false)
    private UUID slotId;

    // Ties the reservation to the cart during checkout; once the order is
    // created this is set so confirmReservation() can look it up by order.
    // TODO: remove this, look ReserveSlotRequest for why
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "order_id")
    private UUID orderId;  // This won't be set at reservation creation, It will after order does

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.RESERVED;

    @Column(name = "reserved_at", nullable = false)
    private LocalDateTime reservedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}

