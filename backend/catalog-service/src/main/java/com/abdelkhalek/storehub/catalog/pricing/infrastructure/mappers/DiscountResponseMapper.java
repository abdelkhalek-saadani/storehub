package com.abdelkhalek.storehub.catalog.pricing.infrastructure.mappers;


import com.abdelkhalek.storehub.order.pricing.domain.models.Discount;
import com.abdelkhalek.storehub.order.pricing.infrastructure.models.DiscountResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountResponseMapper {

    DiscountResponse fromDiscount(Discount discount);

    Discount toDiscount(DiscountResponse discountResponse);

    List<DiscountResponse> fromDiscounts(List<Discount> discounts);

    List<Discount> toDiscounts(List<DiscountResponse> discountsResponse);

}