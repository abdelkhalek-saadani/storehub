package com.abdelkhalek.storehub.catalog.product;

import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
            List<String> categories,
            Boolean isBestSeller,
            String saleEventSlug) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (saleEventSlug != null) {
                Join<ProductEntity, SaleEvent> saleEventJoin = root.join("saleEvent");
                predicates.add(cb.equal(saleEventJoin.get("slug"), saleEventSlug));
            }

            if (isBestSeller != null) {
                predicates.add(cb.equal(root.get("isBestSeller"), isBestSeller));
            }

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

            // To check that the product has a stock row
            if (query != null) {
                Subquery<Integer> stockSubquery = query.subquery(Integer.class);
                Root<StockEntity> stockRoot = stockSubquery.from(StockEntity.class);
                stockSubquery.select(cb.literal(1))
                        .where(cb.equal(stockRoot.get("productId"), root.get("id")));

                predicates.add(cb.exists(stockSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}