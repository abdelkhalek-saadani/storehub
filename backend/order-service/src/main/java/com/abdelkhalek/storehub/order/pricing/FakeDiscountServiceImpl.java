package com.abdelkhalek.storehub.order.pricing;


import com.abdelkhalek.storehub.order.pricing.domain.models.Item;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Primary
@Service
public class FakeDiscountServiceImpl implements DiscountService {

    public Mono<PricingResult> calculateTotal(List<Item> items) {
        List<Item> pricedItems = items.stream().map(Item::copy).toList();

        for (Item item : pricedItems) {
            BigDecimal unitPrice = item.getOriginalUnitPrice() == null ? BigDecimal.TEN : item.getOriginalUnitPrice();
            item.initializePrices(unitPrice.subtract(BigDecimal.ONE)); // unit price -1
        }

        BigDecimal originalTotal = pricedItems.stream().map(Item::getOriginalSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal finalTotal = pricedItems.stream().map(Item::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = BigDecimal.TEN;

        return Mono.just(new PricingResult(pricedItems, originalTotal, finalTotal, totalDiscount));
    }
}