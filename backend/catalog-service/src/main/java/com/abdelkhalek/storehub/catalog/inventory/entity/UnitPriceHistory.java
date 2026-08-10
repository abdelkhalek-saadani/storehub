package com.abdelkhalek.storehub.catalog.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Not;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


/**
 * <b>Note: </b>Placeholder for future unit price history feature.
 * Not currently used/imported anywhere in the codebase.
 * <p>
 * Append-only, same pattern as StockMovement, applied to price changes.
 */
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

}
