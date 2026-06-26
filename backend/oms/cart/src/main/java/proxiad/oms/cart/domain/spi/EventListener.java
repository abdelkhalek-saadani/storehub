package proxiad.oms.cart.domain.spi;

import proxiad.oms.cart.infrastructure.models.TotalCalculatedEvent;

public interface EventListener {
    public void totalListener(TotalCalculatedEvent event);
}
