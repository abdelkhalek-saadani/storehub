package proxiad.oms.cart.application.mappers;

import org.mapstruct.*;
import proxiad.oms.cart.application.model.CartDTO;
import proxiad.oms.cart.application.model.MoneyDTO;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.models.CartItem;
import proxiad.oms.cart.application.mappers.CycleAvoidingMappingContext;
import proxiad.oms.cart.application.model.CartItemDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;




    @Mapper(componentModel = "spring")
    public abstract class CartDTOMapper {


        @Mapping(target = "items", qualifiedByName = "toCartItemListWithContext")
        public abstract Cart toCart(CartDTO cartDTO, @Context CycleAvoidingMappingContext context);

        @Mapping(target = "items", qualifiedByName = "toCartItemDTOList")
        public abstract CartDTO fromCart(Cart cart);


        // Cart Item mapper
        public abstract CartItem toCartItem(CartItemDTO dto, @Context CycleAvoidingMappingContext context);

        @AfterMapping
        protected void addCartFromContext(@MappingTarget CartItem cartItem, @Context CycleAvoidingMappingContext context){
            // customized the CycleAvoidingMappingContext,
            // so I can get the stored cart directly (without key , from class field)
            cartItem.setCart(context.getStoredCart());
        }

        public abstract CartItemDTO fromCartItem(CartItem cartItem);

        @Named("toCartItemListWithContext")
        public List<CartItem> toCartItemList(List<CartItemDTO> dtos, @Context CycleAvoidingMappingContext context) {
            if (dtos==null) return new ArrayList<>();;
            return dtos.stream()
                    .map(dto -> toCartItem(dto, context))
                    .collect(Collectors.toList());
        }

        @Named("toCartItemDTOList")
        public List<CartItemDTO> toCartItemDTOList(List<CartItem> cartItems) {
            if (cartItems==null) return new ArrayList<>();
            return cartItems.stream()
                    .map(cartItem -> fromCartItem(cartItem))
                    .collect(Collectors.toList());
        }


    }

