package ai.shreds.domain.exceptions;

import ai.shreds.domain.value_objects.DomainValueReservationId;

/**
 * Exception thrown when a reservation is not found.
 */
public class DomainExceptionReservationNotFound extends RuntimeException {
    
    private final DomainValueReservationId reservationId;
    
    public DomainExceptionReservationNotFound(DomainValueReservationId reservationId) {
        super(String.format("Reservation not found: %s", reservationId));
        this.reservationId = reservationId;
    }
    
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
}