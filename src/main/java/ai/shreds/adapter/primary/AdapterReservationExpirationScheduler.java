package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationProcessExpiredReservationsInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Adapter for scheduling reservation expiration processing.
 * This component automatically triggers expiration processing based on the configured cron schedule.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdapterReservationExpirationScheduler {

    private final ApplicationProcessExpiredReservationsInputPort processExpiredReservationsInputPort;

    /**
     * Scheduled method to process expired reservations.
     * The cron expression is configured in application.yml under reservation.expiration.cron
     */
    @Scheduled(cron = "${reservation.expiration.cron:0 */5 * * * *}")
    public void processExpiredReservations() {
        log.info("Starting scheduled expiration processing");
        
        try {
            processExpiredReservationsInputPort.processExpiredReservations();
            log.info("Scheduled expiration processing completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled expiration processing", e);
            // Don't rethrow to avoid breaking the scheduler
        }
    }
}