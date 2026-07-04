package com.abdelkhalek.storehub.catalog.pricing.domain.spi;


import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;
import com.abdelkhalek.storehub.order.pricing.domain.models.UnitPrice;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {


    public Mono<List<Discount>> getDiscounts(List<String> productIdList);

    public Mono<List<UnitPrice>> getUnitPrices(List<String> productIdList);

}
