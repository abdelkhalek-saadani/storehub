package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.dtos.AddItemRequest;
import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.GetCartRequest;
import com.abdelkhalek.storehub.order.cart.dtos.GuestCartRequest;
import com.abdelkhalek.storehub.order.cart.services.PricesResponse;
import com.abdelkhalek.storehub.order.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public Mono<CartResponse> getCart(@RequestBody @Valid GetCartRequest request) {
        return userService.getCurrentUserId()
                .flatMap( (userId) -> cartService.getCart(userId, request));
    }

    @PostMapping("/items")
    public Mono<CartResponse> upsertItem(@RequestBody @Valid AddItemRequest request) {
        log.debug("Add item request: {}", request);
        return userService.getCurrentUserId()
                .flatMap((userId) -> cartService.upsertItem(userId, request));
    }




    @PostMapping("/quote")
    public Mono<PricesResponse> quote(@RequestBody @Valid GuestCartRequest request) {
        // no auth required, guest endpoint, stateless
        return cartService.quote(request);
    }
}