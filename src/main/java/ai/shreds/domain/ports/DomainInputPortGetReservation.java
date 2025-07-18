package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueReservationId;
import java.util.Optional;

/**
 * Input port for retrieving a single reservation.
 * This port defines the contract for reservation retrieval use cases.
 */
public interface DomainInputPortGetReservation {
    
    /**
     * Retrieve a reservation by its ID
     * @param reservationId the reservation ID to find
     * @return the reservation entity if found
     */
    Optional<DomainComponentReservationEntity> getReservationById(DomainValueReservationId reservationId);
}