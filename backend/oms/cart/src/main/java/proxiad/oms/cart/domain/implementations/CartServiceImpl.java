package proxiad.oms.cart.domain.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proxiad.oms.cart.domain.CartService;
import proxiad.oms.cart.domain.config.Loggable;
import proxiad.oms.cart.domain.models.*;
import proxiad.oms.cart.domain.spi.CartRepository;
import proxiad.oms.cart.domain.spi.EventPublisher;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Loggable
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    EventPublisher eventPublisher;

    @Override
    @Transactional
    public Cart setTotal(UUID cartId, Money total) throws EntityNotFoundException {
        Cart cart = getCart(cartId);
        cart.setTotal(total);
        cartRepository.save(cart);

        return cart;
    }

    @Override
    public Cart getCart(UUID cartId) {

        Optional<Cart> cart = cartRepository.findById(cartId);
        Cart foundCart;
        if (cart.isPresent()) {
            foundCart = cart.get();
        } else {
            throw new EntityNotFoundException("Cart not found with ID: " + cartId);
        }

        return foundCart;
    }

    @Override
    public Cart create(Cart cart) {

        return cartRepository.save(cart);
    }

    @Override
    public Cart reset(UUID cartId) {

        Cart cart = getCart(cartId);
        cart.setTotal(new Money());
        cart.getItems().clear();

        return cartRepository.save(cart);
    }

    @Override
    public Cart delete(UUID cartId) {

        Cart cart = getCart(cartId);
        cartRepository.deleteById(cartId);

        return cart;
    }


    private void addItem(Cart cart, CartItem item) {

        cart.getItems().add(item);
        item.setCart(cart);

        eventPublisher.publish(
                new ItemAddedEvent(
                        cart.getId(),
                        cart.getItems().stream()
                                .map(CartItem::new).collect(Collectors.toList()) // this to set the cart null, we dont need it to be sent plus avoid infinite reference
                )
        );


    }

    private void removeItem(Cart cart, CartItem item) {

        cart.getItems().removeIf(cartItem -> cartItem.getProductId().equals(item.getProductId()));
        item.setCart(null);
        eventPublisher.publish(
                new ItemRemovedEvent(
                    cart.getId(),
                    cart.getItems().stream()
                            .map(CartItem::new).collect(Collectors.toList()) // deep copy for each cartItem
                )
        );

    }

    @Override
    @Transactional
    public Cart setQuantity(UUID cartId, UUID productId, int quantity) {
        Cart cart = getCart(cartId);
        CartItem cartItem = new CartItem(productId, quantity);
        CartItem existentItem;
        if (!(cart.getItems().contains(cartItem)) && quantity > 0) {
            addItem(cart, cartItem);
        } else if ((cart.getItems().contains(cartItem)) && quantity <= 0) {
            removeItem(cart, cartItem);
        } else {
            existentItem = cart.getItems().stream().filter(item -> item.getProductId().equals(productId)).findFirst().orElse(null);
            if (existentItem != null) {
                existentItem.setQuantity(quantity);
                eventPublisher.publish(
                        new ItemQuantityChangedEvent(
                                cart.getId(),
                                cart.getItems().stream()
                                        .map(CartItem::new).collect(Collectors.toList()) // this to set the cart null, we dont need it to be sent plus avoid infinite reference
                        )
                );
            }
        }
        return cartRepository.save(cart);
    }
}
