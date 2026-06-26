package proxiad.oms.pricing.domain.strategies;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.AppliedDiscount;
import proxiad.oms.pricing.domain.models.Discount;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

@Slf4j
@Data
public class QuantityDiscount implements DiscountStrategy {
    private int minimumQuantity;
    private BigDecimal percentage;
    private String productId;
    private String id;

    public QuantityDiscount( String id,int minimumQuantity,
                             BigDecimal percentage, String productId) {

        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
        this.productId = productId;
        this.id = id;
    }

    public QuantityDiscount(Discount discount) {
        this(
                discount.getId(),
                Integer.parseInt(discount.getAttributes().get("minimumQuantity")),
                BigDecimal.valueOf(Integer.parseInt(discount.getAttributes().get("percentage"))),
                discount.getProductId()
                );
    }

    public void update(Discount discount) {
        id = discount.getId();
        percentage = BigDecimal.valueOf(Integer.parseInt(discount.getAttributes().get("percentage")));
        minimumQuantity = Integer.parseInt(discount.getAttributes().get("minimumQuantity"));
        productId = discount.getProductId();
    }

    @Override
    public void apply(Cart cart) {

        log.info("Now we will apply quantity discount for {} ", productId);
        cart.getItems().forEach(item -> {
            boolean applies = (productId == null || productId.equals(item.getProductId())) &&
                    item.getQuantity() >= minimumQuantity;

            if (applies) {
                BigDecimal discountAmount =
                        (item.getOriginalUnitPrice().multiply(percentage))
                                .divide(
                                        BigDecimal.valueOf(100),new MathContext(10, RoundingMode.HALF_UP)
                                );

                String description = percentage + "% discount for buying " +
                        minimumQuantity + " or more";

                AppliedDiscount discount = new AppliedDiscount(
                        description, discountAmount
                );

                item.applyDiscount(discount);
            }
        });

        cart.calculateTotalDiscount();
    }
}
