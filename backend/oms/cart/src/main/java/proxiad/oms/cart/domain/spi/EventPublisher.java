package proxiad.oms.cart.domain.spi;

import org.springframework.stereotype.Component;
import proxiad.oms.cart.domain.models.DomainEvent;

@Component
public interface EventPublisher {
    public void publish(DomainEvent event);
}
