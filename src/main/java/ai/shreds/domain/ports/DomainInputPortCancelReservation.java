package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueReservationId;

/**
 * Input port for cancelling a reservation.
 * This port defines the contract for reservation cancellation use cases.
 */
public interface DomainInputPortCancelReservation {
    
    /**
     * Cancel an existing reservation
     * @param reservationId the reservation to cancel
     * @return the cancelled reservation entity
     */
    DomainComponentReservationEntity cancelReservation(DomainValueReservationId reservationId);
}