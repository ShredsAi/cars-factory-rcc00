package ai.shreds.application.ports;

import java.util.List;
import ai.shreds.application.dtos.ApplicationProductionRunIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;

/**
 * Input port for retrieving reservations by production run ID.
 */
public interface ApplicationGetReservationsByProductionRunInputPort {

    /**
     * Retrieves all reservations for the given production run.
     *
     * @param productionRunId the production run identifier DTO
     * @return list of reservation response DTOs
     * @throws ApplicationResourceNotFoundException if no reservations are found
     */
    List<ApplicationReservationResponseDTO> getReservationsByProductionRun(
            ApplicationProductionRunIdDTO productionRunId)
            throws ApplicationResourceNotFoundException;
}
