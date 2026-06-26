package proxiad.oms.cart.infrastructure.implementations;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import proxiad.oms.cart.domain.config.Loggable;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.spi.CartRepository;
import proxiad.oms.cart.infrastructure.exceptions.RepositoryException;
import proxiad.oms.cart.infrastructure.mappers.CartEntityMapper;
import proxiad.oms.cart.infrastructure.mappers.CycleAvoidingMappingContext;
import proxiad.oms.cart.infrastructure.models.CartEntity;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@Loggable
public class CartRepositoryAdapter implements CartRepository {

    private final CartJpaRepository cartRepositoryImpl;

    @Autowired
    CartEntityMapper cartEntityMapper;


    public CartRepositoryAdapter(CartJpaRepository cartRepositoryImpl) {
        this.cartRepositoryImpl = cartRepositoryImpl;
    }

    @Override
    public Optional<Cart> findById(UUID cartId) {

        try {
            return cartRepositoryImpl.findById(cartId)
                    .map(cartEntity -> cartEntityMapper.toCart(cartEntity,new CycleAvoidingMappingContext()));
        } catch (Exception e) {
            throw new EntityNotFoundException("Cart not found with ID: "+cartId, e);
        }
    }

    @Override
    public Cart save(Cart cart) {

        CartEntity cartEntity = cartEntityMapper.fromCart(
                cart,
                new CycleAvoidingMappingContext()
        );
        CartEntity savedEntity = cartRepositoryImpl.save(cartEntity);
        try {
            return cartEntityMapper.toCart(
                    savedEntity,
                    new CycleAvoidingMappingContext()
            );
        } catch (Exception e) {
            throw new RepositoryException("Error saving cart", e);
        }
    }

    @Override
    public Cart deleteById(UUID cartId) {
        try {
            Cart deletedCart = findById(cartId).orElse(null);
            cartRepositoryImpl.deleteById(cartId);
            return deletedCart;
        } catch (Exception e) {
            throw new RepositoryException("Error deleting cart by ID", e);
        }
    }
}
