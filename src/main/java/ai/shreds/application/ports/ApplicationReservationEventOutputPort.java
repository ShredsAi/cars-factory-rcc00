package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationReservationEventDTO;

/**
 * Output port for publishing reservation lifecycle events to external systems.
 */
public interface ApplicationReservationEventOutputPort {

    /**
     * Publishes a reservation event for inter-shred communication.
     *
     * @param event the reservation event DTO to publish
     */
    void publishEvent(ApplicationReservationEventDTO event);
}
