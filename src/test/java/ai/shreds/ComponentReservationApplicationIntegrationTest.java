package ai.shreds;

import ai.shreds.domain.ports.DomainOutputPortDeviceProtocolRepository;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Integration test for the Component Reservation Application.
 * This test verifies that the Spring Boot application context loads successfully.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComponentReservationApplicationIntegrationTest extends BaseIntegrationTest {

    @MockBean
    private DomainOutputPortDeviceProtocolRepository deviceProtocolRepository;

    @MockBean
    private DomainOutputPortGPSDeviceRepository gpsDeviceRepository;

    @Test
    void contextLoads() {
        // If the application context fails to load, this test will fail automatically.
        // An empty method is sufficient to verify a successful context load.
    }
}