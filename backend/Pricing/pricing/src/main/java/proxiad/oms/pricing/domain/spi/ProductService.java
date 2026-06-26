package proxiad.oms.pricing.domain.spi;

import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.domain.models.UnitPrice;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {


    public Mono<List<Discount>> getDiscounts(List<String> productIdList); //fetch discounts

    public Mono<List<UnitPrice>> getUnitPrices(List<String> productIdList); //fetch unit prices

}
