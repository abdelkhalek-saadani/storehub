package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.order.models.Address;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.AddressRequest;
import org.mapstruct.Mapper;

@Mapper( componentModel = "spring")
public interface AddressRequestMapper {


    AddressRequest fromAddress(Address address);
}
