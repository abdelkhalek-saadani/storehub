package com.abdelkhalek.storehub.order.pricing.infrastructure.mappers;


import com.abdelkhalek.storehub.order.pricing.domain.models.UnitPrice;
import com.abdelkhalek.storehub.order.pricing.infrastructure.models.UnitPriceResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitPriceResponseMapper {

    UnitPriceResponse fromUnitPrice(UnitPrice unitPrice);

    UnitPrice toUnitPrice(UnitPriceResponse unitPriceResponse);

    List<UnitPriceResponse> fromUnitPrices(List<UnitPrice> unitPrices);

    List<UnitPrice> toUnitPrices(List<UnitPriceResponse> unitPricesResponse);

}
