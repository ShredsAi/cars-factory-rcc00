package ai.shreds.shared.dtos;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Future;
import java.util.UUID;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import ai.shreds.application.dtos.ApplicationReservationRequestDTO;

/**
 * DTO for creating a single component reservation request (shared layer).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedReservationRequestDTO {
    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotNull(message = "Location ID is required")
    private UUID locationId;

    @NotNull(message = "Production run ID is required")
    private UUID productionRunId;

    @NotNull(message = "Quantity must be specified")
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantityReserved;

    @NotNull(message = "Quantity unit is required")
    private String quantityUnit;

    @NotNull(message = "Reserved by user ID is required")
    private UUID reservedBy;

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration time must be in the future")
    private LocalDateTime expiresAt;

    /**
     * Convert this shared DTO to the application DTO
     */
    public ApplicationReservationRequestDTO toApplicationDTO() {
        return ApplicationReservationRequestDTO.fromSharedDTO(this);
    }
}