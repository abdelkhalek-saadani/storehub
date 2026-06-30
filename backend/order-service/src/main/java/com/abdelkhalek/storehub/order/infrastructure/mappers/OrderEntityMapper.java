package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.infrastructure.models.order.*;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.infrastructure.models.order.*;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface OrderEntityMapper {

    OrderEntity fromOrder(Order order);
    Order toOrder(OrderEntity orderEntity);

    AddressEntity fromAddress(Address address);
    Address toAddress(AddressEntity addressEntity);
    CartItemEntity fromCartItem(CartItem cartItem);
    CartItem toCartItem(CartItemEntity cartItemEntity);
    MoneyEntity fromMoney(Money money);
    Money toMoney(MoneyEntity moneyEntity);
    SlotEntity fromSlot(Slot slot);
    Slot toSlot(SlotEntity slotEntity);
}
