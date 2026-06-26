package proxiad.oms.cart.domain.models;

import java.util.List;
import java.util.UUID;

public class ItemQuantityChangedEvent extends DomainEvent {

    private UUID cartId;
    private List<CartItem> cartItems;

    public ItemQuantityChangedEvent(UUID cartId, List<CartItem> cartItems) {
        this.cartId = cartId;
        this.cartItems = cartItems;
    }

    public UUID getCartId() {
        return cartId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

}