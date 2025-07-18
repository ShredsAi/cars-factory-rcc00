package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;
import ai.shreds.application.exceptions.ApplicationConflictException;

/**
 * Input port for consuming a component reservation.
 */
public interface ApplicationConsumeReservationInputPort {

    /**
     * Consumes an active reservation.
     *
     * @param reservationId the reservation identifier DTO
     * @return the updated reservation details
     * @throws ApplicationResourceNotFoundException if the reservation is not found
     * @throws ApplicationConflictException if the reservation cannot be consumed (invalid state)
     */
    ApplicationReservationResponseDTO consumeReservation(ApplicationReservationIdDTO reservationId);
}
