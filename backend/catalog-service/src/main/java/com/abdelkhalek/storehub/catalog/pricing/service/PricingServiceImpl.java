package com.abdelkhalek.storehub.catalog.pricing.service;

import com.abdelkhalek.storehub.catalog.dtos.*;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import com.abdelkhalek.storehub.catalog.pricing.domain.factories.DiscountStrategyFactory;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.*;
import com.abdelkhalek.storehub.catalog.pricing.domain.strategies.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class PricingServiceImpl implements PricingService {

    private final ProductRepository productRepository;
    private final DiscountService discountService;

    public PricesResponse calculateTotal(PricesRequest request) {

        List<UUID> productIds = request.getItems().stream().map(PriceItemRequest::getProductId)
                .toList();

        List<ProductEntity> products =
                productRepository.findByStoreIdAndIdInWithDiscounts(request.getStoreId(),
                        productIds);

        List<DiscountWithProductIds> discounts =
                discountService.findActiveDiscounts(request.getStoreId(), productIds);

        // Build the list of Item
        List<Item> items = products.stream().map((productEntity -> {
            Optional<PriceItemRequest> priceItemOptional =
                    request.getItems().stream()
                            .filter((item) -> item.getProductId().equals(productEntity.getId()))
                            .findAny();
            if (priceItemOptional.isEmpty()) {
                throw new RuntimeException("Product " + productEntity.getId() + " not found" +
                        " in " + products);
            }
            int qty = priceItemOptional.get().getQuantity();
            Item item = new Item();
            item.setProductId(productEntity.getId());
            item.setQuantity(qty);
            item.setUnitPrice(productEntity.getUnitPrice());
            return item;
        })).toList();

        log.debug("Items with their unit price are {}", items);

        // Discount strategies operate on a Cart (some may need cross-item logic,
        // e.g. bundle discounts), this Cart is purely an internal scratch object,
        // never returned or persisted.
        Cart scratchCart = new Cart();
        scratchCart.setItems(items);

        for (DiscountWithProductIds discount : discounts) {
            DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(discount);
            log.debug("Applying {}", discountStrategy);
            discountStrategy.apply(scratchCart);
        }

        log.debug("cart items after discount: {}", scratchCart.getItems());

        BigDecimal originalTotal = scratchCart.getOriginalTotal();
        BigDecimal finalTotal = scratchCart.getFinalTotal();
        BigDecimal totalDiscount = originalTotal.subtract(finalTotal);

        log.debug("Pricing result: original={}, final={}, discount={}", originalTotal, finalTotal,
                totalDiscount);

        PricesResponse response = new PricesResponse();
        response.setFinalTotal(finalTotal);
        response.setOriginalTotal(originalTotal);
        response.setTotalDiscount(totalDiscount);

        List<PriceItemResponse> priceItems = items.stream().map(
                (item -> {
                    PriceItemResponse priceItemResponse = new PriceItemResponse();
                    priceItemResponse.setProductId(item.getProductId());
                    priceItemResponse.setQuantity(item.getQuantity());
                    priceItemResponse.setUnitPrice(item.getOriginalUnitPrice());
                    priceItemResponse.setOriginalLineTotal(item.getOriginalUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                    priceItemResponse.setFinalLineTotal(item.getFinalUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
                    BigDecimal appliedDiscountAmount =
                            (item.getFinalUnitPrice().subtract(item.getOriginalUnitPrice()))
                                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                    priceItemResponse.setDiscountAmount(appliedDiscountAmount);
                    // TODO: Add applied offer id and type
                    item.getAppliedDiscounts().stream().findAny()
                            .ifPresent(appliedDiscount -> priceItemResponse
                                    .setAppliedOffer(new AppliedOffer(null,
                                            appliedDiscount.getDescription(), null)));
                    return priceItemResponse;
                })).toList();

        response.setItems(priceItems);

        return response;
    }
}
