package proxiad.oms.pricing.application.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// TODO: Delete this class because I moved ot to common project
public class CartChangedEvent {
    private String cartId;
    private List<CartItemEvent> cartItems;



}


