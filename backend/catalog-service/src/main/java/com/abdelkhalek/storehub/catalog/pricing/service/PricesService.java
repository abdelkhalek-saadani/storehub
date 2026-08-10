package com.abdelkhalek.storehub.catalog.pricing.service;

import com.abdelkhalek.storehub.catalog.pricing.dto.PriceItemRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesResponse;
import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.exception.InsufficientStockException;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.pricing.exception.StockNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PricesService {

    private final StockRepository stockRepository;
    private final PricingService pricingService;

    public PricesService(StockRepository stockRepository, PricingService pricingService) {
        this.stockRepository = stockRepository;
        this.pricingService = pricingService;
    }

    public PricesResponse getPrices(PricesRequest request) {
        // Check products availability (there is sufficient qty or not, this will look for
        // the product stock)
        request.getItems().forEach(item -> {
            validateStock(request.getStoreId(), item);
        });
        // Use the pricing package to perform the totals calculation

        return pricingService.calculateTotal(request);
    }


    private void validateStock(UUID storeId, PriceItemRequest item) {
        StockEntity stockEntity = stockRepository
                .findByStoreIdAndProductId(storeId, item.getProductId())
                .orElseThrow(() -> new StockNotFoundException(item.getProductId()));

        if (stockEntity.getQuantityAvailable() < item.getQuantity()) {
            throw new InsufficientStockException(
                    item.getProductId(),
                    item.getQuantity(),
                    stockEntity.getQuantityAvailable()
            );
        }
    }
}
