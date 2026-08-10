package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.dto.CartResponse;
import com.abdelkhalek.storehub.order.cart.dto.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.service.CartService;
import com.abdelkhalek.storehub.order.cart.service.OwnerResolver;
import com.abdelkhalek.storehub.order.order.models.ServiceResult;
import com.abdelkhalek.storehub.order.shared.dto.PricesResponse;
import com.abdelkhalek.storehub.order.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OwnerResolver ownerResolver;
    private final UserService userService;

    @GetMapping
    public Mono<CartResponse> getCart(@RequestParam @NotNull UUID storeId, ServerWebExchange exchange) {
        String guestId = exchange.getRequest().getHeaders().getFirst("X-Guest-Id");
        return ownerResolver.resolveOwner(guestId)
                .flatMap(owner -> cartService.getCart(owner, storeId))
                .doOnNext(result -> {
                    if (result.isGuest())
                        exchange.getResponse().getHeaders()
                                .set("X-Guest-Id", result.guestId().toString()); // echo back if new
                })
                .map(ServiceResult::body);

    }


    @PostMapping("items")
    public Mono<CartResponse> upsertItems(@RequestBody @Valid UpdateCartRequest request, ServerWebExchange exchange) {
        String guestId = exchange.getRequest().getHeaders().getFirst("X-Guest-Id");

        return ownerResolver.resolveOwner(guestId)
                .flatMap((owner) -> cartService.upsertItems(owner, request))
                .doOnNext(result -> {
                    if (result.isGuest())
                        exchange.getResponse().getHeaders()
                                .set("X-Guest-Id", result.guestId().toString()); // echo back if new
                })
                .map(ServiceResult::body);
    }

    @DeleteMapping
    public Mono<CartResponse> clearCart(@RequestParam @NotNull UUID storeId, ServerWebExchange exchange) {
        String guestId = exchange.getRequest().getHeaders().getFirst("X-Guest-Id");

        return ownerResolver.resolveOwner(guestId)
                .flatMap((owner) -> cartService.clearCart(owner, storeId))
                .doOnNext(result -> {
                    if (result.isGuest())
                        exchange.getResponse().getHeaders()
                                .set("X-Guest-Id", result.guestId().toString()); // echo back if new
                })
                .map(ServiceResult::body);
    }


    @PostMapping("quote")
    public Mono<PricesResponse> quote(@RequestBody @Valid UpdateCartRequest request) {
        // no auth required, guest endpoint, stateless
        return cartService.quote(request);
    }

    @PostMapping("merge")
    public Mono<CartResponse> mergeGuestCart(@RequestParam @NotNull UUID storeId,
                                             @RequestHeader(value = "X-Guest-Id", required =
                                                     false) UUID guestId) {
        return userService.getCurrentUserId()
                .flatMap(userId -> cartService.mergeGuestCart(userId, guestId, storeId));
    }
}