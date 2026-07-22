package com.abdelkhalek.storehub.order.order.entity;

import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@NoArgsConstructor
@Table(
        name = "order"
)
public class OrderEntity {

    @Id
    UUID id;
    UUID userId;
    UUID storeId;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;


    @OneToMany(mappedBy = "order_id")
    List<OrderItemEntity> items;


    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime updatedAt;

    String deliveryAddress;
    String billingAddress;

    UUID slotId;

    BigDecimal deliveryFee = BigDecimal.TEN;

    UUID slotRetainId;
    List<UUID> inventoryRetainIds;

}
