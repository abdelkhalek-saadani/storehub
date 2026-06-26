package proxiad.oms.cart.infrastructure.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxiad.oms.cart.domain.models.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
// TODO: Delete this class because I moved ot to common project
public class CartChangedEvent{
    private UUID cartId;
    private List<CartItem> cartItems;

}


