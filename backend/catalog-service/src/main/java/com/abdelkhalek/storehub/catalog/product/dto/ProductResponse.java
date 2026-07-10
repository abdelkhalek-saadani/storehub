package com.abdelkhalek.storehub.catalog.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID storeId,
        String name,
        String description,
        BigDecimal unitPrice,
        DiscountSummary activeDiscount, // null if none active
        String categoryName
) {}