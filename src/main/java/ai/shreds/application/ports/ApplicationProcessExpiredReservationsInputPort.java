package ai.shreds.application.ports;

/**
 * Input port for processing expired reservations.
 */
public interface ApplicationProcessExpiredReservationsInputPort {

    /**
     * Processes reservations that have expired and releases stock.
     */
    void processExpiredReservations();
}
