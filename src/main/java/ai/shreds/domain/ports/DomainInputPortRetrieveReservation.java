package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainReservationResponseDTO;
import ai.shreds.domain.value_objects.DomainValueReservationId;
import ai.shreds.domain.value_objects.DomainProductionRunIdValue;
import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving reservations.
 * This port defines the contract for reservation retrieval use cases.
 */
public interface DomainInputPortRetrieveReservation {
    
    /**
     * Retrieve a reservation by its ID
     * @param reservationId the reservation ID to find
     * @return the reservation response if found
     */
    Optional<DomainReservationResponseDTO> getReservationById(DomainValueReservationId reservationId);
    
    /**
     * Retrieve all reservations for a production run
     * @param productionRunId the production run ID to find reservations for
     * @return list of reservations for the production run
     */
    List<DomainReservationResponseDTO> getReservationsByProductionRun(DomainProductionRunIdValue productionRunId);
}