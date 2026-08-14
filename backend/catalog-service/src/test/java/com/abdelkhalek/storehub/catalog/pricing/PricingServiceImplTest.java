package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Item;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.dto.PriceItemRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesResponse;
import com.abdelkhalek.storehub.catalog.pricing.service.DiscountService;
import com.abdelkhalek.storehub.catalog.pricing.service.PricingServiceImpl;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import com.abdelkhalek.storehub.catalog.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock private DiscountService discountService;
    @Mock private PriceItemMapper priceItemMapper;
    @InjectMocks
    private PricingServiceImpl pricingService;

    @Test
    void calculateTotal_buildsItems_withCorrectUnitPriceAndQuantity() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(3);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setName("Widget");
        product.setUnitPrice(BigDecimal.TEN);

        when(productRepository.findByStoreIdAndIdInWithDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of(product));
        when(discountService.findActiveDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of());
        when(priceItemMapper.toResponses(anyList())).thenReturn(List.of());

        pricingService.calculateTotal(request);

        // capture items passed to mapper to assert built correctly
        ArgumentCaptor<List<Item>> captor = ArgumentCaptor.forClass(List.class);
        verify(priceItemMapper).toResponses(captor.capture());
        Item builtItem = captor.getValue().getFirst();

        assertThat(builtItem.getProductId()).isEqualTo(productId);
        assertThat(builtItem.getQuantity()).isEqualTo(3);
        assertThat(builtItem.getOriginalUnitPrice()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void calculateTotal_throwsRuntimeException_whenProductNotInRequest() {
        UUID storeId = UUID.randomUUID();
        UUID requestedProductId = UUID.randomUUID();
        UUID returnedProductId = UUID.randomUUID(); // mismatch

        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(requestedProductId);
        itemRequest.setQuantity(1);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        ProductEntity product = new ProductEntity();
        product.setId(returnedProductId);

        when(productRepository.findByStoreIdAndIdInWithDiscounts(storeId, List.of(requestedProductId)))
                .thenReturn(List.of(product));

        assertThatThrownBy(() -> pricingService.calculateTotal(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void calculateTotal_appliesEachActiveDiscount_toCart() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(1);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setUnitPrice(BigDecimal.TEN);

        DiscountWithProductIds discount = new DiscountWithProductIds(
                UUID.randomUUID(), storeId, DiscountType.PERCENTAGE_OFF,
                new PercentageOff(BigDecimal.valueOf(10)),
                Instant.now(), Instant.now().plusSeconds(3600), List.of(productId));

        when(productRepository.findByStoreIdAndIdInWithDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of(product));
        when(discountService.findActiveDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of(discount));
        when(priceItemMapper.toResponses(anyList())).thenReturn(List.of());

        PricesResponse response = pricingService.calculateTotal(request);

        // 10% off 10 = 1 discount per unit, qty 1 -> total discount 1
        assertThat(response.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void calculateTotal_computesOriginalFinalAndDiscountTotals_correctly() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(2);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setUnitPrice(BigDecimal.valueOf(20));

        when(productRepository.findByStoreIdAndIdInWithDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of(product));
        when(discountService.findActiveDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of());
        when(priceItemMapper.toResponses(anyList())).thenReturn(List.of());

        PricesResponse response = pricingService.calculateTotal(request);

        assertThat(response.getOriginalTotal()).isEqualByComparingTo(BigDecimal.valueOf(40)); // 20 * 2
        assertThat(response.getFinalTotal()).isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(response.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculateTotal_returnsZeroDiscount_whenNoActiveDiscounts() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        PriceItemRequest itemRequest = new PriceItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(1);

        PricesRequest request = new PricesRequest();
        request.setStoreId(storeId);
        request.setItems(List.of(itemRequest));

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setUnitPrice(BigDecimal.TEN);

        when(productRepository.findByStoreIdAndIdInWithDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of(product));
        when(discountService.findActiveDiscounts(storeId, List.of(productId)))
                .thenReturn(List.of());
        when(priceItemMapper.toResponses(anyList())).thenReturn(List.of());

        PricesResponse response = pricingService.calculateTotal(request);

        assertThat(response.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
