package ai.shreds.application.services;

import ai.shreds.application.dtos.ApplicationReservationEventDTO;
import ai.shreds.application.ports.ApplicationReservationEventOutputPort;
import ai.shreds.application.exceptions.ApplicationValidationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for publishing application-level events.
 * This service implements the ApplicationReservationEventOutputPort to publish reservation events.
 */
@Service
public class ApplicationEventPublisherService implements ApplicationReservationEventOutputPort {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventPublisherService.class);
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Autowired
    public ApplicationEventPublisherService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publishEvent(ApplicationReservationEventDTO event) {
        if (event == null) {
            throw new ApplicationValidationException("Event cannot be null");
        }
        
        try {
            logger.debug("Publishing reservation event: reservationId={}, eventType={}", 
                event.getReservationId(), event.getEventType());
            
            // Validate event before publishing
            validateEvent(event);
            
            // Publish the event through Spring's ApplicationEventPublisher
            applicationEventPublisher.publishEvent(event);
            
            logger.info("Successfully published reservation event: reservationId={}, eventType={}", 
                event.getReservationId(), event.getEventType());
                
        } catch (Exception e) {
            logger.error("Failed to publish reservation event: reservationId={}, eventType={}", 
                event.getReservationId(), event.getEventType(), e);
            throw new ApplicationValidationException("Failed to publish event: " + e.getMessage());
        }
    }
    
    /**
     * Validates the event before publishing.
     * 
     * @param event the event to validate
     * @throws ApplicationValidationException if validation fails
     */
    private void validateEvent(ApplicationReservationEventDTO event) {
        if (event.getReservationId() == null) {
            throw new ApplicationValidationException("Reservation ID cannot be null in event");
        }
        
        if (event.getEventType() == null || event.getEventType().trim().isEmpty()) {
            throw new ApplicationValidationException("Event type cannot be null or empty");
        }
        
        if (event.getTimestamp() == null) {
            throw new ApplicationValidationException("Event timestamp cannot be null");
        }
        
        if (event.getItemId() == null) {
            throw new ApplicationValidationException("Item ID cannot be null in event");
        }
        
        if (event.getLocationId() == null) {
            throw new ApplicationValidationException("Location ID cannot be null in event");
        }
        
        if (event.getProductionRunId() == null) {
            throw new ApplicationValidationException("Production run ID cannot be null in event");
        }
        
        logger.debug("Event validation passed for reservation: {}", event.getReservationId());
    }
}