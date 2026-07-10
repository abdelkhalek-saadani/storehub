package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.dtos.CartResponse;
import com.abdelkhalek.storehub.order.cart.dtos.GetCartRequest;
import com.abdelkhalek.storehub.order.cart.dtos.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.services.cart.CartService;
import com.abdelkhalek.storehub.order.cart.services.price.PricesResponse;
import com.abdelkhalek.storehub.order.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public Mono<CartResponse> getCart(@RequestBody @Valid GetCartRequest request) {
        return userService.getCurrentUserId()
                .flatMap( (userId) -> cartService.getCart(userId, request));
    }


    @PostMapping("items")
    public Mono<CartResponse> upsertItems(@RequestBody @Valid UpdateCartRequest request) {
        return userService.getCurrentUserId()
                .flatMap((userId) -> cartService.upsertItems(userId, request));
    }








    @PostMapping("quote")
    public Mono<PricesResponse> quote(@RequestBody @Valid UpdateCartRequest request) {
        // no auth required, guest endpoint, stateless
        return cartService.quote(request);
    }
}