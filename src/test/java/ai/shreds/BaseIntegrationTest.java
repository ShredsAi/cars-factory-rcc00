package ai.shreds;

import ai.shreds.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
@Import(TestSecurityConfig.class) // Removed TestMessagingConfig
public abstract class BaseIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // --- Database Configuration ---
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "sa");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        // --- Messaging Configuration ---
        // Rely on Spring Boot's auto-configuration for the embedded Artemis broker
        registry.add("spring.activemq.broker-url", () -> "vm://localhost?broker.persistent=false");

        // --- Application-specific Test Configuration ---
        registry.add("reservation.topics.reservation-created", () -> "test.inventory.reservations.created");
        registry.add("reservation.topics.reservation-expired", () -> "test.inventory.reservations.expired");
        registry.add("reservation.topics.reservation-consumed", () -> "test.inventory.reservations.consumed");
        registry.add("reservation.topics.reservation-cancelled", () -> "test.inventory.reservations.cancelled");
    }
}