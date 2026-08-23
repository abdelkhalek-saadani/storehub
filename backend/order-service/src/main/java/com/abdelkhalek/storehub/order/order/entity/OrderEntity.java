package com.abdelkhalek.storehub.order.order.entity;

import com.abdelkhalek.storehub.order.order.dto.AddressDto;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import io.github.joselion.springr2dbcrelationships.annotations.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.relational.core.mapping.Column;
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
        name = "orders"
)
@EnableR2dbcAuditing
public class OrderEntity {

    @Id
    UUID id;
    UUID userId;
    UUID guestId;
    String email;
    UUID storeId;
    BigDecimal originalTotal;
    BigDecimal finalTotal;
    BigDecimal totalDiscount;


    @OneToMany(mappedBy = "order_id")
    List<OrderItemEntity> items;


    @CreatedDate
    LocalDateTime createdAt;
    @LastModifiedDate
    LocalDateTime updatedAt;


    AddressDto deliveryAddress;
    String billingAddress;

    UUID slotId;

    BigDecimal deliveryFee = BigDecimal.TEN;

    UUID slotRetainId;
    List<UUID> inventoryRetainIds;

    @Column("status")
    OrderStatus status;

    UUID paymentId;
    String paymentOrderId;

    // paymentApprovalLink is cache from payment-service
    // It is cached so responses returned due to idempotencyKey match have the approval link,
    // avoiding recalling payment-svc each time
    String paymentApprovalLink;

    UUID idempotencyKey;

}
