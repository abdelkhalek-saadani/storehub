package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.dto.CartResponse;
import com.abdelkhalek.storehub.order.cart.dto.UpdateCartRequest;
import com.abdelkhalek.storehub.order.cart.service.CartService;
import com.abdelkhalek.storehub.order.cart.service.OwnerResolver;
import com.abdelkhalek.storehub.order.shared.dto.PricesResponse;
import com.abdelkhalek.storehub.order.shared.model.ServiceResult;
import com.abdelkhalek.storehub.order.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart", description = "Shopping cart operations for guest and authenticated users")
public class CartController {

    private final CartService cartService;
    private final OwnerResolver ownerResolver;
    private final UserService userService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the current cart for a guest or authenticated user",
            description = "For guest, set the X-Guest-Id header")
    @GetMapping
    public Mono<CartResponse> getCart(@RequestHeader(value = "X-Guest-Id", required = false) UUID guestId,
                                      @RequestParam @NotNull UUID storeId, ServerWebExchange exchange) {
        return ownerResolver.resolveOwner(guestId)
                .flatMap(owner -> cartService.getCart(owner, storeId))
                .doOnNext(result -> echoIfGuest(exchange, result))
                .map(ServiceResult::body);

    }


    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add or update items in the cart",
            description = "For guest, set the X-Guest-Id header")
    @PostMapping("items")
    public Mono<CartResponse> upsertItems(@RequestHeader(value = "X-Guest-Id", required = false) UUID guestId,
                                          @RequestBody @Valid UpdateCartRequest request,
                                          ServerWebExchange exchange) {

        return ownerResolver.resolveOwner(guestId)
                .flatMap((owner) -> cartService.upsertItems(owner, request))
                .doOnNext(result -> echoIfGuest(exchange, result))
                .map(ServiceResult::body);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Clear all items from the cart",
            description = "For guest, set the X-Guest-Id header")
    @DeleteMapping
    public Mono<CartResponse> clearCart(@RequestHeader(value = "X-Guest-Id", required = false) UUID guestId,
                                        @RequestParam @NotNull UUID storeId, ServerWebExchange exchange) {

        return ownerResolver.resolveOwner(guestId)
                .flatMap((owner) -> cartService.clearCart(owner, storeId))
                .doOnNext(result -> echoIfGuest(exchange, result)) // echo back if new
                .map(ServiceResult::body);
    }


    @Operation(summary = "Get a price quote for a cart",
            description = "Stateless, no authentication required.")
    @PostMapping("quote")
    public Mono<PricesResponse> quote(@RequestBody @Valid UpdateCartRequest request) {
        // no auth required, guest endpoint, stateless
        return cartService.quote(request);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Merge a guest cart into the authenticated user's cart")
    @PostMapping("merge")
    public Mono<CartResponse> mergeGuestCart(@RequestParam @NotNull UUID storeId,
                                             @RequestHeader(value = "X-Guest-Id", required =
                                                     false) UUID guestId) {
        return userService.getCurrentUserId()
                .flatMap(userId -> cartService.mergeGuestCart(userId, guestId, storeId));
    }

    private void echoIfGuest(ServerWebExchange exchange, ServiceResult<?> result) {
        if (result.isGuest()) {
            exchange.getResponse().getHeaders().set("X-Guest-Id", result.guestId().toString());
        }
    }
}