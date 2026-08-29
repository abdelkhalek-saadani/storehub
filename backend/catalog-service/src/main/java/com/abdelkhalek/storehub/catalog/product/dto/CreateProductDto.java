package com.abdelkhalek.storehub.catalog.product.dto;

import java.math.BigDecimal;

public record CreateProductDto(
        String name,
        BigDecimal unitPrice,
        int initialQty
) {
}
