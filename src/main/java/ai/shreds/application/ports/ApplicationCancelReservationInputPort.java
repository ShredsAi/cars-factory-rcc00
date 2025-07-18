package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;
import ai.shreds.application.exceptions.ApplicationConflictException;

/**
 * Input port for cancelling a component reservation.
 */
public interface ApplicationCancelReservationInputPort {

    /**
     * Cancels an active reservation.
     *
     * @param reservationId the reservation identifier DTO
     * @return the updated reservation details with status CANCELLED
     * @throws ApplicationResourceNotFoundException if no reservation is found for the given ID
     * @throws ApplicationConflictException if the reservation cannot be cancelled (invalid state)
     */
    ApplicationReservationResponseDTO cancelReservation(ApplicationReservationIdDTO reservationId)
            throws ApplicationResourceNotFoundException, ApplicationConflictException;
}
