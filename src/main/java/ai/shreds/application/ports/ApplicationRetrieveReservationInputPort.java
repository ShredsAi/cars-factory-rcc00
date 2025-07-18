package ai.shreds.application.ports;

import java.util.List;
import java.util.Optional;
import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.dtos.ApplicationProductionRunIdDTO;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;
import ai.shreds.application.exceptions.ApplicationValidationException;

/**
 * Input port for retrieving reservation information.
 * Provides methods to retrieve reservations by various criteria.
 */
public interface ApplicationRetrieveReservationInputPort {

    /**
     * Retrieves a reservation by its unique identifier.
     *
     * @param reservationId the reservation identifier DTO
     * @return Optional containing the reservation details if found
     * @throws ApplicationValidationException if the reservation ID is invalid
     */
    Optional<ApplicationReservationResponseDTO> getReservationById(ApplicationReservationIdDTO reservationId)
            throws ApplicationValidationException;

    /**
     * Retrieves all reservations for a given production run.
     *
     * @param productionRunId the production run identifier DTO
     * @return List of reservation response DTOs for the production run
     * @throws ApplicationValidationException if the production run ID is invalid
     */
    List<ApplicationReservationResponseDTO> getReservationsByProductionRun(ApplicationProductionRunIdDTO productionRunId)
            throws ApplicationValidationException;

    /**
     * Retrieves all active reservations (not cancelled or consumed).
     *
     * @return List of active reservation response DTOs
     */
    List<ApplicationReservationResponseDTO> getActiveReservations();

    /**
     * Retrieves all expired reservations that need processing.
     *
     * @return List of expired reservation response DTOs
     */
    List<ApplicationReservationResponseDTO> getExpiredReservations();
}