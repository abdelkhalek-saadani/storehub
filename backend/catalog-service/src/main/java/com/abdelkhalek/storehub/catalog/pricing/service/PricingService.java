package com.abdelkhalek.storehub.catalog.pricing.service;



import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;

public interface PricingService {
    PricesResponse calculateTotal(PricesRequest request);
}

