package proxiad.oms.cart.application.model;

import lombok.Data;

@Data
public class CartItemDTO {
    private String id;
    private String productId;
    private int quantity;
    private MoneyDTO originalUnitPrice;    // à verifier
    private MoneyDTO unitPrice;            // à verifier
}
