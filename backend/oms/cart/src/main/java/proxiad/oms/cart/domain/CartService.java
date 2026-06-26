package proxiad.oms.cart.domain;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.models.CartItem;
import proxiad.oms.cart.domain.models.Money;

import java.util.UUID;

public interface CartService {

    public Cart reset(UUID cartId);
    // public void addItem(Cart cart, CartItem item);
    // public void removeItem(Cart cart, CartItem item);
    public Cart setQuantity(UUID cartId, UUID productId, int quantity);
    public Cart setTotal(UUID cartId, Money total);
    public Cart getCart(UUID cartId);
    public Cart create(Cart cart);
    public Cart delete(UUID cartId);
}
