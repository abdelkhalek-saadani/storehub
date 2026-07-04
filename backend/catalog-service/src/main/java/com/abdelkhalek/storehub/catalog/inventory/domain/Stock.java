package com.abdelkhalek.storehub.catalog.inventory.domain;

import com.abdelkhalek.storehub.catalog.inventory.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 *
 * Invariant: quantityAvailable = quantityOnHand - quantityReserved.
 * This class is the ONLY place allowed to mutate the three quantity fields,
 * so the invariant can't drift no matter which service method calls it.
 *
 */

@Getter
@NoArgsConstructor
public class Stock {

    private UUID storeId;

    private UUID productId;

    private int quantityOnHand;

    private int quantityReserved;

    private int quantityAvailable;

    private LocalDate expiryDate;

    /** Manager adds a new batch, or an ADJUSTMENT increases stock. */
    public void receive(int qty, LocalDate newExpiryDate) {
        this.quantityOnHand += qty;
        this.quantityAvailable += qty;
        if (newExpiryDate != null) {
            this.expiryDate = (this.expiryDate == null)
                    ? newExpiryDate
                    : (newExpiryDate.isBefore(this.expiryDate) ? newExpiryDate : this.expiryDate);
        }
    }

    /** Checkout: hold stock for an order. Does not touch onHand. */
    public void reserve(int qty) {
        if (qty > this.quantityAvailable) {
            throw new InsufficientStockException(this.productId, qty, this.quantityAvailable);
        }
        this.quantityReserved += qty;
        this.quantityAvailable -= qty;
    }

    /** Payment failed / order cancelled: give the hold back. */
    public void release(int qty) {
        this.quantityReserved -= qty;
        this.quantityAvailable += qty;
    }

    /** Payment succeeded: the hold becomes a real deduction from physical stock. */
    public void confirmDeduction(int qty) {
        this.quantityOnHand -= qty;
        this.quantityReserved -= qty;
        // quantityAvailable is unchanged - it was already spent at reservation time.
    }

    /** Manager correction that reduces stock (loss, damage, recount). */
    public void adjustDown(int qty) {
        if (qty > this.quantityAvailable) {
            throw new InsufficientStockException(this.productId, qty, this.quantityAvailable);
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("Passing negative quantity to adjust down");
        }
        this.quantityOnHand -= qty;
        this.quantityAvailable -= qty;
    }

    public void adjustUp(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Passing negative quantity to adjust up");
        }
        this.quantityOnHand += qty;
        this.quantityAvailable += qty;
    }

}
