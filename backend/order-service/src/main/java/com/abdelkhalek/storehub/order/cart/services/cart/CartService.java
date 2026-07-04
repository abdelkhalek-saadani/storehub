package com.abdelkhalek.storehub.order.cart.services.cart;

import com.abdelkhalek.storehub.order.cart.CartMapper;
import com.abdelkhalek.storehub.order.cart.domain.CartItem;
import com.abdelkhalek.storehub.order.cart.dtos.AddItemRequest;
import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.GetCartRequest;
import com.abdelkhalek.storehub.order.cart.dtos.GuestCartRequest;
import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.services.price.PriceItemResponse;
import com.abdelkhalek.storehub.order.cart.services.price.PricesRequest;
import com.abdelkhalek.storehub.order.cart.services.price.PricesResponse;
import com.abdelkhalek.storehub.order.cart.services.price.PricesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final PricesService pricesService;


    public Mono<CartResponse> getCart(UUID userId, GetCartRequest request) {
        return cartRepository.findByUserIdAndStoreId(userId, request.storeId())
                .switchIfEmpty(createEmptyCart(userId, request.storeId()))
                .map(cartMapper::fromEntityToResponse);
    }

    public Mono<CartResponse> upsertItem(UUID userId, AddItemRequest request) {
        return getOrCreateCart(userId, request.storeId())
                .map(cartMapper::fromEntityToDomain)
                .map(cart -> cart.upsert(new CartItem(request.productId(), request.quantity())))
                .flatMap(cart -> {
                    PricesRequest pricesRequest = cartMapper.toPricesRequest(cart);
                    Mono<PricesResponse> pricesResponseMono = pricesService.fetchPrices(pricesRequest);

                    return pricesResponseMono.map((pricesResponse -> {
                        log.info("Prices Response: {}", pricesResponse);
                        for (PriceItemResponse pr : pricesResponse.getItems()) {
                            log.info("Price Item: {}", pr);
                        }
                        List<CartItem> items =
                                cartMapper.fromPriceItemsResponse(pricesResponse.getItems());
                        cart.setItems(items);
                        cart.setFinalTotal(pricesResponse.getFinalTotal());
                        cart.setTotalDiscount(pricesResponse.getTotalDiscount());
                        cart.setOriginalTotal(pricesResponse.getOriginalTotal());
                        log.info("cart domain: {}", cart);
                        return cart;
                    }));
                })
                .map(cart -> {
                    CartEntity cartEntity = cartMapper.toEntity(cart);
                    cartEntity.setStoreId(request.storeId());
                    cartEntity.setUserId(userId);
                    log.info("cart entity: {}", cartEntity);
                    return cartEntity;
                })
                .flatMap(cartRepository::save)
                .map(cartMapper::fromEntityToResponse);
    }


    public Mono<PricesResponse> quote(GuestCartRequest request) {
        if (request.items().isEmpty()) {
            return Mono.just(PricesResponse.empty());
        }
        return pricesService.fetchPrices(cartMapper.fromGuestCartRequestToPricesRequest(request));
    }

    // --- internal helpers ---

    private Mono<CartEntity> getOrCreateCart(UUID userId, UUID storeId) {
        return cartRepository.findByUserIdAndStoreId(userId, storeId)
                .switchIfEmpty(createEmptyCart(userId, storeId));
    }

    private Mono<CartEntity> createEmptyCart(UUID userId, UUID storeId) {
        CartEntity cart = new CartEntity();
        cart.setUserId(userId);
        cart.setStoreId(storeId);
        return cartRepository.save(cart);
    }


}