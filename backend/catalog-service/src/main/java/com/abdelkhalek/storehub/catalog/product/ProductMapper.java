package com.abdelkhalek.storehub.catalog.product;


import com.abdelkhalek.storehub.catalog.pricing.entity.DiscountEntity;
import com.abdelkhalek.storehub.catalog.product.dto.DiscountSummary;
import com.abdelkhalek.storehub.catalog.product.dto.ParentCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.dto.ProductResponse;
import com.abdelkhalek.storehub.catalog.product.dto.SubCategoryDTO;
import com.abdelkhalek.storehub.catalog.product.entity.ParentCategory;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(source = "discounts", target = "activeDiscount", qualifiedByName = "activeDiscount")
    @Mapping(source = "subCategory", target = "categoryName", qualifiedByName =
            "subCategoryToCategory")
    ProductResponse toResponse(ProductEntity entity);

    List<ProductResponse> toResponses(List<ProductEntity> entities);

    @Named("subCategoryToCategory")
    default String subCategoryToCategory(SubCategory subCategory) {
        if (subCategory == null) return null;
        return subCategory.getName();
    }

    @Named("activeDiscount")
    default DiscountSummary findActiveDiscount(Set<DiscountEntity> discounts) {
        return discounts.stream()
                .filter(d -> d.isActiveAt(Instant.now()))
                .findFirst()
                .map(this::toSummary)
                .orElse(null);
    }

    DiscountSummary toSummary(DiscountEntity entity);

    SubCategoryDTO toSubCategoryDTO(SubCategory subCategory);
    List<SubCategoryDTO> toSubCategoryDTOs(List<SubCategory> subCategory);



    ParentCategoryDTO toParentCategoryDTO(ParentCategory parentCategory);
    List<ParentCategoryDTO> toParentCategoryDTOs(List<ParentCategory> parentCategory);
}
