package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;

/**
 * Input port for retrieving a component reservation by its ID.
 */
public interface ApplicationGetReservationInputPort {

    /**
     * Retrieves a reservation by its unique identifier.
     *
     * @param reservationId the reservation identifier DTO
     * @return the reservation details
     * @throws ApplicationResourceNotFoundException if no reservation is found for the given ID
     */
    ApplicationReservationResponseDTO getReservation(ApplicationReservationIdDTO reservationId)
            throws ApplicationResourceNotFoundException;
}
