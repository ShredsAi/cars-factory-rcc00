package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.events.DomainReservationEvent;
import ai.shreds.domain.ports.DomainOutputPortEventPublisher;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfrastructureJmsEventPublisherImpl implements DomainOutputPortEventPublisher {

    private final JmsTemplate jmsTemplate;
    private final InfrastructureReservationEventMessageConverter messageConverter;

    @Value("${reservation.topics.reservation-created:inventory.reservations.created}")
    private String reservationCreatedTopic;

    @Value("${reservation.topics.reservation-expired:inventory.reservations.expired}")
    private String reservationExpiredTopic;

    @Value("${reservation.topics.reservation-consumed:inventory.reservations.consumed}")
    private String reservationConsumedTopic;

    @Value("${reservation.topics.reservation-cancelled:inventory.reservations.cancelled}")
    private String reservationCancelledTopic;

    @Override
    public void publish(DomainReservationEvent event) {
        try {
            SharedReservationEventMessageDTO message = messageConverter.convertToMessage(event);
            String topicName = getTopicName(message.getEventType());
            log.info("Publishing event type '{}' to topic '{}' for reservationId: {}", 
                     message.getEventType(), topicName, message.getReservationId());
            jmsTemplate.convertAndSend(topicName, message);
            log.debug("Successfully published message: {}", message);
        } catch (Exception e) {
            log.error("Failed to publish reservation event for reservationId: {}", event.getEventId(), e);
            // Depending on the requirements, a custom exception could be thrown here.
        }
    }

    private String getTopicName(String eventType) {
        switch (eventType) {
            case "CREATED":
                return reservationCreatedTopic;
            case "EXPIRED":
                return reservationExpiredTopic;
            case "CONSUMED":
                return reservationConsumedTopic;
            case "CANCELLED":
                return reservationCancelledTopic;
            default:
                log.warn("Unknown event type '{}', using default topic.", eventType);
                return jmsTemplate.getDefaultDestinationName();
        }
    }
}