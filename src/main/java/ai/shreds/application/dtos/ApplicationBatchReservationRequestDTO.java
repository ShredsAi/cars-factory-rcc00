package ai.shreds.application.dtos;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.shared.dtos.SharedBatchReservationRequestDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for batch reservation requests at the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationBatchReservationRequestDTO {
    private UUID productionRunId;
    private UUID reservedBy;
    private LocalDateTime expiresAt;
    private List<ApplicationReservationItemDTO> items;

    /**
     * Convert to domain batch reservation request DTO.
     */
    public DomainBatchReservationRequestDTO toDomainDTO() {
        return DomainBatchReservationRequestDTO.fromApplicationDTO(this);
    }

    /**
     * Create an application DTO from a shared DTO.
     */
    public static ApplicationBatchReservationRequestDTO fromSharedDTO(SharedBatchReservationRequestDTO dto) {
        ApplicationBatchReservationRequestDTO appDto = new ApplicationBatchReservationRequestDTO();
        appDto.setProductionRunId(dto.getProductionRunId());
        appDto.setReservedBy(dto.getReservedBy());
        appDto.setExpiresAt(dto.getExpiresAt());
        appDto.setItems(dto.getItems().stream()
            .map(ApplicationReservationItemDTO::fromSharedDTO)
            .collect(Collectors.toList()));
        return appDto;
    }
}
