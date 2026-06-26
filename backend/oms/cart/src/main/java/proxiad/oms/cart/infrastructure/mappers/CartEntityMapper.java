package proxiad.oms.cart.infrastructure.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.models.CartItem;
import proxiad.oms.cart.domain.models.Money;
import proxiad.oms.cart.infrastructure.models.CartEntity;
import proxiad.oms.cart.infrastructure.models.CartItemEntity;
import proxiad.oms.cart.infrastructure.models.MoneyEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {MoneyEntityMapper.class})
public interface CartEntityMapper {

//    CartEntityMapper INSTANCE = Mappers.getMapper( CartEntityMapper.class );

    @Mapping(target = "items", qualifiedByName = "toCartItemListWithContext")
    Cart toCart(CartEntity cartEntity, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "items", qualifiedByName = "toCartItemEntityListWithContext")
    CartEntity fromCart(Cart cart, @Context CycleAvoidingMappingContext context);

    // Cart Item mapper


    CartItem toCartItem(CartItemEntity entity, @Context CycleAvoidingMappingContext context);


    CartItemEntity fromCartItem(CartItem cartItem, @Context CycleAvoidingMappingContext context);


    @Named("toCartItemListWithContext")
    default List<CartItem> toCartItemList(List<CartItemEntity> entities, @Context CycleAvoidingMappingContext context) {
        return entities.stream()
                .map(entity -> toCartItem(entity, context))
                .collect(Collectors.toList());
    }

    @Named("toCartItemEntityListWithContext")
    default List<CartItemEntity> toCartItemEntityList(List<CartItem> cartItems, @Context CycleAvoidingMappingContext context) {
        return cartItems.stream()
                .map(cartItem -> fromCartItem(cartItem, context))
                .collect(Collectors.toList());
    }

}