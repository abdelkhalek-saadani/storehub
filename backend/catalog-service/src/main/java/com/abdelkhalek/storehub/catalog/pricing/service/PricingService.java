package com.abdelkhalek.storehub.catalog.pricing;



import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;

import java.util.List;

public interface PricingService {
    PricesResponse calculateTotal(PricesRequest request);
}

