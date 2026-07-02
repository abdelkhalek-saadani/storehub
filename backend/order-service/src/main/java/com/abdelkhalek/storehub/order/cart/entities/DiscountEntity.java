package com.abdelkhalek.storehub.order.cart.entities;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With

public class DiscountEntity {

    private UUID id;
    private String productId;
    private Map<String, String> attributes;

    @ManyToOne(foreignKey = "cart_id")
    CartEntity cart;
    UUID cartId;
}
