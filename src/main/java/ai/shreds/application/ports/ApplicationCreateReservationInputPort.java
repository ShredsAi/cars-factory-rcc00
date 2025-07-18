package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationReservationRequestDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationConflictException;

public interface ApplicationCreateReservationInputPort {
    /**
     * Creates a new component reservation
     * @param request The reservation request details
     * @return The created reservation details
     * @throws ApplicationValidationException if the request is invalid
     * @throws ApplicationConflictException if there is insufficient stock
     */
    ApplicationReservationResponseDTO createReservation(ApplicationReservationRequestDTO request);
}
