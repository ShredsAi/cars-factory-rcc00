package ai.shreds;

import ai.shreds.domain.ports.DomainOutputPortDeviceProtocolRepository;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import ai.shreds.shared.dtos.SharedBatchReservationRequestDTO;
import ai.shreds.shared.dtos.SharedBatchReservationResponseDTO;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;
import ai.shreds.shared.dtos.SharedReservationItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/test-schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BatchReservationIntegrationTest extends BaseIntegrationTest {

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
        jmsTemplate.setReceiveTimeout(100);
        while (jmsTemplate.receive(CREATED_TOPIC) != null) {}
        jmsTemplate.setReceiveTimeout(5000);
    }

    @Test
    void whenValidBatchRequest_thenAllReservationsCreatedAtomically() throws Exception {
        // Arrange
        UUID productionRunId = UUID.randomUUID();
        UUID reservedBy = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        List<SharedReservationItemDTO> items = Arrays.asList(
            SharedReservationItemDTO.builder()
                .itemId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .locationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .quantityReserved(new BigDecimal("15.0000"))
                .quantityUnit("PIECES")
                .build(),
            SharedReservationItemDTO.builder()
                .itemId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .locationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"))
                .quantityReserved(new BigDecimal("20.0000"))
                .quantityUnit("PIECES")
                .build(),
            SharedReservationItemDTO.builder()
                .itemId(UUID.fromString("123e4567-e89b-12d3-a456-426614174004"))
                .locationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174005"))
                .quantityReserved(new BigDecimal("25.0000"))
                .quantityUnit("PIECES")
                .build()
        );

        SharedBatchReservationRequestDTO batchRequest = SharedBatchReservationRequestDTO.builder()
            .productionRunId(productionRunId)
            .reservedBy(reservedBy)
            .expiresAt(expiresAt)
            .items(items)
            .build();

        // Act
        ResponseEntity<SharedBatchReservationResponseDTO> response = restTemplate.postForEntity(
            "/v1/reservations/batch",
            batchRequest,
            SharedBatchReservationResponseDTO.class
        );

        // Assert HTTP Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        SharedBatchReservationResponseDTO responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getProductionRunId()).isEqualTo(productionRunId);
        assertThat(responseBody.getReservations()).hasSize(3);

        // Assert Database State - All reservations created
        Integer finalReservationCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM component_reservation WHERE production_run_id = ?",
            Integer.class, productionRunId);
        assertThat(finalReservationCount).isEqualTo(3);

        // Assert Database State - All stock levels updated
        Map<String, Object> finalStock1 = jdbcTemplate.queryForMap(
            "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
            items.get(0).getItemId(), items.get(0).getLocationId());
        assertThat(new BigDecimal(finalStock1.get("reserved_qty").toString())).isEqualByComparingTo("25.0000");

        Map<String, Object> finalStock2 = jdbcTemplate.queryForMap(
            "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
            items.get(1).getItemId(), items.get(1).getLocationId());
        assertThat(new BigDecimal(finalStock2.get("reserved_qty").toString())).isEqualByComparingTo("20.0000");

        Map<String, Object> finalStock3 = jdbcTemplate.queryForMap(
            "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
            items.get(2).getItemId(), items.get(2).getLocationId());
        assertThat(new BigDecimal(finalStock3.get("reserved_qty").toString())).isEqualByComparingTo("25.0000");

        // Assert JMS Events
        for (int i = 0; i < 3; i++) {
            Object message = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
            assertThat(message).isNotNull().isInstanceOf(SharedReservationEventMessageDTO.class);
            SharedReservationEventMessageDTO event = (SharedReservationEventMessageDTO) message;
            assertThat(event.getEventType()).isEqualTo("CREATED");
            assertThat(event.getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Test
    void whenOneItemHasInsufficientStock_thenNoReservationsCreated() throws Exception {
        // Arrange
        UUID productionRunId = UUID.randomUUID();
        UUID reservedBy = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1);

        List<SharedReservationItemDTO> items = Arrays.asList(
            SharedReservationItemDTO.builder()
                .itemId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .locationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174001"))
                .quantityReserved(new BigDecimal("15.0000"))
                .quantityUnit("PIECES")
                .build(),
            SharedReservationItemDTO.builder()
                .itemId(UUID.fromString("123e4567-e89b-12d3-a456-426614174002"))
                .locationId(UUID.fromString("123e4567-e89b-12d3-a456-426614174003"))
                .quantityReserved(new BigDecimal("60.0000"))
                .quantityUnit("PIECES")
                .build()
        );

        SharedBatchReservationRequestDTO batchRequest = SharedBatchReservationRequestDTO.builder()
            .productionRunId(productionRunId)
            .reservedBy(reservedBy)
            .expiresAt(expiresAt)
            .items(items)
            .build();

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/v1/reservations/batch",
            batchRequest,
            String.class
        );

        // Assert HTTP Response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // Assert Database State - No reservations created
        Integer finalReservationCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM component_reservation WHERE production_run_id = ?",
            Integer.class, productionRunId);
        assertThat(finalReservationCount).isZero();

        // Assert Database State - Stock levels unchanged
        Map<String, Object> finalStock1 = jdbcTemplate.queryForMap(
            "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
            items.get(0).getItemId(), items.get(0).getLocationId());
        assertThat(new BigDecimal(finalStock1.get("reserved_qty").toString())).isEqualByComparingTo("10.0000");

        Map<String, Object> finalStock2 = jdbcTemplate.queryForMap(
            "SELECT reserved_qty FROM stock_level WHERE item_id = ? AND location_id = ?",
            items.get(1).getItemId(), items.get(1).getLocationId());
        assertThat(new BigDecimal(finalStock2.get("reserved_qty").toString())).isEqualByComparingTo("0.0000");

        // Assert JMS Event - no event should be published
        Object message = jmsTemplate.receiveAndConvert(CREATED_TOPIC);
        assertThat(message).isNull();
    }
}