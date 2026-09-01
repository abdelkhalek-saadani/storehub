package com.abdelkhalek.storehub.catalog.product.dto;

import java.util.UUID;

public record CreateSaleEventDto (
        UUID id,
        String name,
        String imageUrl,
        String slug,
        String description

){


}
