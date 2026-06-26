package proxiad.oms.cart.application.implementations;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import proxiad.oms.cart.application.CartServiceAdapter;
import proxiad.oms.cart.application.model.CartDTO;
import proxiad.oms.cart.application.model.CreateCartDTO;
import proxiad.oms.cart.application.model.ProductQuantityDTO;

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart API", description = "Endpoints for managing carts and items.")
public class CartController {

    // 1. Create a new cart
    @PostMapping
    @Operation(summary = "Create a new cart", description = "Creates an empty cart and returns its ID.")
    public String createCart() {
        return "Cart created (mock)";
    }

    // 2. Get cart by ID
    @GetMapping("/{cartId}")
    @Operation(summary = "Get cart details", description = "Retrieves details of a specific cart.")
    public String getCartById(
            @Parameter(description = "Cart ID") @PathVariable String cartId) {
        return "Details of cart " + cartId + " (mock)";
    }

    // 3. Get cart by user ID
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cart details by user", description = "Retrieves the cart associated with a specific user.")
    public String getCartByUser(
            @Parameter(description = "User ID") @PathVariable String userId) {
        return "Cart of user " + userId + " (mock)";
    }

    // 4. Add an item to the cart
    @PostMapping("/{cartId}/items")
    @Operation(summary = "Add an item to the cart", description = "Adds a product to the cart with a given quantity.")
    public String addItemToCart(
            @Parameter(description = "Cart ID") @PathVariable String cartId,
            @RequestParam String productId,
            @RequestParam int quantity) {
        return "Added " + quantity + " of product " + productId + " to cart " + cartId + " (mock)";
    }

    // 5. Update an item quantity
    @PutMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "Update an item in the cart", description = "Updates the quantity of a specific item in the cart.")
    public String updateItemQuantity(
            @Parameter(description = "Cart ID") @PathVariable String cartId,
            @Parameter(description = "Item ID") @PathVariable String itemId,
            @RequestParam int quantity) {
        return "Updated item " + itemId + " to quantity " + quantity + " in cart " + cartId + " (mock)";
    }

    // 6. Remove an item from the cart
    @DeleteMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "Remove an item from the cart", description = "Removes a specific item from the cart.")
    public String removeItemFromCart(
            @Parameter(description = "Cart ID") @PathVariable String cartId,
            @Parameter(description = "Item ID") @PathVariable String itemId) {
        return "Removed item " + itemId + " from cart " + cartId + " (mock)";
    }

    // 7. Increment an item quantity
    @PatchMapping("/{cartId}/items/{itemId}/increment")
    @Operation(summary = "Increment item quantity", description = "Increases the quantity of a specific item by 1.")
    public String incrementItemQuantity(
            @Parameter(description = "Cart ID") @PathVariable String cartId,
            @Parameter(description = "Item ID") @PathVariable String itemId) {
        return "Incremented quantity of item " + itemId + " in cart " + cartId + " (mock)";
    }

    // 8. Decrement an item quantity
    @PatchMapping("/{cartId}/items/{itemId}/decrement")
    @Operation(summary = "Decrement item quantity", description = "Decreases the quantity of a specific item by 1.")
    public String decrementItemQuantity(
            @Parameter(description = "Cart ID") @PathVariable String cartId,
            @Parameter(description = "Item ID") @PathVariable String itemId) {
        return "Decremented quantity of item " + itemId + " in cart " + cartId + " (mock)";
    }

    @PutMapping("/reset/{id}")
    @Operation(summary = "Reset a cart", description = "Resets a cart to its initial empty state.")
    public String resetCart(
            @Parameter(description = "Cart ID") @PathVariable("id") String cartId) {
        return "Cart " + cartId + " has been reset (mock)";
    }
}

//@Validated
//@RestController
//@RequestMapping("cart")
//class CartController {
//
//    private final CartServiceAdapter cartServiceAdapter;
//
//    CartController(CartServiceAdapter cartServiceAdapter) {
//        this.cartServiceAdapter = cartServiceAdapter;
//    }
//
//    // Create : send CreateCartDTO(contains just customerId) without id, returns CartDTO
//    @PostMapping()
//    ResponseEntity<CartDTO> create(@Valid @RequestBody CreateCartDTO createCartDTO) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartServiceAdapter.create(createCartDTO));
//    }
//
//    // Set quantity: send productId along with its quantity productQuantityDTO
//    @PutMapping()
//    ResponseEntity<CartDTO> updateQuantity(@Valid @RequestBody ProductQuantityDTO productQuantityDTO) {
//        return ResponseEntity.ok(cartServiceAdapter.setQuantity(productQuantityDTO));
//    }
//
//    // Reset: /cart/reset/id returns CartDTO
//    @PutMapping("reset/{id}")
//    ResponseEntity<CartDTO> reset(@PathVariable("id") UUID cartId) {
//        return ResponseEntity.ok(cartServiceAdapter.reset(cartId));
//    }
//
//    // delete: /cart/id with Delete returns CartDTO
//    @DeleteMapping("{id}")
//    ResponseEntity<CartDTO> delete(@PathVariable("id") UUID cartId) {
//        return ResponseEntity.ok(cartServiceAdapter.delete(cartId));
//    }
//
//    // get cart:  returns CartDTO
//    @GetMapping("{id}")
//    ResponseEntity<CartDTO> get(@PathVariable("id") UUID cartId) {
//        CartDTO cartDTO = cartServiceAdapter.get(cartId);
//        return ResponseEntity.ok(cartDTO);
//    }

//}