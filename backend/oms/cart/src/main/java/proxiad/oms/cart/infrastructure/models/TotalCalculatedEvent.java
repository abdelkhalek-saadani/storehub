package proxiad.oms.cart.infrastructure.models;

import lombok.Data;
import proxiad.oms.cart.domain.models.DomainEvent;

import java.math.BigDecimal;

@Data
public class TotalCalculatedEvent extends DomainEvent {

    private String cartId;
    private MoneyEvent total;


    public TotalCalculatedEvent() {}

    public TotalCalculatedEvent(String cartId, MoneyEvent total) {
        this.cartId = cartId;
        this.total = total;
    }

}
