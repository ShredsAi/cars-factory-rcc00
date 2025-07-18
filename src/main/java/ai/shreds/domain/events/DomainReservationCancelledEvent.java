package ai.shreds.domain.events;

import ai.shreds.domain.entities.DomainComponentReservationEntity;

/**
 * Domain event representing a reservation cancellation.
 */
public class DomainReservationCancelledEvent extends DomainReservationEvent {
    
    private final DomainComponentReservationEntity reservation;
    
    public DomainReservationCancelledEvent(DomainComponentReservationEntity reservation) {
        super("RESERVATION_CANCELLED", reservation);
        this.reservation = reservation;
    }
    
    public DomainComponentReservationEntity getReservation() {
        return reservation;
    }
    
    @Override
    public String toString() {
        return "DomainReservationCancelledEvent{" +
                "reservation=" + reservation +
                ", eventType='" + getEventType() + '\'' +
                ", reservationId=" + getReservationId() +
                ", occurredAt=" + getOccurredAt() +
                '}';
    }
}