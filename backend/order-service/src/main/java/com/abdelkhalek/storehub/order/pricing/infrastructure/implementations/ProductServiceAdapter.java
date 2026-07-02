package com.abdelkhalek.storehub.order.pricing.infrastructure.implementations;

import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;
import com.abdelkhalek.storehub.order.pricing.domain.models.UnitPrice;
import com.abdelkhalek.storehub.order.pricing.domain.spi.ProductService;
import com.abdelkhalek.storehub.order.pricing.infrastructure.mappers.DiscountResponseMapper;
import com.abdelkhalek.storehub.order.pricing.infrastructure.mappers.UnitPriceResponseMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service("pricingProductServiceAdapter")
public class ProductServiceAdapter implements ProductService {

    private final ExternalProductClient externalProductClient;
    private final DiscountResponseMapper discountResponseMapper;
    private final UnitPriceResponseMapper unitPriceResponseMapper;

    public ProductServiceAdapter(
            ExternalProductClient externalProductClient,
            UnitPriceResponseMapper unitPriceResponseMapper,
            DiscountResponseMapper discountResponseMapper) {
        this.externalProductClient = externalProductClient;
        this.unitPriceResponseMapper = unitPriceResponseMapper;
        this.discountResponseMapper = discountResponseMapper;
    }

    public Mono<List<UnitPrice>> getUnitPrices(List<String> productIds){
        return externalProductClient.getUnitPrices(productIds)
                .map(unitPriceResponseMapper::toUnitPrices);
    }

    public Mono<List<Discount>> getDiscounts(List<String> productIds){
        return externalProductClient.getDiscounts(productIds)
                .map(discountResponseMapper::toDiscounts);
    }


}
