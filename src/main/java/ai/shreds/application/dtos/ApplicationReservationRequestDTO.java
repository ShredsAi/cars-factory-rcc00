package ai.shreds.application.dtos;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import ai.shreds.domain.dtos.DomainReservationRequestDTO;
import ai.shreds.shared.dtos.SharedReservationRequestDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReservationRequestDTO {

    private UUID itemId;
    private UUID locationId;
    private UUID productionRunId;
    private BigDecimal quantityReserved;
    private String quantityUnit;
    private UUID reservedBy;
    private LocalDateTime expiresAt;

    /**
     * Convert this application DTO to the domain DTO
     */
    public DomainReservationRequestDTO toDomainDTO() {
        return DomainReservationRequestDTO.fromApplicationDTO(this);
    }

    /**
     * Create an application DTO from a shared DTO
     */
    public static ApplicationReservationRequestDTO fromSharedDTO(SharedReservationRequestDTO dto) {
        ApplicationReservationRequestDTO appDto = new ApplicationReservationRequestDTO();
        appDto.setItemId(dto.getItemId());
        appDto.setLocationId(dto.getLocationId());
        appDto.setProductionRunId(dto.getProductionRunId());
        appDto.setQuantityReserved(dto.getQuantityReserved());
        appDto.setQuantityUnit(dto.getQuantityUnit());
        appDto.setReservedBy(dto.getReservedBy());
        appDto.setExpiresAt(dto.getExpiresAt());
        return appDto;
    }
}
