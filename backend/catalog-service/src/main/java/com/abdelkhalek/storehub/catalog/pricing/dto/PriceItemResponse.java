package com.abdelkhalek.storehub.catalog.pricing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceItemResponse {
    UUID productId;
    String productName;
    String productImageUrl;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal originalLineTotal;
    BigDecimal discountAmount;
    BigDecimal finalLineTotal;
    AppliedOffer appliedOffer;
}
