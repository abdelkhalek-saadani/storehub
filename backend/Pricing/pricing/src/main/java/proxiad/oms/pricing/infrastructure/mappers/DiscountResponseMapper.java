package proxiad.oms.pricing.infrastructure.mappers;


import org.mapstruct.Mapper;
import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.infrastructure.models.DiscountResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountResponseMapper {

    DiscountResponse fromDiscount(Discount discount);

    Discount toDiscount(DiscountResponse discountResponse);

    List<DiscountResponse> fromDiscounts(List<Discount> discounts);

    List<Discount> toDiscounts(List<DiscountResponse> discountsResponse);

}