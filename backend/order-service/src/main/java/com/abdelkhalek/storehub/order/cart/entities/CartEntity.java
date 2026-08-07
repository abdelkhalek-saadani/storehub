package com.abdelkhalek.storehub.order.cart.entities;

import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
@Table(name = "cart")
public class CartEntity {

    @Id
    UUID id;

    UUID userId;

    UUID guestId;

    UUID storeId;


    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;

    @OneToMany(mappedBy = "cart_id")
    List<CartItemEntity> items;

    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;
}