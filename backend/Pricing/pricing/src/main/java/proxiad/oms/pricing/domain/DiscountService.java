package proxiad.oms.pricing.domain;

import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.TotalWithDiscount;
import reactor.core.publisher.Mono;

public interface DiscountService {

    public Mono<Void> calculateTotal(Cart cart);

}
