package ai.shreds;

import ai.shreds.application.ports.ApplicationProcessExpiredReservationsInputPort;
import ai.shreds.domain.ports.DomainOutputPortDeviceProtocolRepository;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;
import ai.shreds.shared.dtos.SharedReservationRequestDTO;
import ai.shreds.shared.dtos.SharedReservationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.OutputCaptureExtension;
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
 * Integration test for reservation expiration processing.
 * Tests the automatic expiration of reservations, which is critical for releasing unused stock back to inventory.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
@Sql(scripts = {"/test-schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationExpirationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ApplicationProcessExpiredReservationsInputPort processExpiredReservationsInputPort;

    @Autowired
    private JmsTemplate jmsTemplate;

    @MockBean
    private DomainOutputPortDeviceProtocolRepository deviceProtocolRepository;

    @MockBean
    private DomainOutputPortGPSDeviceRepository gpsDeviceRepository;

    private final String CREATED_TOPIC = "test.inventory.reservations.created";
    private final String EXPIRED_TOPIC = "test.inventory.reservations.expired";

    @BeforeEach
    void setUp() {
        // Drain all queues to ensure no messages from previous tests interfere
        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(CREATED_TOPIC) != null) {}
        while (jmsTemplate.receive(EXPIRED_TOPIC) != null) {}
        jmsTemplate.setReceiveTimeout(5000);
    }

    @Test
    void whenReservationExpires_thenStatusUpdatedAndStockReleased() throws Exception {
        // Arrange - Use a different item/location to avoid conflicts with existing test data
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
        // Set expiration to 5 seconds in the future to pass validation
        createRequest.setExpiresAt(LocalDateTime.now().plusSeconds(5));

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

        // Verify initial database state
        Map<String, Object> reservationBeforeExpiration = jdbcTemplate.queryForMap(
                "SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(reservationBeforeExpiration.get("status")).isEqualTo("ACTIVE");

        // Verify stock level after creation - should have reserved quantity
        Map<String, Object> stockLevelBeforeExpiration = jdbcTemplate.queryForMap(
                "SELECT quantity_on_hand, reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        assertThat(new BigDecimal(stockLevelBeforeExpiration.get("reserved_qty").toString()))
                .isEqualByComparingTo(quantityToReserve);

        // Consume creation event
        Object createdMessage = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
        assertThat(createdMessage).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO createdEvent = (SharedReservationEventMessageDTO) createdMessage;
        assertThat(createdEvent.getEventType()).isEqualTo("CREATED");
        assertThat(createdEvent.getReservationId()).isEqualTo(reservationId);

        // Wait for the reservation to naturally expire (wait 6 seconds)
        Thread.sleep(6000);

        // Act 2 - Process expired reservations
        processExpiredReservationsInputPort.processExpiredReservations();

        // Assert expiration was processed correctly
        
        // Verify database state after expiration
        Map<String, Object> reservationAfterExpiration = jdbcTemplate.queryForMap(
                "SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(reservationAfterExpiration.get("status")).isEqualTo("EXPIRED");

        // Verify stock level after expiration - reserved quantity should be released
        Map<String, Object> stockLevelAfterExpiration = jdbcTemplate.queryForMap(
                "SELECT quantity_on_hand, reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        
        // Initial stock was 50, should remain the same
        assertThat(new BigDecimal(stockLevelAfterExpiration.get("quantity_on_hand").toString()))
                .isEqualByComparingTo(new BigDecimal("50.0000"));
        
        // Reserved quantity should be released back to 0
        assertThat(new BigDecimal(stockLevelAfterExpiration.get("reserved_qty").toString()))
                .isEqualByComparingTo(BigDecimal.ZERO);

        // Verify expiration event was published
        Object expiredMessage = jmsTemplate.receiveAndConvert(EXPIRED_TOPIC);
        assertThat(expiredMessage).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO expiredEvent = (SharedReservationEventMessageDTO) expiredMessage;
        assertThat(expiredEvent.getEventType()).isEqualTo("EXPIRED");
        assertThat(expiredEvent.getReservationId()).isEqualTo(reservationId);
        assertThat(expiredEvent.getStatus()).isEqualTo("EXPIRED");
        assertThat(expiredEvent.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);
        assertThat(expiredEvent.getItemId()).isEqualTo(itemId);
        assertThat(expiredEvent.getLocationId()).isEqualTo(locationId);

        // Verify that we can still retrieve the expired reservation
        ResponseEntity<SharedReservationResponseDTO> getResponse = restTemplate.getForEntity(
                "/v1/reservations/" + reservationId,
                SharedReservationResponseDTO.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        SharedReservationResponseDTO retrievedReservation = getResponse.getBody();
        assertThat(retrievedReservation).isNotNull();
        assertThat(retrievedReservation.getReservationId()).isEqualTo(reservationId);
        assertThat(retrievedReservation.getStatus()).isEqualTo("EXPIRED");

        // Verify that expired reservations cannot be consumed or cancelled
        ResponseEntity<String> consumeResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/consume",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class
        );
        assertThat(consumeResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ResponseEntity<String> cancelResponse = restTemplate.exchange(
                "/v1/reservations/" + reservationId + "/cancel",
                org.springframework.http.HttpMethod.PUT,
                null,
                String.class
        );
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Verify no additional events were published
        Object additionalMessage = jmsTemplate.receiveAndConvert(EXPIRED_TOPIC);
        assertThat(additionalMessage).isNull();
    }
}