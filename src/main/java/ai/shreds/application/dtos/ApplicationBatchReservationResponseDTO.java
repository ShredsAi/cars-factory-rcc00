package ai.shreds.application.dtos;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import ai.shreds.domain.dtos.DomainBatchReservationResponseDTO;
import ai.shreds.shared.dtos.SharedBatchReservationResponseDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a batch reservation response in the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationBatchReservationResponseDTO {
    private UUID productionRunId;
    private List<ApplicationReservationResponseDTO> reservations = new ArrayList<>();

    /**
     * Convert this application DTO to a shared DTO for API response.
     */
    public SharedBatchReservationResponseDTO toSharedDTO() {
        SharedBatchReservationResponseDTO shared = new SharedBatchReservationResponseDTO();
        shared.setProductionRunId(this.productionRunId);
        shared.setReservations(this.reservations.stream()
            .map(ApplicationReservationResponseDTO::toSharedDTO)
            .collect(Collectors.toList()));
        return shared;
    }

    /**
     * Create an application DTO from a domain DTO.
     */
    public static ApplicationBatchReservationResponseDTO fromDomainDTO(DomainBatchReservationResponseDTO dto) {
        ApplicationBatchReservationResponseDTO appDto = new ApplicationBatchReservationResponseDTO();
        appDto.setProductionRunId(dto.getProductionRunId());
        if (dto.getCreatedReservations() != null) {
            appDto.setReservations(dto.getCreatedReservations().stream()
                .map(ApplicationReservationResponseDTO::fromDomainDTO)
                .collect(Collectors.toList()));
        }
        return appDto;
    }
}