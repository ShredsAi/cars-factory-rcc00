package ai.shreds.shared.dtos;

import lombok.*;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import ai.shreds.application.dtos.ApplicationBatchReservationResponseDTO;

/**
 * DTO for returning a batch reservation response (shared layer).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedBatchReservationResponseDTO {
    private UUID productionRunId;
    private List<SharedReservationResponseDTO> reservations;

    /**
     * Convert from application DTO to shared DTO
     */
    public static SharedBatchReservationResponseDTO fromApplicationDTO(ApplicationBatchReservationResponseDTO dto) {
        return SharedBatchReservationResponseDTO.builder()
            .productionRunId(dto.getProductionRunId())
            .reservations(dto.getReservations().stream()
                .map(SharedReservationResponseDTO::fromApplicationDTO)
                .collect(Collectors.toList()))
            .build();
    }
}