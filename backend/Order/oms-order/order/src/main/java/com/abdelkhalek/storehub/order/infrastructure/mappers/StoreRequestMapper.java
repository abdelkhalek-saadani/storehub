package com.abdelkhalek.storehub.order.infrastructure.mappers;

import com.abdelkhalek.storehub.order.domain.models.Store;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoreRequestMapper {

    StoreRequest fromStore(Store store);

    Store toStore(StoreRequest storeRequest);

}
