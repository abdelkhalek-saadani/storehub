package com.abdelkhalek.storehub.catalog.pricing.service;



import com.abdelkhalek.storehub.catalog.pricing.dto.PricesRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesResponse;

public interface PricingService {
    PricesResponse calculateTotal(PricesRequest request);
}

