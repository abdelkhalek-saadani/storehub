package proxiad.oms.pricing.domain.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.domain.models.Item;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PercentageDiscountTest {
    private static final String PRODUCT_ID = "product123";
    private static final BigDecimal UNIT_PRICE = BigDecimal.valueOf(100);
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(20); // 20%
    private static final int QUANTITY = 5;

    private PercentageDiscount discountStrategy;
    private Cart cart;

    @BeforeEach
    public void setup() {
        Discount discount = new Discount(
                "PercentageDiscount",
                PRODUCT_ID,
                Map.of(
                        "percentage", DISCOUNT_PERCENTAGE.toPlainString()
                )
        );

        discountStrategy = new PercentageDiscount(discount);
        cart = new Cart();
    }

    @Test
    public void testApplyDiscountWhenConditionsMet() {
        // Arrange
        cart.addItem(createItem(PRODUCT_ID,QUANTITY, UNIT_PRICE));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item item = cart.getItems().getFirst();
        BigDecimal expectedDiscountPerUnit = UNIT_PRICE.multiply(DISCOUNT_PERCENTAGE).divide(BigDecimal.valueOf(100), new MathContext(10, RoundingMode.HALF_UP));
        BigDecimal expectedTotal = (UNIT_PRICE.subtract(expectedDiscountPerUnit)).multiply(BigDecimal.valueOf(QUANTITY));

        assertAll(
                () -> assertEquals(1, item.getAppliedDiscounts().size(), "One discount should be applied"),
                () -> assertEquals(expectedDiscountPerUnit, item.getAppliedDiscounts().getFirst().getAmountPerUnit(), "Discount per unit should be correct"),
                () -> assertEquals(expectedTotal, cart.getFinalTotal(), "Cart total should reflect discount")
        );
    }

    @Test
    public void testApplyDiscountWhenCartHasTwoItems() {
        // Arrange
        cart.addItem(createItem(PRODUCT_ID, QUANTITY, UNIT_PRICE));
        int otherItemQuantity = 20;
        BigDecimal otherItemUnitPrice = BigDecimal.valueOf(10);
        cart.addItem(createItem("product124", otherItemQuantity, otherItemUnitPrice));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item discountedItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(PRODUCT_ID))
                .findFirst()
                .orElseThrow();

        BigDecimal expectedDiscountPerUnit = UNIT_PRICE.multiply(DISCOUNT_PERCENTAGE).divide(BigDecimal.valueOf(100),new MathContext(10, RoundingMode.HALF_UP));
        BigDecimal expectedDiscountedItemTotal = (UNIT_PRICE.subtract(expectedDiscountPerUnit)).multiply(BigDecimal.valueOf(QUANTITY));
        BigDecimal expectedOtherItemTotal = otherItemUnitPrice.multiply(BigDecimal.valueOf(otherItemQuantity));
        BigDecimal expectedCartTotal = expectedDiscountedItemTotal.add(expectedOtherItemTotal);

        assertAll(
                () -> assertEquals(1, discountedItem.getAppliedDiscounts().size(), "One discount should be applied on the first item"),
                () -> assertEquals(expectedDiscountPerUnit, discountedItem.getAppliedDiscounts().getFirst().getAmountPerUnit(), "Discount per unit should be correct"),
                () -> assertEquals(expectedCartTotal, cart.getFinalTotal(), "Cart total should be correct including both items")
        );
    }
    // Helper method to create an item easily
    private Item createItem(String productId, int quantity, BigDecimal unitPrice) {
        Item item = new Item();
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.initializePrices(unitPrice);
        return item;
    }
}