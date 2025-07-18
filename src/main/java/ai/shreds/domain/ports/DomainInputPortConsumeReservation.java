package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueReservationId;

/**
 * Input port for consuming a reservation.
 * This port defines the contract for reservation consumption use cases.
 */
public interface DomainInputPortConsumeReservation {
    
    /**
     * Consume an existing reservation
     * @param reservationId the reservation to consume
     * @return the consumed reservation entity
     */
    DomainComponentReservationEntity consumeReservation(DomainValueReservationId reservationId);
}