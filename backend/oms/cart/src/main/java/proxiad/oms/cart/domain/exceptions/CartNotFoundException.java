package proxiad.oms.cart.domain.exceptions;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(UUID id) {
        super("Cart with id " + id + " not found");
    }
}
