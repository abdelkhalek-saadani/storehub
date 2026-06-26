package proxiad.oms.pricing.application.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import com.proxiad.events.CartChangedEvent;
import com.proxiad.events.CartItemEvent;
import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.Item;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CartChangedEventMapper {

//    CartEntityMapper INSTANCE = Mappers.getMapper( CartEntityMapper.class );

    @Mapping(source="cartItems",target = "items", qualifiedByName = "toItemList")
    Cart toCart(CartChangedEvent cartChangedEvent);

    @Mapping(source="items",target = "cartItems", qualifiedByName = "toCartItemEventList")
    CartChangedEvent fromCart(Cart cart);

    // Cart Item mapper

    CartItemEvent fromItem(Item item);

    Item toItem(CartItemEvent cartItemEvent);


    @Named("toItemList")
    default List<Item> toCartItemList(List<CartItemEvent> entities) {
        return entities.stream()
                .map(this::toItem)
                .collect(Collectors.toList());
    }

    @Named("toCartItemEventList")
    default List<CartItemEvent> toCartItemEntityList(List<Item> items) {
        return items.stream()
                .map(this::fromItem)
                .collect(Collectors.toList());
    }

}