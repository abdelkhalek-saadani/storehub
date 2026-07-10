package com.abdelkhalek.storehub.catalog.product.dto;

import java.util.List;
import java.util.UUID;

public record ParentCategoryDTO(UUID id, String name, List<SubCategoryDTO> subCategories) {}
