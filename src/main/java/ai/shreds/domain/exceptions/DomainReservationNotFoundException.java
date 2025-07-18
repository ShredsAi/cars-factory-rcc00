package ai.shreds.domain.exceptions;

import ai.shreds.domain.value_objects.DomainValueReservationId;

/**
 * Domain exception thrown when a reservation is not found.
 */
public class DomainReservationNotFoundException extends RuntimeException {
    
    private final DomainValueReservationId reservationId;
    
    public DomainReservationNotFoundException(DomainValueReservationId reservationId) {
        super("Reservation not found: " + reservationId);
        this.reservationId = reservationId;
    }
    
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
}