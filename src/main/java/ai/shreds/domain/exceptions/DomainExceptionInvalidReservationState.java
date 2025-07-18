package ai.shreds.domain.exceptions;

import ai.shreds.domain.value_objects.DomainValueReservationId;
import ai.shreds.domain.value_objects.DomainValueReservationStatus;

/**
 * Exception thrown when a reservation is in an invalid state for the requested operation.
 */
public class DomainExceptionInvalidReservationState extends RuntimeException {
    
    private final DomainValueReservationId reservationId;
    private final DomainValueReservationStatus currentStatus;
    private final String attemptedOperation;
    
    public DomainExceptionInvalidReservationState(DomainValueReservationId reservationId, 
                                                 DomainValueReservationStatus currentStatus,
                                                 String attemptedOperation) {
        super(String.format("Cannot %s reservation %s in status %s",
                attemptedOperation, reservationId, currentStatus));
        this.reservationId = reservationId;
        this.currentStatus = currentStatus;
        this.attemptedOperation = attemptedOperation;
    }
    
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
    
    public DomainValueReservationStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public String getAttemptedOperation() {
        return attemptedOperation;
    }
}