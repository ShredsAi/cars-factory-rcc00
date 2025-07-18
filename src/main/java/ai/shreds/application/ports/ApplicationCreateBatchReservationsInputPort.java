package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationBatchReservationRequestDTO;
import ai.shreds.application.dtos.ApplicationBatchReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationConflictException;

/**
 * Input port for creating multiple component reservations in a single batch.
 */
public interface ApplicationCreateBatchReservationsInputPort {

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
}
