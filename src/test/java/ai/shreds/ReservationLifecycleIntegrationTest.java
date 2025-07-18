package ai.shreds;

import ai.shreds.domain.ports.DomainOutputPortDeviceProtocolRepository;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;
import ai.shreds.shared.dtos.SharedReservationRequestDTO;
import ai.shreds.shared.dtos.SharedReservationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the complete reservation lifecycle.
 * Tests the core functionality of creating, consuming, and cancelling reservations.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test-schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationLifecycleIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @MockBean
    private DomainOutputPortDeviceProtocolRepository deviceProtocolRepository;

    @MockBean
    private DomainOutputPortGPSDeviceRepository gpsDeviceRepository;

    private final String CREATED_TOPIC = "test.inventory.reservations.created";
    private final String CONSUMED_TOPIC = "test.inventory.reservations.consumed";
    private final String CANCELLED_TOPIC = "test.inventory.reservations.cancelled";

    @BeforeEach
    void setUp() {
        // Drain all queues to ensure no messages from previous tests interfere
        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(CREATED_TOPIC) != null) {}
        while (jmsTemplate.receive(CONSUMED_TOPIC) != null) {}
        while (jmsTemplate.receive(CANCELLED_TOPIC) != null) {}
        jmsTemplate.setReceiveTimeout(5000);
    }

    @Test
    void whenReservationCreated_thenCanBeConsumed() throws Exception {
        // Arrange - Create a reservation first
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
        BigDecimal quantityToReserve = new BigDecimal("15.0000");
        UUID reservedBy = UUID.randomUUID();
        UUID productionRunId = UUID.randomUUID();

        SharedReservationRequestDTO createRequest = new SharedReservationRequestDTO();
        createRequest.setItemId(itemId);
        createRequest.setLocationId(locationId);
        createRequest.setProductionRunId(productionRunId);
        createRequest.setQuantityReserved(quantityToReserve);
        createRequest.setQuantityUnit("PIECES");
        createRequest.setReservedBy(reservedBy);
        createRequest.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Act 1 - Create the reservation
        ResponseEntity<SharedReservationResponseDTO> createResponse = restTemplate.postForEntity(
                "/v1/reservations",
                createRequest,
                SharedReservationResponseDTO.class
        );

        // Assert creation was successful
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SharedReservationResponseDTO createdReservation = createResponse.getBody();
        assertThat(createdReservation).isNotNull();
        UUID reservationId = createdReservation.getReservationId();
        assertThat(reservationId).isNotNull();
        assertThat(createdReservation.getStatus()).isEqualTo("ACTIVE");

        // Verify database state after creation
        Map<String, Object> reservationInDb = jdbcTemplate.queryForMap(
                "SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(reservationInDb.get("status")).isEqualTo("ACTIVE");

        // Verify stock level after creation
        Map<String, Object> stockLevel = jdbcTemplate.queryForMap(
                "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        assertThat(new BigDecimal(stockLevel.get("reserved_qty").toString()))
                .isEqualByComparingTo(quantityToReserve);

        // Verify creation event was published
        Object createdMessage = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
        assertThat(createdMessage).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO createdEvent = (SharedReservationEventMessageDTO) createdMessage;
        assertThat(createdEvent.getEventType()).isEqualTo("CREATED");
        assertThat(createdEvent.getReservationId()).isEqualTo(reservationId);
        assertThat(createdEvent.getStatus()).isEqualTo("ACTIVE");

        // Act 2 - Consume the reservation
        ResponseEntity<SharedReservationResponseDTO> consumeResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/consume",
                org.springframework.http.HttpMethod.PUT,
                null,
                SharedReservationResponseDTO.class
        );

        // Assert consumption was successful
        assertThat(consumeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SharedReservationResponseDTO consumedReservation = consumeResponse.getBody();
        assertThat(consumedReservation).isNotNull();
        assertThat(consumedReservation.getReservationId()).isEqualTo(reservationId);
        assertThat(consumedReservation.getStatus()).isEqualTo("CONSUMED");
        assertThat(consumedReservation.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);

        // Verify database state after consumption
        Map<String, Object> consumedReservationInDb = jdbcTemplate.queryForMap(
                "SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(consumedReservationInDb.get("status")).isEqualTo("CONSUMED");

        // Verify stock level after consumption - both quantityOnHand and reservedQty should decrease
        Map<String, Object> stockLevelAfterConsume = jdbcTemplate.queryForMap(
                "SELECT quantity_on_hand, reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        
        // Initial stock was 50, we consumed 15, so remaining should be 35
        assertThat(new BigDecimal(stockLevelAfterConsume.get("quantity_on_hand").toString()))
                .isEqualByComparingTo(new BigDecimal("35.0000"));
        
        // Reserved quantity should decrease from 15 to 0 after consumption
        assertThat(new BigDecimal(stockLevelAfterConsume.get("reserved_qty").toString()))
                .isEqualByComparingTo(BigDecimal.ZERO);

        // Verify consumption event was published
        Object consumedMessage = jmsTemplate.receiveAndConvert(CONSUMED_TOPIC);
        assertThat(consumedMessage).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO consumedEvent = (SharedReservationEventMessageDTO) consumedMessage;
        assertThat(consumedEvent.getEventType()).isEqualTo("CONSUMED");
        assertThat(consumedEvent.getReservationId()).isEqualTo(reservationId);
        assertThat(consumedEvent.getStatus()).isEqualTo("CONSUMED");
        assertThat(consumedEvent.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);
        assertThat(consumedEvent.getItemId()).isEqualTo(itemId);
        assertThat(consumedEvent.getLocationId()).isEqualTo(locationId);
    }

    @Test
    void whenReservationCreated_thenCanBeCancelled() throws Exception {
        // Arrange - Create a reservation first
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174004");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174005");
        BigDecimal quantityToReserve = new BigDecimal("25.0000");
        UUID reservedBy = UUID.randomUUID();
        UUID productionRunId = UUID.randomUUID();

        SharedReservationRequestDTO createRequest = new SharedReservationRequestDTO();
        createRequest.setItemId(itemId);
        createRequest.setLocationId(locationId);
        createRequest.setProductionRunId(productionRunId);
        createRequest.setQuantityReserved(quantityToReserve);
        createRequest.setQuantityUnit("PIECES");
        createRequest.setReservedBy(reservedBy);
        createRequest.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Act 1 - Create the reservation
        ResponseEntity<SharedReservationResponseDTO> createResponse = restTemplate.postForEntity(
                "/v1/reservations",
                createRequest,
                SharedReservationResponseDTO.class
        );

        // Assert creation was successful
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SharedReservationResponseDTO createdReservation = createResponse.getBody();
        assertThat(createdReservation).isNotNull();
        UUID reservationId = createdReservation.getReservationId();
        assertThat(reservationId).isNotNull();
        assertThat(createdReservation.getStatus()).isEqualTo("ACTIVE");

        // Verify initial stock level
        Map<String, Object> initialStockLevel = jdbcTemplate.queryForMap(
                "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        assertThat(new BigDecimal(initialStockLevel.get("reserved_qty").toString()))
                .isEqualByComparingTo(quantityToReserve);

        // Consume creation event
        jmsTemplate.receiveAndConvert(CREATED_TOPIC);

        // Act 2 - Cancel the reservation
        ResponseEntity<SharedReservationResponseDTO> cancelResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/cancel",
                org.springframework.http.HttpMethod.PUT,
                null,
                SharedReservationResponseDTO.class
        );

        // Assert cancellation was successful
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SharedReservationResponseDTO cancelledReservation = cancelResponse.getBody();
        assertThat(cancelledReservation).isNotNull();
        assertThat(cancelledReservation.getReservationId()).isEqualTo(reservationId);
        assertThat(cancelledReservation.getStatus()).isEqualTo("CANCELLED");
        assertThat(cancelledReservation.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);

        // Verify database state after cancellation
        Map<String, Object> cancelledReservationInDb = jdbcTemplate.queryForMap(
                "SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(cancelledReservationInDb.get("status")).isEqualTo("CANCELLED");

        // Verify stock level after cancellation - reserved quantity should be released
        Map<String, Object> stockLevelAfterCancel = jdbcTemplate.queryForMap(
                "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        assertThat(new BigDecimal(stockLevelAfterCancel.get("reserved_qty").toString()))
                .isEqualByComparingTo(BigDecimal.ZERO);

        // Verify cancellation event was published
        Object cancelledMessage = jmsTemplate.receiveAndConvert(CANCELLED_TOPIC);
        assertThat(cancelledMessage).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO cancelledEvent = (SharedReservationEventMessageDTO) cancelledMessage;
        assertThat(cancelledEvent.getEventType()).isEqualTo("CANCELLED");
        assertThat(cancelledEvent.getReservationId()).isEqualTo(reservationId);
        assertThat(cancelledEvent.getStatus()).isEqualTo("CANCELLED");
        assertThat(cancelledEvent.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);
        assertThat(cancelledEvent.getItemId()).isEqualTo(itemId);
        assertThat(cancelledEvent.getLocationId()).isEqualTo(locationId);
    }

    @Test
    void whenReservationConsumed_thenCannotBeConsumedAgain() throws Exception {
        // Arrange - Create and consume a reservation
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        BigDecimal quantityToReserve = new BigDecimal("10.0000");

        SharedReservationRequestDTO createRequest = new SharedReservationRequestDTO();
        createRequest.setItemId(itemId);
        createRequest.setLocationId(locationId);
        createRequest.setProductionRunId(UUID.randomUUID());
        createRequest.setQuantityReserved(quantityToReserve);
        createRequest.setQuantityUnit("PIECES");
        createRequest.setReservedBy(UUID.randomUUID());
        createRequest.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Create reservation
        ResponseEntity<SharedReservationResponseDTO> createResponse = restTemplate.postForEntity(
                "/v1/reservations",
                createRequest,
                SharedReservationResponseDTO.class
        );
        UUID reservationId = createResponse.getBody().getReservationId();

        // Consume creation event
        jmsTemplate.receiveAndConvert(CREATED_TOPIC);

        // Consume the reservation
        restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/consume",
                org.springframework.http.HttpMethod.PUT,
                null,
                SharedReservationResponseDTO.class
        );

        // Consume the consumption event
        jmsTemplate.receiveAndConvert(CONSUMED_TOPIC);

        // Act - Try to consume again
        ResponseEntity<String> secondConsumeResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/consume",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class
        );

        // Assert - Should fail with conflict
        assertThat(secondConsumeResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Verify no additional events were published
        Object message = jmsTemplate.receiveAndConvert(CONSUMED_TOPIC);
        assertThat(message).isNull();
    }

    @Test
    void whenReservationCancelled_thenCannotBeCancelled() throws Exception {
        // Arrange - Create and cancel a reservation
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        BigDecimal quantityToReserve = new BigDecimal("8.0000");

        SharedReservationRequestDTO createRequest = new SharedReservationRequestDTO();
        createRequest.setItemId(itemId);
        createRequest.setLocationId(locationId);
        createRequest.setProductionRunId(UUID.randomUUID());
        createRequest.setQuantityReserved(quantityToReserve);
        createRequest.setQuantityUnit("PIECES");
        createRequest.setReservedBy(UUID.randomUUID());
        createRequest.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Create reservation
        ResponseEntity<SharedReservationResponseDTO> createResponse = restTemplate.postForEntity(
                "/v1/reservations",
                createRequest,
                SharedReservationResponseDTO.class
        );
        UUID reservationId = createResponse.getBody().getReservationId();

        // Consume creation event
        jmsTemplate.receiveAndConvert(CREATED_TOPIC);

        // Cancel the reservation
        restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/cancel",
                org.springframework.http.HttpMethod.PUT,
                null,
                SharedReservationResponseDTO.class
        );

        // Consume the cancellation event
        jmsTemplate.receiveAndConvert(CANCELLED_TOPIC);

        // Act - Try to cancel again
        ResponseEntity<String> secondCancelResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/cancel",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class
        );

        // Assert - Should fail with conflict
        assertThat(secondCancelResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Verify no additional events were published
        Object message = jmsTemplate.receiveAndConvert(CANCELLED_TOPIC);
        assertThat(message).isNull();
    }
}