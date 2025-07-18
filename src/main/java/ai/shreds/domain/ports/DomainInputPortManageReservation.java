package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueReservationId;

/**
 * Domain input port for managing reservations.
 */
public interface DomainInputPortManageReservation {
    
    /**
     * Cancel a reservation.
     * 
     * @param reservationId the reservation ID to cancel
     * @throws ai.shreds.domain.exceptions.DomainExceptionReservationNotFound if reservation not found
     * @throws ai.shreds.domain.exceptions.DomainExceptionInvalidReservationState if reservation cannot be cancelled
     */
    void cancelReservation(DomainValueReservationId reservationId);
    
    /**
     * Consume a reservation.
     * 
     * @param reservationId the reservation ID to consume
     * @throws ai.shreds.domain.exceptions.DomainExceptionReservationNotFound if reservation not found
     * @throws ai.shreds.domain.exceptions.DomainExceptionInvalidReservationState if reservation cannot be consumed
     */
    void consumeReservation(DomainValueReservationId reservationId);
    
    /**
     * Process expired reservations.
     * This method finds all expired active reservations and expires them.
     */
    void processExpiredReservations();
    
    /**
     * Get reservation by ID.
     * 
     * @param reservationId the reservation ID
     * @return the reservation entity
     * @throws ai.shreds.domain.exceptions.DomainExceptionReservationNotFound if reservation not found
     */
    DomainComponentReservationEntity getReservation(DomainValueReservationId reservationId);
}