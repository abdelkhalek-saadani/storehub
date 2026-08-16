package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.domain.Cart;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.domain.CartOwner;
import com.abdelkhalek.storehub.order.cart.dto.CartResponse;
import com.abdelkhalek.storehub.order.cart.dto.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.entity.CartEntity;
import com.abdelkhalek.storehub.order.cart.entity.CartItemEntity;
import com.abdelkhalek.storehub.order.cart.repository.CartRepository;
import com.abdelkhalek.storehub.order.cart.service.CartService;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import com.abdelkhalek.storehub.order.shared.dto.PricesResponse;
import com.abdelkhalek.storehub.order.shared.service.PricesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock private CartMapper cartMapper;
    @Mock private PricesService pricesService;
    @InjectMocks
    private CartService cartService;

    @Test
    void getCart_returnsUserCart_whenUserOwnerAndCartExists() {
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofUser(userId);
        CartEntity entity = new CartEntity();
        CartResponse response = createCartResponse(storeId);

        when(cartRepository.findByUserIdAndStoreId(userId, storeId)).thenReturn(Mono.just(entity));
        when(cartMapper.fromEntityToResponse(entity)).thenReturn(response);

        StepVerifier.create(cartService.getCart(owner, storeId))
                .expectNextMatches(result -> result.body().storeId().equals(response.storeId()))
                .verifyComplete();

        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCart_createsEmptyCart_whenUserOwnerAndNoCartExists() {
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofUser(userId);
        CartEntity savedEntity = new CartEntity();
        CartResponse response = createCartResponse(storeId);

        when(cartRepository.findByUserIdAndStoreId(userId, storeId)).thenReturn(Mono.empty());
        when(cartRepository.save(any(CartEntity.class))).thenReturn(Mono.just(savedEntity));
        when(cartMapper.fromEntityToResponse(savedEntity)).thenReturn(response);

        StepVerifier.create(cartService.getCart(owner, storeId))
                .expectNextCount(1)
                .verifyComplete();

        verify(cartRepository).save(any(CartEntity.class));
    }

    @Test
    void getCart_returnsGuestCart_whenGuestOwnerAndCartExists() {
        UUID guestId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofGuest(guestId);
        CartEntity entity = new CartEntity();
        CartResponse response = createCartResponse(storeId);

        when(cartRepository.findByGuestIdAndStoreId(guestId, storeId)).thenReturn(Mono.just(entity));
        when(cartMapper.fromEntityToResponse(entity)).thenReturn(response);

        StepVerifier.create(cartService.getCart(owner, storeId))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void upsertItems_updatesExistingCart_andRepricesUsingPricesService() {
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID existingProductId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofUser(userId);
        UpdateCartRequest request = new UpdateCartRequest(storeId,
                List.of(new UpdateCartRequest.CartItem(productId, 2)));

        CartEntity existingEntity = new CartEntity();
        Cart domainCart = new Cart();// real, empty cart, upsert() will run for real
        domainCart.setItems(List.of(new CartItem(existingProductId,10)));
        PricesResponse pricesResponse = new PricesResponse(List.of(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);
        CartEntity savedEntity = new CartEntity();
        CartResponse response = createCartResponse(storeId);

        when(cartRepository.findByUserIdAndStoreId(userId, storeId)).thenReturn(Mono.just(existingEntity));
        when(cartMapper.fromEntityToDomain(existingEntity)).thenReturn(domainCart);

        // capture what pricing was actually requested for
        ArgumentCaptor<Cart> pricesRequestCaptor = ArgumentCaptor.forClass(Cart.class);
        when(cartMapper.toPricesRequest(pricesRequestCaptor.capture())).thenReturn(mock(PricesRequest.class));

        when(pricesService.fetchPrices(any())).thenReturn(Mono.just(pricesResponse));
        when(cartMapper.fromPriceItemsResponse(any())).thenReturn(List.of());

        // capture the cart passed to toEntity, to check totals/items were applied
        ArgumentCaptor<Cart> toEntityCaptor = ArgumentCaptor.forClass(Cart.class);
        when(cartMapper.toEntity(toEntityCaptor.capture())).thenReturn(savedEntity);

        when(cartRepository.save(savedEntity)).thenReturn(Mono.just(savedEntity));
        when(cartMapper.fromEntityToResponse(savedEntity)).thenReturn(response);

        StepVerifier.create(cartService.upsertItems(owner, request))
                .expectNextCount(1)
                .verifyComplete();

        // 1. correct item was upserted into the domain cart before pricing
        Cart cartSentForPricing = pricesRequestCaptor.getValue();
        assertThat(cartSentForPricing.getItems())
                .extracting(CartItem::getProductId,
                        CartItem::getQuantity)
                .containsExactlyInAnyOrder(tuple(productId, 2), tuple(existingProductId, 10));

        // 2. reprice results (totals) were actually applied to the cart before saving
        Cart cartSentToEntity = toEntityCaptor.getValue();
        assertThat(cartSentToEntity.getFinalTotal()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(cartSentToEntity.getOriginalTotal()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(cartSentToEntity.getTotalDiscount()).isEqualByComparingTo(BigDecimal.ZERO);

        verify(cartRepository).save(savedEntity);
    }

    @Test
    void quote_returnsZeroPrices_whenItemsEmpty() {
        UpdateCartRequest request = new UpdateCartRequest(UUID.randomUUID(), List.of());

        StepVerifier.create(cartService.quote(request))
                .expectNextMatches(pr -> pr.finalTotal().equals(BigDecimal.ZERO))
                .verifyComplete();

        verifyNoInteractions(pricesService);
    }

    @Test
    void quote_delegatesToPricesService_whenItemsPresent() {
        UUID productId = UUID.randomUUID();
        UpdateCartRequest request = new UpdateCartRequest(UUID.randomUUID(),
                List.of(new UpdateCartRequest.CartItem(productId, 1)));
        PricesResponse expected = new PricesResponse(List.of(), BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO);

        when(cartMapper.fromGuestCartRequestToPricesRequest(request)).thenReturn(mock(PricesRequest.class));
        when(pricesService.fetchPrices(any())).thenReturn(Mono.just(expected));

        StepVerifier.create(cartService.quote(request))
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void clearCart_resetsItemsAndTotals_toZero() {
        UUID userId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofUser(userId);
        CartEntity entity = new CartEntity();
        entity.setItems(List.of(new CartItemEntity(UUID.randomUUID(), 2)));
        entity.setFinalTotal(BigDecimal.TEN);
        CartResponse response = createCartResponse(storeId);

        when(cartRepository.findByUserIdAndStoreId(userId, storeId)).thenReturn(Mono.just(entity));
        ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
        when(cartRepository.save(captor.capture())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(cartMapper.fromEntityToResponse(any())).thenReturn(response);

        StepVerifier.create(cartService.clearCart(owner, storeId))
                .expectNextCount(1)
                .verifyComplete();

        CartEntity saved = captor.getValue();
        assertThat(saved.getItems()).isEmpty();
        assertThat(saved.getFinalTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }





    private CartResponse createCartResponse(UUID storeId){
        return new CartResponse(UUID.randomUUID(),List.of(),BigDecimal.ZERO,
                BigDecimal.ZERO,BigDecimal.ZERO,storeId);
    }
}
