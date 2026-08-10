package com.abdelkhalek.storehub.catalog.pricing.service;


import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.DiscountRule;
import com.abdelkhalek.storehub.catalog.pricing.entity.DiscountEntity;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.pricing.exception.DiscountOverlapException;
import com.abdelkhalek.storehub.catalog.pricing.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<DiscountWithProductIds> findActiveDiscounts(UUID storeId, List<UUID> productIds) {
        return discountRepository.findActiveDiscountsForProducts(storeId, productIds, Instant.now())
                .stream()
                .map(d -> new DiscountWithProductIds(
                        d.getId(), d.getStoreId(), d.getType(), d.getRule(),
                        d.getStartsAt(), d.getEndsAt(),
                        d.getProducts().stream().map(ProductEntity::getId).toList()))
                .toList();
    }

    @Transactional
    public DiscountEntity create(UUID storeId, DiscountType type, DiscountRule rule,
                                 Instant startsAt, Instant endsAt, Set<ProductEntity> products) {
        for (ProductEntity product : products) {
            boolean overlaps = discountRepository
                    .existsOverlappingForProduct(storeId, product.getId(), startsAt, endsAt);
            if (overlaps) {
                throw new DiscountOverlapException(product.getId(), startsAt, endsAt);
            }
        }

        DiscountEntity discount = new DiscountEntity();
        // set fields...
        discount.getProducts().addAll(products);
        return discountRepository.save(discount);
    }
}