package ai.shreds.domain.events;

import ai.shreds.domain.entities.DomainComponentReservationEntity;

/**
 * Domain event representing a reservation creation.
 */
public class DomainReservationCreatedEvent extends DomainReservationEvent {

    private final DomainComponentReservationEntity reservation;

    public DomainReservationCreatedEvent(DomainComponentReservationEntity reservation) {
        super("RESERVATION_CREATED", reservation);
        this.reservation = reservation;
    }

    public DomainComponentReservationEntity getReservation() {
        return reservation;
    }

    @Override
    public String toString() {
        return "DomainReservationCreatedEvent{" +
                "reservation=" + reservation +
                ", eventType=" + getEventType() +
                ", reservationId=" + getReservationId() +
                ", occurredAt=" + getOccurredAt() +
                "}";
    }
}