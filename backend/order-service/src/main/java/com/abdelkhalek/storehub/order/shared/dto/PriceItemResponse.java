package com.abdelkhalek.storehub.order.shared.dto;

import com.abdelkhalek.storehub.order.shared.model.AppliedOffer;

import java.math.BigDecimal;
import java.util.UUID;


public record PriceItemResponse (
    UUID productId,
    String productName,
    String productImageUrl,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal originalLineTotal,
    BigDecimal discountAmount,
    BigDecimal finalLineTotal,
    AppliedOffer appliedOffer){}