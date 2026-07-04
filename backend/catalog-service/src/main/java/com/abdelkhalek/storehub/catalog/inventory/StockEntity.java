package com.abdelkhalek.storehub.catalog.inventory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One row per (storeId, productId).
 *
 * Concurrency: @Version gives optimistic locking. Two concurrent checkouts
 * reading the same row will not both succeed - the second writer gets an
 * ObjectOptimisticLockingFailureException and the caller must retry the
 * whole transaction (see StockService).
 */
@Entity
@Table(name = "stock", uniqueConstraints = @UniqueConstraint(columnNames = {"storeId", "productId"}))
@Getter
@NoArgsConstructor
public class StockEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantityOnHand;

    @Column(nullable = false)
    private int quantityReserved;

    @Column(nullable = false)
    private int quantityAvailable;

    @Version
    private long version;

    @Column
    private LocalDate expiryDate;

}
