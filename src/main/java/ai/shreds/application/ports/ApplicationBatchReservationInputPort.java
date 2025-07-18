package ai.shreds.application.ports;

import java.util.List;
import ai.shreds.application.dtos.ApplicationBatchReservationRequestDTO;
import ai.shreds.application.dtos.ApplicationBatchReservationResponseDTO;
import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationConflictException;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;

/**
 * Input port for batch reservation operations.
 * Provides methods to handle multiple reservations in batch operations.
 */
public interface ApplicationBatchReservationInputPort {

    /**
     * Creates multiple reservations atomically for a production run.
     *
     * @param request the batch reservation request details
     * @return the batch reservation response details
     * @throws ApplicationValidationException if any request in the batch is invalid
     * @throws ApplicationConflictException if there is insufficient stock for any item
     */
    ApplicationBatchReservationResponseDTO createBatchReservations(
        ApplicationBatchReservationRequestDTO request
    ) throws ApplicationValidationException, ApplicationConflictException;

    /**
     * Cancels multiple reservations in a batch operation.
     *
     * @param reservationIds the list of reservation identifiers to cancel
     * @return the list of updated reservation details
     * @throws ApplicationResourceNotFoundException if any reservation is not found
     * @throws ApplicationConflictException if any reservation cannot be cancelled
     */
    List<ApplicationReservationResponseDTO> cancelBatchReservations(
        List<ApplicationReservationIdDTO> reservationIds
    ) throws ApplicationResourceNotFoundException, ApplicationConflictException;

    /**
     * Consumes multiple reservations in a batch operation.
     *
     * @param reservationIds the list of reservation identifiers to consume
     * @return the list of consumed reservation details
     * @throws ApplicationResourceNotFoundException if any reservation is not found
     * @throws ApplicationConflictException if any reservation cannot be consumed
     */
    List<ApplicationReservationResponseDTO> consumeBatchReservations(
        List<ApplicationReservationIdDTO> reservationIds
    ) throws ApplicationResourceNotFoundException, ApplicationConflictException;
}