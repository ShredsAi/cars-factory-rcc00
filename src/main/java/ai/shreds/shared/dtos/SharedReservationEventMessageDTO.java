package ai.shreds.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedReservationEventMessageDTO {
    private UUID reservationId;
    private UUID itemId;
    private UUID locationId;
    private UUID productionRunId;
    private BigDecimal quantityReserved;
    private String quantityUnit;
    private String status;
    private String eventType;
    private LocalDateTime timestamp;
}
