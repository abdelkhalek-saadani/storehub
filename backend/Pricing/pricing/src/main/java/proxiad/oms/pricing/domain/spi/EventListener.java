package proxiad.oms.pricing.domain.spi;

import com.proxiad.events.CartChangedEvent;

public interface EventListener {
    public void cartChangedListener(CartChangedEvent event);
}
