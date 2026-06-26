package proxiad.oms.cart.domain.spi;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import proxiad.oms.cart.domain.models.Cart;

import java.util.Optional;
import java.util.UUID;

@Component
public interface CartRepository {
    public Optional<Cart> findById(UUID cartId);
    public Cart save(Cart cart);
    public Cart deleteById(UUID cartId);
}
