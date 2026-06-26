package proxiad.oms.cart.infrastructure.mappers;

import org.mapstruct.Mapper;
import proxiad.oms.cart.domain.models.ItemAddedEvent;
import proxiad.oms.cart.domain.models.ItemQuantityChangedEvent;
import proxiad.oms.cart.domain.models.ItemRemovedEvent;
import com.proxiad.events.CartChangedEvent;

@Mapper(componentModel = "spring")

public interface CartChangedEventMapper {

    CartChangedEvent fromItemAddedEvent(ItemAddedEvent itemAddedEvent);

    CartChangedEvent fromItemRemovedEvent(ItemRemovedEvent itemRemovedEvent);

    CartChangedEvent fromItemQuantityChangedEvent(ItemQuantityChangedEvent itemQuantityChangedEvent);

}
