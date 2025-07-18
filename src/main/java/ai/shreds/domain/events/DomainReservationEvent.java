package ai.shreds.domain.events;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.*;
import java.time.Instant;

/**
 * Base class for all reservation domain events.
 * This provides common structure for reservation-related events.
 */
public abstract class DomainReservationEvent {
    
    private final String eventType;
    private final DomainValueReservationId reservationId;
    private final Instant occurredAt;
    private final String eventId;
    protected final DomainComponentReservationEntity reservation;
    
    protected DomainReservationEvent(String eventType, DomainComponentReservationEntity reservation) {
        this.eventType = eventType;
        this.reservation = reservation;
        this.reservationId = reservation.getReservationId();
        this.occurredAt = Instant.now();
        this.eventId = java.util.UUID.randomUUID().toString();
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
    
    public DomainValueItemId getItemId() {
        return reservation.getItemId();
    }
    
    public DomainValueLocationId getLocationId() {
        return reservation.getLocationId();
    }
    
    public DomainValueProductionRunId getProductionRunId() {
        return reservation.getProductionRunId();
    }
    
    public DomainValueQuantity getQuantityReserved() {
        return reservation.getQuantityReserved();
    }
    
    public DomainValueReservationStatus getStatus() {
        return reservation.getStatus();
    }
    
    public Instant getTimestamp() {
        return occurredAt;
    }
    
    public Instant getOccurredAt() {
        return occurredAt;
    }
    
    public String getEventId() {
        return eventId;
    }

    public DomainComponentReservationEntity getReservation() {
        return reservation;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainReservationEvent that = (DomainReservationEvent) o;
        return eventId.equals(that.eventId);
    }
    
    @Override
    public int hashCode() {
        return eventId.hashCode();
    }
    
    @Override
    public String toString() {
        return "DomainReservationEvent{" +
                "eventType='" + eventType + "'" +
                ", reservationId=" + reservationId +
                ", occurredAt=" + occurredAt +
                ", eventId='" + eventId + "'" +
                "}";
    }
}