package com.abdelkhalek.storehub.order.pricing;

import com.abdelkhalek.storehub.order.pricing.domain.factories.DiscountStrategyFactory;
import com.abdelkhalek.storehub.order.pricing.domain.models.Cart;
import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;
import com.abdelkhalek.storehub.order.pricing.domain.models.Item;
import com.abdelkhalek.storehub.order.pricing.domain.models.UnitPrice;
import com.abdelkhalek.storehub.order.pricing.domain.spi.ProductService;
import com.abdelkhalek.storehub.order.pricing.domain.strategies.DiscountStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private ProductService productService;

    public Mono<PricingResult> calculateTotal(List<Item> items) {

        List<String> productIdList = items.stream().map(Item::getProductId).toList();

        Mono<List<UnitPrice>> unitPrices = productService.getUnitPrices(productIdList);
        Mono<List<Discount>> discounts = productService.getDiscounts(productIdList);

        return Mono.zip(unitPrices, discounts)
                .map(tuple -> {
                    List<UnitPrice> unitPriceList = tuple.getT1();
                    List<Discount> discountList = tuple.getT2();

                    // Work on copies so the caller's items are never mutated
                    List<Item> pricedItems = items.stream().map(Item::copy).toList();

                    Map<String, BigDecimal> unitPriceMap =
                            unitPriceList.stream()
                                    .collect(Collectors.toMap(UnitPrice::getId, unitPrice -> new BigDecimal(unitPrice.getUnitPrice())));

                    for (Item item : pricedItems) {
                        item.initializePrices(unitPriceMap.get(item.getProductId()));
                        log.info("the item is: {}", item);
                    }

                    // Discount strategies operate on a Cart (some may need cross-item logic,
                    // e.g. bundle discounts) — this Cart is purely an internal scratch object,
                    // never returned or persisted.
                    Cart scratchCart = new Cart();
                    scratchCart.setItems(pricedItems);

                    for (Discount discount : discountList) {
                        DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(discount);
                        log.info("Applying {}", discountStrategy);
                        discountStrategy.apply(scratchCart);
                    }

                    BigDecimal originalTotal = scratchCart.getOriginalTotal();
                    BigDecimal finalTotal = scratchCart.getFinalTotal();
                    BigDecimal totalDiscount = originalTotal.subtract(finalTotal);

                    log.info("Pricing result: original={}, final={}, discount={}", originalTotal, finalTotal, totalDiscount);

                    return new PricingResult(scratchCart.getItems(), originalTotal, finalTotal, totalDiscount);
                });
    }
}