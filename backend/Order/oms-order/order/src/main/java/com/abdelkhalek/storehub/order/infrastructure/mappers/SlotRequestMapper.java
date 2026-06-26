package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.Slot;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.SlotRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SlotRequestMapper {

    SlotRequest fromSlot(Slot slot);

    Slot toSlot(SlotRequest slotRequest);

}
