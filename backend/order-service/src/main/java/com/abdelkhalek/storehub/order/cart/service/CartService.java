package com.abdelkhalek.storehub.order.cart.service;

import com.abdelkhalek.storehub.order.cart.CartMapper;
import com.abdelkhalek.storehub.order.cart.domain.Cart;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.domain.CartOwner;
import com.abdelkhalek.storehub.order.cart.dto.AddItemRequest;
import com.abdelkhalek.storehub.order.cart.dto.CartResponse;
import com.abdelkhalek.storehub.order.cart.dto.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.entity.CartEntity;
import com.abdelkhalek.storehub.order.cart.repository.CartRepository;
import com.abdelkhalek.storehub.order.shared.model.ServiceResult;
import com.abdelkhalek.storehub.order.shared.dto.PriceItemResponse;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import com.abdelkhalek.storehub.order.shared.dto.PricesResponse;
import com.abdelkhalek.storehub.order.shared.service.PricesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final PricesService pricesService;


    public Mono<ServiceResult<CartResponse>> getCart(CartOwner owner, UUID storeId) {
        Mono<CartEntity> cart = owner.isGuest()
                ? cartRepository.findByGuestIdAndStoreId(owner.guestId(), storeId)
                : cartRepository.findByUserIdAndStoreId(owner.userId(), storeId);

        return cart.switchIfEmpty(Mono.defer(() -> createEmptyCart(owner, storeId)))
                .map(cartMapper::fromEntityToResponse)
                .map((cr) -> owner.isGuest() ? ServiceResult.forGuest(cr, owner.guestId()) :
                        ServiceResult.forUser(cr));
    }

    private Mono<CartResponse> getCart(UUID userId, UUID storeId) {
        return cartRepository.findByUserIdAndStoreId(userId, storeId)
                .switchIfEmpty(createEmptyCart(CartOwner.ofUser(userId), storeId))
                .map(cartMapper::fromEntityToResponse);
    }

    public Mono<ServiceResult<CartResponse>> upsertItems(CartOwner owner,
                                                         UpdateCartRequest request) {
        return getOrCreateCart(owner, request.storeId())
                .doOnNext((c) -> log.debug("the looked up or created cart {}", c))
                .map(cartMapper::fromEntityToDomain)
                .map(cart -> {
                    List<CartItem> items =
                            request.items().stream().map((item) -> new CartItem(item.productId(),
                                    item.quantity())).toList();
                    return cart.upsert(items);
                })
                .flatMap(this::repriceAndSave)
                .map(cartMapper::fromEntityToResponse)
                .map((cr) -> owner.isGuest() ?
                        ServiceResult.forGuest(cr, owner.guestId()) : ServiceResult.forUser(cr));
    }


    public Mono<CartResponse> upsertItem(CartOwner owner, AddItemRequest request) {
        return getOrCreateCart(owner, request.storeId())
                .map(cartMapper::fromEntityToDomain)
                .map(cart -> cart.upsert(new CartItem(request.productId(), request.quantity())))
                .flatMap(this::repriceAndSave)
                .map(cartMapper::fromEntityToResponse);
    }


    public Mono<PricesResponse> quote(UpdateCartRequest request) {
        if (request.items().isEmpty()) {
            return Mono.just(new PricesResponse(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        return pricesService.fetchPrices(cartMapper.fromGuestCartRequestToPricesRequest(request));
    }

    // --- internal helpers ---

    /**
     * Apply discounts, update unit prices and save the cart
     *
     * @param cart cart with items
     * @return the saved cart entity
     */
    private Mono<CartEntity> repriceAndSave(Cart cart) {
        return reprice(cart)
                .map(cartMapper::toEntity)
                .doOnNext(cartEntity -> log.debug("cart entity: {}", cartEntity))
                .flatMap(cartRepository::save);
    }

    /**
     * Populate cart items with their prices and their final totals
     * with discounts applied
     * It request the items unit prices and discounts from the catalog-service
     *
     * @param cart cart with items (productId, and quantity)
     * @return the passed cart with prices populated and discounts applied
     */
    public Mono<Cart> reprice(Cart cart) {
        if (cart.getItems().isEmpty()) {
            Cart uc = cart.toBuilder()
                    .finalTotal(BigDecimal.ZERO)
                    .totalDiscount(BigDecimal.ZERO)
                    .originalTotal(BigDecimal.ZERO)
                    .build();
            log.debug("empty cart: {}", cart);
            return Mono.just(uc);
        }
        PricesRequest pricesRequest = cartMapper.toPricesRequest(cart);
        Mono<PricesResponse> pricesResponseMono = pricesService.fetchPrices(pricesRequest);

        return pricesResponseMono.map((pricesResponse -> {
            log.debug("Prices Response: {}", pricesResponse);
            for (PriceItemResponse pr : pricesResponse.items()) {
                log.debug("Price Item: {}", pr);
            }
            List<CartItem> items =
                    cartMapper.fromPriceItemsResponse(pricesResponse.items());
            Cart uc = cart.toBuilder()
                    .items(items)
                    .finalTotal(pricesResponse.finalTotal())
                    .totalDiscount(pricesResponse.totalDiscount())
                    .originalTotal(pricesResponse.originalTotal())
                    .build();
            log.debug("cart order: {}", cart);
            return uc;
        }));
    }


    private Mono<CartEntity> getOrCreateCart(CartOwner owner, UUID storeId) {
        Mono<CartEntity> cartMono = owner.isGuest() ?
                cartRepository.findByGuestIdAndStoreId(owner.guestId(), storeId) :
                cartRepository.findByUserIdAndStoreId(owner.userId(), storeId);

        return cartMono.switchIfEmpty(Mono.defer(() -> createEmptyCart(owner, storeId)));
    }

    private Mono<CartEntity> getOrCreateCart(UUID userId, UUID storeId) {
        return getOrCreateCart(CartOwner.ofUser(userId), storeId);
    }

    private Mono<CartEntity> createEmptyCart(CartOwner owner, UUID storeId) {
        CartEntity cart = new CartEntity();
        if (owner.isGuest()) {
            cart.setGuestId(owner.guestId());
        } else {
            cart.setUserId(owner.userId());
        }
        cart.setStoreId(storeId);
        cart.setItems(List.of());
        return cartRepository.save(cart);
    }


    public Mono<ServiceResult<CartResponse>> clearCart(CartOwner owner, UUID storeId) {
        return getOrCreateCart(owner, storeId)
                .map(this::clearCart)
                .doOnNext(cartEntity -> log.debug("cart entity after clearing: {}", cartEntity))
                .flatMap(cartRepository::save)
                .map(cartMapper::fromEntityToResponse)
                .map((cr) -> owner.isGuest() ? ServiceResult.forGuest(cr, owner.guestId()) :
                        ServiceResult.forUser(cr));
    }

    public Mono<Void> clearCart(UUID cartId){
        return cartRepository.findById(cartId)
                .map(this::clearCart)
                .doOnNext(cartEntity -> log.debug("cart entity cleared: {}", cartEntity))
                .flatMap(cartRepository::save)
                .then();
    }

    private CartEntity clearCart(CartEntity cart) {
        cart.setItems(List.of());
        cart.setOriginalTotal(BigDecimal.ZERO);
        cart.setFinalTotal(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
        return cart;
    }

    public Mono<CartResponse> mergeGuestCart(UUID userId, UUID guestId, UUID storeId) {
        if (guestId == null) {
            return getCart(userId, storeId);
        }

        return cartRepository.findByGuestIdAndStoreId(guestId, storeId)
                .flatMap(guestCartEntity -> {
                    Cart guestCart = cartMapper.fromEntityToDomain(guestCartEntity);

                    return getOrCreateCart(userId, storeId)
                            .map(cartMapper::fromEntityToDomain)
                            .map(userCart -> userCart.merge(guestCart.getItems()))
                            .flatMap(this::repriceAndSave)
                            .flatMap(saved -> cartRepository.delete(guestCartEntity)
                                    .thenReturn(saved));
                })
                .map(cartMapper::fromEntityToResponse)
                .switchIfEmpty(getCart(userId, storeId));
    }
}