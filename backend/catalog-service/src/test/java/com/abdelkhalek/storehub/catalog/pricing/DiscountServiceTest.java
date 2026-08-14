package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountWithProductIds;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.DiscountRule;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.PercentageOff;
import com.abdelkhalek.storehub.catalog.pricing.entity.DiscountEntity;
import com.abdelkhalek.storehub.catalog.pricing.exception.DiscountOverlapException;
import com.abdelkhalek.storehub.catalog.pricing.repository.DiscountRepository;
import com.abdelkhalek.storehub.catalog.pricing.service.DiscountService;
import com.abdelkhalek.storehub.catalog.product.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    private DiscountRepository discountRepository;
    @InjectMocks
    private DiscountService discountService;

    @Test
    void findActiveDiscounts_mapsEntitiesToDiscountWithProductIds() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<UUID> productIds = List.of(productId);

        ProductEntity product = new ProductEntity();
        product.setId(productId);

        DiscountEntity entity = new DiscountEntity();
        entity.setId(UUID.randomUUID());
        entity.setStoreId(storeId);
        entity.setType(DiscountType.PERCENTAGE_OFF);
        entity.getProducts().add(product);

        when(discountRepository.findActiveDiscountsForProducts(eq(storeId), eq(productIds), any(Instant.class)))
                .thenReturn(List.of(entity));

        List<DiscountWithProductIds> result = discountService.findActiveDiscounts(storeId, productIds);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getProductIds()).containsExactly(productId);
    }

    @Test
    void create_savesDiscount_whenNoOverlap() {
        UUID storeId = UUID.randomUUID();
        ProductEntity product = new ProductEntity();
        product.setId(UUID.randomUUID());
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        when(discountRepository.existsOverlappingForProduct(storeId, product.getId(), start, end))
                .thenReturn(false);
        when(discountRepository.save(any(DiscountEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        DiscountEntity result = discountService.create(
                storeId, DiscountType.PERCENTAGE_OFF, mock(PercentageOff.class), start, end,
                Set.of(product));

        assertThat(result.getProducts()).contains(product);
        verify(discountRepository).save(any(DiscountEntity.class));
    }

    @Test
    void create_throwsDiscountOverlapException_whenAnyProductOverlaps() {
        UUID storeId = UUID.randomUUID();
        ProductEntity product = new ProductEntity();
        product.setId(UUID.randomUUID());
        Instant start = Instant.now();
        Instant end = start.plusSeconds(3600);

        when(discountRepository.existsOverlappingForProduct(storeId, product.getId(), start, end))
                .thenReturn(true);

        assertThatThrownBy(() -> discountService.create(
                storeId, DiscountType.PERCENTAGE_OFF, mock(PercentageOff.class), start, end, Set.of(product)))
                .isInstanceOf(DiscountOverlapException.class);

        verify(discountRepository, never()).save(any());
    }
}
