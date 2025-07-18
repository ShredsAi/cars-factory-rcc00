package ai.shreds.application.services;

import ai.shreds.application.ports.ApplicationProcessExpiredReservationsInputPort;
import ai.shreds.application.ports.ApplicationReservationEventOutputPort;
import ai.shreds.application.dtos.ApplicationReservationEventDTO;
import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.events.DomainReservationExpiredEvent;
import ai.shreds.domain.ports.DomainInputPortProcessExpiredReservations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for processing expired reservations.
 * This service orchestrates the expiration processing and event publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationExpiredReservationProcessorService implements ApplicationProcessExpiredReservationsInputPort {

    private final DomainInputPortProcessExpiredReservations domainProcessExpiredReservationsPort;
    private final ApplicationReservationEventOutputPort eventOutputPort;

    @Override
    @Transactional
    public void processExpiredReservations() {
        log.info("Starting processing of expired reservations");
        
        try {
            List<DomainComponentReservationEntity> expiredReservations = domainProcessExpiredReservationsPort.processExpiredReservations();
            
            log.info("Found {} expired reservations to process", expiredReservations.size());
            
            // Publish events for each expired reservation
            for (DomainComponentReservationEntity reservation : expiredReservations) {
                try {
                    ApplicationReservationEventDTO eventDTO = ApplicationReservationEventDTO.fromDomainEvent(
                        new DomainReservationExpiredEvent(reservation)
                    );
                    eventOutputPort.publishEvent(eventDTO);
                    log.debug("Published expired event for reservation: {}", reservation.getReservationId().getValue());
                } catch (Exception e) {
                    log.error("Failed to publish expired event for reservation: {}", reservation.getReservationId().getValue(), e);
                    // Don't fail the entire process for a single event publishing failure
                }
            }
            
            log.info("Successfully processed {} expired reservations", expiredReservations.size());
        } catch (Exception e) {
            log.error("Error processing expired reservations", e);
            throw e;
        }
    }
}