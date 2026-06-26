package proxiad.oms.pricing.infrastructure.mappers;


import org.mapstruct.Mapper;
import proxiad.oms.pricing.domain.models.UnitPrice;
import proxiad.oms.pricing.infrastructure.models.UnitPriceResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitPriceResponseMapper {

    UnitPriceResponse fromUnitPrice(UnitPrice unitPrice);

    UnitPrice toUnitPrice(UnitPriceResponse unitPriceResponse);

    List<UnitPriceResponse> fromUnitPrices(List<UnitPrice> unitPrices);

    List<UnitPrice> toUnitPrices(List<UnitPriceResponse> unitPricesResponse);

}
