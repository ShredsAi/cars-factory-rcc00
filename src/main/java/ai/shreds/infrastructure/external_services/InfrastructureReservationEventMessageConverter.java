package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.events.DomainReservationCancelledEvent;
import ai.shreds.domain.events.DomainReservationConsumedEvent;
import ai.shreds.domain.events.DomainReservationCreatedEvent;
import ai.shreds.domain.events.DomainReservationEvent;
import ai.shreds.domain.events.DomainReservationExpiredEvent;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class InfrastructureReservationEventMessageConverter {

    public SharedReservationEventMessageDTO convertToMessage(DomainReservationEvent event) {
        if (event instanceof DomainReservationCreatedEvent) {
            return convertCreatedEvent((DomainReservationCreatedEvent) event);
        } else if (event instanceof DomainReservationExpiredEvent) {
            return convertExpiredEvent((DomainReservationExpiredEvent) event);
        } else if (event instanceof DomainReservationConsumedEvent) {
            return convertConsumedEvent((DomainReservationConsumedEvent) event);
        } else if (event instanceof DomainReservationCancelledEvent) {
            return convertCancelledEvent((DomainReservationCancelledEvent) event);
        }
        throw new IllegalArgumentException("Unknown event type: " + event.getClass().getName());
    }

    public SharedReservationEventMessageDTO convertCreatedEvent(DomainReservationCreatedEvent event) {
        return fromReservationEntity(event.getReservation(), "CREATED");
    }

    public SharedReservationEventMessageDTO convertExpiredEvent(DomainReservationExpiredEvent event) {
        return fromReservationEntity(event.getReservation(), "EXPIRED");
    }

    public SharedReservationEventMessageDTO convertConsumedEvent(DomainReservationConsumedEvent event) {
        return fromReservationEntity(event.getReservation(), "CONSUMED");
    }

    public SharedReservationEventMessageDTO convertCancelledEvent(DomainReservationCancelledEvent event) {
        return fromReservationEntity(event.getReservation(), "CANCELLED");
    }

    private SharedReservationEventMessageDTO fromReservationEntity(DomainComponentReservationEntity entity, String eventType) {
        SharedReservationEventMessageDTO dto = new SharedReservationEventMessageDTO();
        dto.setReservationId(entity.getReservationId().getValue());
        dto.setItemId(entity.getItemId().getValue());
        dto.setLocationId(entity.getLocationId().getValue());
        dto.setProductionRunId(entity.getProductionRunId().getValue());
        dto.setQuantityReserved(entity.getQuantityReserved().getValue());
        dto.setQuantityUnit(entity.getQuantityReserved().getUnit());
        dto.setStatus(entity.getStatus().getValue());
        dto.setEventType(eventType);
        dto.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));
        return dto;
    }
}