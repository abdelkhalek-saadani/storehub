package com.abdelkhalek.storehub.order.order.entity;

import io.github.joselion.springr2dbcrelationships.annotations.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
@NoArgsConstructor

@Table(
        name = "order_item"
)
public class OrderItemEntity {


    @Id
    UUID id;

    String productName;

    UUID productId;

    int quantity;

    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    String appliedOfferLabel;

    @ManyToOne(foreignKey = "order_id")
    OrderEntity order;
    UUID orderId;

    @CreatedDate
    LocalDateTime createdAt;

}
