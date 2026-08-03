package com.abdelkhalek.storehub.order.cart.entities;


import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
@Table(name = "cart_item")
public class CartItemEntity {
    // productName, productImageUrl, unitPrice, and totals are a cache from catalog-service,
    // refreshed on every upsertItems() call (via reprice()).
    // A plain getCart() does NOT refresh them, may show stale data
    // until the cart is next mutated. Acceptable for cart UX; if a
    // product is renamed/repriced, it corrects itself on next add/update.

    @Id
    UUID id;

    String productName;
    String productImageUrl;

    UUID productId;

    int quantity;

    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    String appliedOfferLabel;

    @ManyToOne(foreignKey = "cart_id")
    CartEntity cart;
    UUID cartId;

    LocalDateTime createdAt;}