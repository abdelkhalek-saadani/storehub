package com.abdelkhalek.storehub.catalog;

import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;
import com.abdelkhalek.storehub.catalog.inventory.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.exception.InsufficientStockException;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.pricing.service.PricingService;
import org.springframework.stereotype.Service;

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
            StockEntity stockEntity =
                    stockRepository.findByStoreIdAndProductId(request.getStoreId(),
                            item.getProductId()).orElseThrow();
            if (stockEntity.getQuantityAvailable() > item.getQuantity()) {
                throw new InsufficientStockException(item.getProductId(), item.getQuantity(), stockEntity.getQuantityAvailable());
            }
        });
        // Use the pricing package to perform the totals calculation

        return pricingService.calculateTotal(request);
    }

}
