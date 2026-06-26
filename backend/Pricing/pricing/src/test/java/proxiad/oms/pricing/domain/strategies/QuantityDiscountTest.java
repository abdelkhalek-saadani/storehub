package proxiad.oms.pricing.domain.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.models.Discount;
import proxiad.oms.pricing.domain.models.Item;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class QuantityDiscountTest {

    private static final String PRODUCT_ID = "product123";
    private static final BigDecimal UNIT_PRICE = BigDecimal.valueOf(100);
    private static final int MINIMUM_QUANTITY = 10;
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(10); // 10%

    private QuantityDiscount discountStrategy;
    private Cart cart;

    @BeforeEach
    public void setup() {
        Discount discount = new Discount(
                "QuantityDiscount",
                PRODUCT_ID,
                Map.of(
                        "minimumQuantity", String.valueOf(MINIMUM_QUANTITY),
                        "percentage", DISCOUNT_PERCENTAGE.toPlainString()
                )
        );

        discountStrategy = new QuantityDiscount(discount);
        cart = new Cart();
    }

    @Test
    public void testApplyDiscountWhenConditionsMet() {
        // Arrange
        cart.addItem(createItem(PRODUCT_ID, MINIMUM_QUANTITY, UNIT_PRICE));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item item = cart.getItems().getFirst();
        BigDecimal expectedDiscountPerUnit = UNIT_PRICE.multiply(DISCOUNT_PERCENTAGE).divide(BigDecimal.valueOf(100));
        BigDecimal expectedTotal = UNIT_PRICE.multiply(BigDecimal.valueOf(MINIMUM_QUANTITY)).subtract(expectedDiscountPerUnit.multiply(BigDecimal.valueOf(MINIMUM_QUANTITY)));

        assertAll(
                () -> assertEquals(1, item.getAppliedDiscounts().size(), "One discount should be applied"),
                () -> assertEquals(expectedDiscountPerUnit, item.getAppliedDiscounts().getFirst().getAmountPerUnit(), "Discount per unit should be correct"),
                () -> assertEquals(expectedTotal, cart.getFinalTotal(), "Cart total should reflect discount")
        );
    }

    @Test
    public void testApplyDiscountWhenCartHasTwoItems() {
        // Arrange
        cart.addItem(createItem(PRODUCT_ID, MINIMUM_QUANTITY, UNIT_PRICE));
        cart.addItem(createItem("product124", 20, BigDecimal.valueOf(10)));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item discountedItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(PRODUCT_ID))
                .findFirst()
                .orElseThrow();

        BigDecimal expectedDiscountPerUnit = UNIT_PRICE.multiply(DISCOUNT_PERCENTAGE).divide(BigDecimal.valueOf(100));
        BigDecimal expectedDiscountedItemTotal = UNIT_PRICE.multiply(BigDecimal.valueOf(MINIMUM_QUANTITY))
                .subtract(expectedDiscountPerUnit.multiply(BigDecimal.valueOf(MINIMUM_QUANTITY)));
        BigDecimal expectedOtherItemTotal = BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(20));
        BigDecimal expectedCartTotal = expectedDiscountedItemTotal.add(expectedOtherItemTotal);

        assertAll(
                () -> assertEquals(1, discountedItem.getAppliedDiscounts().size(), "One discount should be applied on the first item"),
                () -> assertEquals(expectedDiscountPerUnit, discountedItem.getAppliedDiscounts().getFirst().getAmountPerUnit(), "Discount per unit should be correct"),
                () -> assertEquals(expectedCartTotal, cart.getFinalTotal(), "Cart total should be correct including both items")
        );
    }

    @Test
    public void testApplyDiscountWhenConditionsNotMet() {
        // Arrange
        cart.addItem(createItem(PRODUCT_ID, 1, UNIT_PRICE));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item item = cart.getItems().getFirst();
        assertTrue(item.getAppliedDiscounts().isEmpty(), "No discount should be applied when quantity is too low");
    }

    @Test
    public void testApplyDiscountWrongProduct() {
        // Arrange
        cart.addItem(createItem("wrongProduct", 15, UNIT_PRICE));

        // Act
        discountStrategy.apply(cart);

        // Assert
        Item item = cart.getItems().getFirst();
        assertTrue(item.getAppliedDiscounts().isEmpty(), "No discount should be applied to wrong product");
    }

    @Test
    public void testApplyDiscountEmptyCart() {
        // Arrange: empty cart, no items

        // Act
        discountStrategy.apply(cart);

        // Assert
        assertTrue(cart.getItems().isEmpty(), "Cart should still be empty");
        assertEquals(BigDecimal.ZERO, cart.getFinalTotal(), "Total should be zero for empty cart");
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
