package proxiad.oms.pricing.domain.spi;

import proxiad.oms.pricing.domain.models.DomainEvent;

public interface EventPublisher {
    public void publish(DomainEvent event);
}
