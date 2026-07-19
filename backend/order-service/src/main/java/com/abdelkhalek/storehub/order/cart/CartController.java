package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.services.cart.CartService;
import com.abdelkhalek.storehub.order.cart.services.price.PricesResponse;
import com.abdelkhalek.storehub.order.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public Mono<CartResponse> getCart(@RequestParam @NotNull UUID storeId) {
        return userService.getCurrentUserId()
                .flatMap( (userId) -> cartService.getCart(userId, storeId));
    }


    @PostMapping("items")
    public Mono<CartResponse> upsertItems(@RequestBody @Valid UpdateCartRequest request) {
        return userService.getCurrentUserId()
                .flatMap((userId) -> cartService.upsertItems(userId, request));
    }

    @DeleteMapping
    public Mono<CartResponse> clearCart(@RequestParam @NotNull UUID storeId) {
        return userService.getCurrentUserId()
                .flatMap((userId) -> cartService.clearCart(userId,storeId));
    }








    @PostMapping("quote")
    public Mono<PricesResponse> quote(@RequestBody @Valid UpdateCartRequest request) {
        // no auth required, guest endpoint, stateless
        return cartService.quote(request);
    }
}