package com.abdelkhalek.storehub.catalog.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Append-only, same pattern as StockMovement, applied to price changes. */
@Entity
@Table(name = "unit_price_history")
@Getter
@NoArgsConstructor
public class UnitPriceHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal oldPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal newPrice;

    @Column(nullable = false)
    private Instant changedAt;

    public UnitPriceHistory(UUID storeId, UUID productId, BigDecimal oldPrice, BigDecimal newPrice) {
        this.storeId = storeId;
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.changedAt = Instant.now();
    }
}
