package proxiad.oms.pricing.domain.strategies;

import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.Discount;

public interface DiscountStrategy {
//    String getId();
    void apply(Cart cart);
    void update(Discount discount);
}