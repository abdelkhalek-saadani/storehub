package com.abdelkhalek.storehub.catalog.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductDto(
        String name,
        BigDecimal unitPrice,
        int initialQty,
        String imageUrl,
        boolean isBestSeller,
        UUID categoryId
) {
}
