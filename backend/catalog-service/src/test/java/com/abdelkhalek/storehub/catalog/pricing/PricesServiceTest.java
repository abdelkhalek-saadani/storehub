package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.exception.InsufficientStockException;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.pricing.dto.PriceItemRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesResponse;
import com.abdelkhalek.storehub.catalog.pricing.exception.StockNotFoundException;
import com.abdelkhalek.storehub.catalog.pricing.service.PricesService;
import com.abdelkhalek.storehub.catalog.pricing.service.PricingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricesServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock private PricingService pricingService;
    @InjectMocks
    private PricesService pricesService;

    @Test
    void getPrices_delegatesToPricingService_whenStockSufficientForAllItems() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(2);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        StockEntity stock = new StockEntity();
        stock.setQuantityAvailable(10);

        PricesResponse expected = new PricesResponse();

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(stock));
        when(pricingService.calculateTotal(request)).thenReturn(expected);

        PricesResponse result = pricesService.getPrices(request);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void getPrices_throwsInsufficientStockException_whenAnyItemShort() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(20);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        StockEntity stock = new StockEntity();
        stock.setQuantityAvailable(5);

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> pricesService.getPrices(request))
                .isInstanceOf(InsufficientStockException.class);

        verifyNoInteractions(pricingService);
    }

    @Test
    void getPrices_throwsStockNotFoundException_whenStockRowMissing() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(1);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricesService.getPrices(request))
                .isInstanceOf(StockNotFoundException.class);

        verifyNoInteractions(pricingService);
    }
}

