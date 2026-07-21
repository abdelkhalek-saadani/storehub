package com.abdelkhalek.storehub.order.cart.services.cart;

import com.abdelkhalek.storehub.order.cart.CartMapper;
import com.abdelkhalek.storehub.order.cart.domain.Cart;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.dtos.AddItemRequest;
import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.services.price.PriceItemResponse;
import com.abdelkhalek.storehub.order.cart.services.price.PricesRequest;
import com.abdelkhalek.storehub.order.cart.services.price.PricesResponse;
import com.abdelkhalek.storehub.order.cart.services.price.PricesService;
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


    public Mono<CartResponse> getCart(UUID userId, UUID storeId) {
        return cartRepository.findByUserIdAndStoreId(userId, storeId)
                .switchIfEmpty(createEmptyCart(userId, storeId))
                .map(cartMapper::fromEntityToResponse);
    }

    public Mono<CartResponse> upsertItems(UUID userId, UpdateCartRequest request) {
        return getOrCreateCart(userId, request.storeId())
                .map(cartMapper::fromEntityToDomain)
                .map(cart -> {
                    List<CartItem> items =
                            request.items().stream().map((item) -> new CartItem(item.productId(),
                                    item.quantity())).toList();
                    return cart.upsert(items);
                })
                .flatMap(this::repriceAndSave)
                .map(cartMapper::fromEntityToResponse);
    }


    public Mono<CartResponse> upsertItem(UUID userId, AddItemRequest request) {
        return getOrCreateCart(userId, request.storeId())
                .map(cartMapper::fromEntityToDomain)
                .map(cart -> cart.upsert(new CartItem(request.productId(), request.quantity())))
                .flatMap(this::repriceAndSave)
                .map(cartMapper::fromEntityToResponse);
    }


    public Mono<PricesResponse> quote(UpdateCartRequest request) {
        if (request.items().isEmpty()) {
            return Mono.just(PricesResponse.empty());
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
    private Mono<Cart> reprice(Cart cart) {
        PricesRequest pricesRequest = cartMapper.toPricesRequest(cart);
        Mono<PricesResponse> pricesResponseMono = pricesService.fetchPrices(pricesRequest);

        return pricesResponseMono.map((pricesResponse -> {
            log.debug("Prices Response: {}", pricesResponse);
            for (PriceItemResponse pr : pricesResponse.getItems()) {
                log.debug("Price Item: {}", pr);
            }
            List<CartItem> items =
                    cartMapper.fromPriceItemsResponse(pricesResponse.getItems());

            cart.setItems(items);
            cart.setFinalTotal(pricesResponse.getFinalTotal());
            cart.setTotalDiscount(pricesResponse.getTotalDiscount());
            cart.setOriginalTotal(pricesResponse.getOriginalTotal());
            log.debug("cart order: {}", cart);
            return cart;
        }));
    }


    private Mono<CartEntity> getOrCreateCart(UUID userId, UUID storeId) {
        return cartRepository.findByUserIdAndStoreId(userId, storeId)
                .switchIfEmpty(createEmptyCart(userId, storeId));
    }

    private Mono<CartEntity> createEmptyCart(UUID userId, UUID storeId) {
        CartEntity cart = new CartEntity();
        cart.setUserId(userId);
        cart.setStoreId(storeId);
        cart.setItems(List.of());
        return cartRepository.save(cart);
    }


    public Mono<CartResponse> clearCart(UUID userId, UUID storeId) {
        return getOrCreateCart(userId, storeId)
                .map(this::clearCart)
                .doOnNext(cartEntity -> log.debug("cart entity after clearing: {}", cartEntity))
                .flatMap(cartRepository::save)
                .map(cartMapper::fromEntityToResponse);
    }

    private CartEntity clearCart(CartEntity cart) {
        cart.setItems(List.of());
        cart.setOriginalTotal(BigDecimal.ZERO);
        cart.setFinalTotal(BigDecimal.ZERO);
        cart.setTotalDiscount(BigDecimal.ZERO);
        return cart;
    }
}