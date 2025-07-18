package ai.shreds.domain.exceptions;

import ai.shreds.domain.value_objects.DomainValueReservationId;
import ai.shreds.domain.value_objects.DomainValueReservationStatus;

/**
 * Domain exception thrown when an invalid reservation state transition is attempted.
 */
public class DomainInvalidReservationStateException extends RuntimeException {
    
    private final DomainValueReservationId reservationId;
    private final DomainValueReservationStatus currentStatus;
    private final String attemptedAction;
    
    public DomainInvalidReservationStateException(DomainValueReservationId reservationId,
                                                 DomainValueReservationStatus currentStatus,
                                                 String attemptedAction) {
        super(String.format("Cannot %s reservation %s with status %s", 
                attemptedAction, reservationId, currentStatus));
        this.reservationId = reservationId;
        this.currentStatus = currentStatus;
        this.attemptedAction = attemptedAction;
    }
    
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
    
    public DomainValueReservationStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public String getAttemptedAction() {
        return attemptedAction;
    }
}