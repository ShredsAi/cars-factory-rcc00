package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import ai.shreds.application.dtos.ApplicationBatchReservationRequestDTO;

/**
 * DTO for creating a batch of component reservation requests (shared layer).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedBatchReservationRequestDTO {
    @NotNull(message = "Production run ID is required")
    private UUID productionRunId;

    @NotNull(message = "Reserved by user ID is required")
    private UUID reservedBy;

    @NotNull(message = "Expiration time is required")
    @Future(message = "Expiration time must be in the future")
    private LocalDateTime expiresAt;

    @NotEmpty(message = "At least one reservation item is required")
    @Valid
    private List<SharedReservationItemDTO> items;

    /**
     * Convert this shared DTO to the application DTO
     */
    public ApplicationBatchReservationRequestDTO toApplicationDTO() {
        return ApplicationBatchReservationRequestDTO.fromSharedDTO(this);
    }
}