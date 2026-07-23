package com.abdelkhalek.storehub.catalog.inventory.entity;

import com.abdelkhalek.storehub.catalog.inventory.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Mutable lifecycle entity (unlike StockMovement, which is append-only).
 * A reservation moves through ACTIVE -> {CONFIRMED | RELEASED | EXPIRED}.
 * Transition timestamps are kept on the row itself, for now
 * doesn't need a second ledger just for reservation state history.
 */
@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    @Setter
    @Column(nullable = true)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant resolvedAt; // set when leaving ACTIVE, whatever the outcome

    public Reservation(UUID storeId, UUID orderId, UUID productId, int quantity, Instant expiresAt) {
        this.storeId = storeId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = ReservationStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
        this.resolvedAt = Instant.now();
    }

    public void release() {
        this.status = ReservationStatus.RELEASED;
        this.resolvedAt = Instant.now();
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
        this.resolvedAt = Instant.now();
    }
}
