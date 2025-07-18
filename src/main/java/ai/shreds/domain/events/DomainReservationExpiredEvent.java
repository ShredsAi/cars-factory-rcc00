package ai.shreds.domain.events;

import ai.shreds.domain.entities.DomainComponentReservationEntity;

/**
 * Domain event representing a reservation expiration.
 */
public class DomainReservationExpiredEvent extends DomainReservationEvent {
    
    private final DomainComponentReservationEntity reservation;
    
    public DomainReservationExpiredEvent(DomainComponentReservationEntity reservation) {
        super("RESERVATION_EXPIRED", reservation);
        this.reservation = reservation;
    }
    
    public DomainComponentReservationEntity getReservation() {
        return reservation;
    }
    
    @Override
    public String toString() {
        return "DomainReservationExpiredEvent{" +
                "reservation=" + reservation +
                ", eventType='" + getEventType() + '\'' +
                ", reservationId=" + getReservationId() +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}