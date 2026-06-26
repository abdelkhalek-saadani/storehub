package proxiad.oms.cart.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proxiad.oms.cart.application.mappers.CartDTOMapper;
import proxiad.oms.cart.application.model.*;
import proxiad.oms.cart.domain.CartService;
import proxiad.oms.cart.domain.config.Loggable;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.application.mappers.CycleAvoidingMappingContext;


import java.util.UUID;

@Service
@Slf4j
@Loggable
public class CartServiceAdapter {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartDTOMapper cartDTOMapper;

    public CartDTO create(CreateCartDTO createCartDTO) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCustomerId(createCartDTO.getCustomerId());
        Cart cart = cartDTOMapper.toCart(cartDTO, new CycleAvoidingMappingContext());

        //maybe I'll change the create argument to just customerId
        Cart createdCart = cartService.create(cart);

        return cartDTOMapper.fromCart(createdCart);
    }

    public CartDTO setQuantity(ProductQuantityDTO productQuantityDTO){

        UUID cartId = UUID.fromString(productQuantityDTO.getCartId());
        UUID productId = UUID.fromString(productQuantityDTO.getProductId());
        int quantity = productQuantityDTO.getQuantity();
        Cart updatedCart = cartService.setQuantity(cartId,productId,quantity);


        return cartDTOMapper.fromCart(updatedCart);
    }

    public CartDTO reset(UUID cartId) {

        Cart cart = cartService.reset(cartId);
        return cartDTOMapper.fromCart(cart);
    }

    public CartDTO delete(UUID cartId) {
        Cart cart = cartService.delete(cartId);
        return cartDTOMapper.fromCart(cart);

    }

    public CartDTO get(UUID cartId) {
        Cart fetchedCart = cartService.getCart(cartId);
        return cartDTOMapper.fromCart(fetchedCart);

    }



}
