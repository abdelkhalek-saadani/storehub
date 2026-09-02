package com.abdelkhalek.storehub.order.order.models;

import com.abdelkhalek.storehub.order.shared.model.AppliedOffer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderItem {

    UUID id;
    String productName;
    String productImageUrl;
    UUID productId;
    int quantity;

    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    AppliedOffer appliedOffer;

    Order order;

    LocalDateTime createdAt;

}