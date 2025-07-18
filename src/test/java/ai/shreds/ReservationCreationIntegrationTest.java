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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test-schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ReservationCreationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JmsTemplate jmsTemplate;

    @MockBean
    private DomainOutputPortDeviceProtocolRepository deviceProtocolRepository;

    @MockBean
    private DomainOutputPortGPSDeviceRepository gpsDeviceRepository;

    private final String CREATED_TOPIC = "test.inventory.reservations.created";

    @BeforeEach
    void setUp() {
        // Drain the queue to ensure no messages from previous tests interfere
        jmsTemplate.setReceiveTimeout(100); // Use a short timeout for draining
        while (jmsTemplate.receive(CREATED_TOPIC) != null) {
            // Loop until the queue is empty
        }
        jmsTemplate.setReceiveTimeout(5000); // Reset to the default for tests
    }

    @Test
    void whenValidReservationRequest_thenReservationCreatedAndStockUpdatedAndEventPublished() throws Exception {
        // Arrange
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
        BigDecimal quantityToReserve = new BigDecimal("10.0000");

        SharedReservationRequestDTO request = new SharedReservationRequestDTO();
        request.setItemId(itemId);
        request.setLocationId(locationId);
        request.setProductionRunId(UUID.randomUUID());
        request.setQuantityReserved(quantityToReserve);
        request.setQuantityUnit("PIECES");
        request.setReservedBy(UUID.randomUUID());
        request.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Act
        ResponseEntity<SharedReservationResponseDTO> response = restTemplate.postForEntity(
                "/v1/reservations",
                request,
                SharedReservationResponseDTO.class
        );

        // Assert HTTP Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SharedReservationResponseDTO responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        UUID reservationId = responseBody.getReservationId();
        assertThat(reservationId).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo("ACTIVE");
        assertThat(responseBody.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);

        // Assert Database State (Reservation)
        Map<String, Object> reservationInDb = jdbcTemplate.queryForMap("SELECT * FROM component_reservation WHERE reservation_id = ?", reservationId);
        assertThat(reservationInDb.get("status")).isEqualTo("ACTIVE");
        assertThat(new BigDecimal(reservationInDb.get("quantity_reserved").toString())).isEqualByComparingTo(quantityToReserve);

        // Assert Database State (Stock Level)
        Map<String, Object> stockLevelInDb = jdbcTemplate.queryForMap("SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?", itemId, locationId);
        assertThat(new BigDecimal(stockLevelInDb.get("reserved_qty").toString())).isEqualByComparingTo("10.0000");

        // Assert JMS Event
        Object message = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
        assertThat(message).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
        SharedReservationEventMessageDTO event = (SharedReservationEventMessageDTO) message;

        assertThat(event.getReservationId()).isEqualTo(reservationId);
        assertThat(event.getItemId()).isEqualTo(itemId);
        assertThat(event.getEventType()).isEqualTo("CREATED");
        assertThat(event.getStatus()).isEqualTo("ACTIVE");
        assertThat(event.getQuantityReserved()).isEqualByComparingTo(quantityToReserve);
    }

    @Test
    void whenInsufficientStock_thenReservationRejectedAndNoChangesMade() throws Exception {
        // Arrange
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
        UUID locationId = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
        BigDecimal quantityToReserve = new BigDecimal("60.0000"); // More than available (50.0000)

        Map<String, Object> initialStockLevel = jdbcTemplate.queryForMap(
                "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        BigDecimal initialReservedQty = new BigDecimal(initialStockLevel.get("reserved_qty").toString());

        Integer initialReservationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM component_reservation WHERE item_id = ? AND location_id = ?",
                Integer.class, itemId, locationId);

        SharedReservationRequestDTO request = new SharedReservationRequestDTO();
        request.setItemId(itemId);
        request.setLocationId(locationId);
        request.setProductionRunId(UUID.randomUUID());
        request.setQuantityReserved(quantityToReserve);
        request.setQuantityUnit("PIECES");
        request.setReservedBy(UUID.randomUUID());
        request.setExpiresAt(LocalDateTime.now().plusDays(1));

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/v1/reservations",
                request,
                String.class
        );

        // Assert HTTP Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Assert Database State (Stock Level)
        Map<String, Object> stockLevelAfter = jdbcTemplate.queryForMap(
                "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
                itemId, locationId);
        BigDecimal reservedQtyAfter = new BigDecimal(stockLevelAfter.get("reserved_qty").toString());
        assertThat(reservedQtyAfter).isEqualByComparingTo(initialReservedQty);

        // Assert Database State (Reservation)
        Integer reservationCountAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM component_reservation WHERE item_id = ? AND location_id = ?",
                Integer.class, itemId, locationId);
        assertThat(reservationCountAfter).isEqualTo(initialReservationCount);

        // Assert JMS Event - no event should be published
        Object message = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
        assertThat(message).isNull();
    }
}