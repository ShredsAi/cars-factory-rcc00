package ai.shreds.domain.ports;

import ai.shreds.domain.events.DomainReservationEvent;

public interface DomainOutputPortEventPublisher {
    void publish(DomainReservationEvent event);
}