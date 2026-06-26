package proxiad.oms.pricing.infrastructure.implementations;

import org.springframework.stereotype.Service;
import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.domain.models.UnitPrice;
import proxiad.oms.pricing.domain.spi.ProductService;
import proxiad.oms.pricing.infrastructure.mappers.DiscountResponseMapper;
import proxiad.oms.pricing.infrastructure.mappers.UnitPriceResponseMapper;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductServiceAdapter implements ProductService {

    private final ExternalProductClient externalProductClient;
    private final DiscountResponseMapper discountResponseMapper;
    private final UnitPriceResponseMapper unitPriceResponseMapper;

    public ProductServiceAdapter(
            ExternalProductClient externalProductClient,
            UnitPriceResponseMapper unitPriceResponseMapper,
            DiscountResponseMapper discountResponseMapper) {
        this.externalProductClient = externalProductClient;
        this.unitPriceResponseMapper = unitPriceResponseMapper;
        this.discountResponseMapper = discountResponseMapper;
    }

    public Mono<List<UnitPrice>> getUnitPrices(List<String> productIds){
        return externalProductClient.getUnitPrices(productIds)
                .map(unitPriceResponseMapper::toUnitPrices);
    }

    public Mono<List<Discount>> getDiscounts(List<String> productIds){
        return externalProductClient.getDiscounts(productIds)
                .map(discountResponseMapper::toDiscounts);
    }


}
