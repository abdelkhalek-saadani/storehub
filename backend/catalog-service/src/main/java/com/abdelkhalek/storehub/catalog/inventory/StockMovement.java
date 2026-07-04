package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.inventory.enums.MovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Append-only ledger. Never updated after insert - this is what makes it
 * an audit trail. Every physical change to Stock.quantityOnHand should have
 * a matching row here, so "why is on-hand 5 instead of 10" is always answerable.
 */
@Entity
@Table(name = "stock_movements")
@Getter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    // Signed delta applied to quantityOnHand: positive for NEW_BATCH,
    // negative for DEDUCT and adjustDown/Up-style ADJUSTMENT.
    @Column(nullable = false)
    private int quantityDelta;

    // orderId for DEDUCT, null reference for NEW_BATCH and ADJUSTMENT
    private UUID referenceId;

    private String note;

    @Column(nullable = false)
    private Instant createdAt;

    public StockMovement(UUID storeId, UUID productId, MovementType type,
                          int quantityDelta, UUID referenceId, String note) {
        this.storeId = storeId;
        this.productId = productId;
        this.type = type;
        this.quantityDelta = quantityDelta;
        this.referenceId = referenceId;
        this.note = note;
        this.createdAt = Instant.now();
    }
}
