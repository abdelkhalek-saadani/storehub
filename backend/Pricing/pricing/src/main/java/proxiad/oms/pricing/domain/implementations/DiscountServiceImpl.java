package proxiad.oms.pricing.domain.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proxiad.oms.pricing.domain.spi.ProductService;
import proxiad.oms.pricing.domain.models.*;
import proxiad.oms.pricing.domain.DiscountService;
import proxiad.oms.pricing.domain.spi.EventPublisher;
import proxiad.oms.pricing.domain.strategies.DiscountStrategy;
import proxiad.oms.pricing.domain.factories.DiscountStrategyFactory;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private ProductService productService;

    public Mono<Void> calculateTotal(Cart cart) {

        // Get unit prices and discounts of item list received (from external service)
        List<String> productIdList = cart.getItems().stream().map(Item::getProductId).toList();

        Mono<List<UnitPrice>> unitPrices = productService.getUnitPrices(productIdList);
        Mono<List<Discount>> discounts = productService.getDiscounts(productIdList);

        // When unit prices and discounts ready then apply discount logic
        return Mono.zip(unitPrices,discounts)
                .flatMap(tuple -> {
                    // TODO: Refactor this logic into multiple methods to make it more readable
                    List<UnitPrice> unitPriceList = tuple.getT1();
                    List<Discount> discountList = tuple.getT2();
                    // Map the list of (productId, unitPrice) to a map with key=productId, value=unitPrice
                    Map<String, BigDecimal> unitPriceMap =
                            unitPriceList.stream()
                                    .collect(Collectors.toMap(UnitPrice::getId, unitPrice -> new BigDecimal(unitPrice.getUnitPrice())));
                    // Populate cart items with unit price
                    for (Item item : cart.getItems()) {
                        item.initializePrices(unitPriceMap.get(item.getProductId())); // this to initialize the original and final unit price
                        log.info("the item is: {}", item);
                    };


                    // Apply or received discounts
                    for (Discount discount: discountList) {
                        DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(discount);
                        log.info("Applying {}" , discountStrategy);
                        discountStrategy.apply(cart);
                    }
                    log.info("Shopping Cart: {}", cart);

                    // I created this (TotalWithDiscount) just to hold the discount info, not mandatory for now
                    TotalWithDiscount totalWithDiscount = new TotalWithDiscount(cart.getFinalTotal(), cart.getTotalDiscount());

                    Money total = new Money(totalWithDiscount.getTotal());

                    // The event containing the new total
                    TotalCalculatedEvent totalCalculatedEvent = new TotalCalculatedEvent(cart.getCartId(), total);

                    eventPublisher.publish(totalCalculatedEvent);

                    return Mono.empty();


                });


    }

}
