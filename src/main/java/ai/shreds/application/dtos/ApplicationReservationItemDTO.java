package ai.shreds.application.dtos;

import java.util.UUID;
import java.math.BigDecimal;

import ai.shreds.domain.dtos.DomainReservationItemDTO;
import ai.shreds.shared.dtos.SharedReservationItemDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReservationItemDTO {
    private UUID itemId;
    private UUID locationId;
    private BigDecimal quantityReserved;
    private String quantityUnit;

    /**
     * Convert this application DTO to the domain DTO
     */
    public DomainReservationItemDTO toDomainDTO() {
        return DomainReservationItemDTO.fromApplicationDTO(this);
    }

    /**
     * Create an application DTO from a shared DTO
     */
    public static ApplicationReservationItemDTO fromSharedDTO(SharedReservationItemDTO dto) {
        ApplicationReservationItemDTO appDto = new ApplicationReservationItemDTO();
        appDto.setItemId(dto.getItemId());
        appDto.setLocationId(dto.getLocationId());
        appDto.setQuantityReserved(dto.getQuantityReserved());
        appDto.setQuantityUnit(dto.getQuantityUnit());
        return appDto;
    }
}