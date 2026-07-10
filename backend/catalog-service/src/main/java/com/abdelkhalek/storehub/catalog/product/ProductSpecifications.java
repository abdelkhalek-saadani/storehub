package com.abdelkhalek.storehub.catalog.product;

import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecifications {

    public static Specification<ProductEntity> filter(
            UUID storeId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> categories) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categories != null && !categories.isEmpty()) {
                Join<ProductEntity, SubCategory> subCategoryJoin = root.join("subCategory");
                predicates.add(subCategoryJoin.get("name").in(categories));
            }

            predicates.add(cb.equal(root.get("storeId"), storeId));

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("unitPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("unitPrice"), maxPrice));
            }



            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}