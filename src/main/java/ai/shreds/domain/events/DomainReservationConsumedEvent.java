package ai.shreds.domain.events;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import java.time.Instant;

/**
 * Domain event representing a reservation consumption.
 */
public class DomainReservationConsumedEvent extends DomainReservationEvent {
    
    public DomainReservationConsumedEvent(DomainComponentReservationEntity reservation) {
        super("RESERVATION_CONSUMED", reservation);
    }
    
    @Override
    public String toString() {
        return "DomainReservationConsumedEvent{" +
                "reservation=" + reservation +
                ", eventType='" + getEventType() + '\'' +
                ", reservationId=" + getReservationId() +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}