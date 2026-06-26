package proxiad.oms.pricing.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemEvent {
    private String productId;
    private int quantity;


    public CartItemEvent(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }


}
