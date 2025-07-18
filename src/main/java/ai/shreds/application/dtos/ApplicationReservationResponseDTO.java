package ai.shreds.application.dtos;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import ai.shreds.shared.dtos.SharedReservationResponseDTO;
import ai.shreds.domain.dtos.DomainReservationResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReservationResponseDTO {

    private UUID reservationId;
    private UUID itemId;
    private UUID locationId;
    private UUID productionRunId;
    private BigDecimal quantityReserved;
    private String quantityUnit;
    private UUID reservedBy;
    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    private String status;

    /**
     * Convert this application DTO to the shared DTO for API responses
     */
    public SharedReservationResponseDTO toSharedDTO() {
        SharedReservationResponseDTO sharedDto = new SharedReservationResponseDTO();
        sharedDto.setReservationId(this.reservationId);
        sharedDto.setItemId(this.itemId);
        sharedDto.setLocationId(this.locationId);
        sharedDto.setProductionRunId(this.productionRunId);
        sharedDto.setQuantityReserved(this.quantityReserved);
        sharedDto.setQuantityUnit(this.quantityUnit);
        sharedDto.setReservedBy(this.reservedBy);
        sharedDto.setReservedAt(this.reservedAt);
        sharedDto.setExpiresAt(this.expiresAt);
        sharedDto.setStatus(this.status);
        return sharedDto;
    }

    /**
     * Create an application DTO from a domain DTO
     */
    public static ApplicationReservationResponseDTO fromDomainDTO(DomainReservationResponseDTO dto) {
        ApplicationReservationResponseDTO appDto = new ApplicationReservationResponseDTO();
        appDto.setReservationId(dto.getReservationId());
        appDto.setItemId(dto.getItemId());
        appDto.setLocationId(dto.getLocationId());
        appDto.setProductionRunId(dto.getProductionRunId());
        appDto.setQuantityReserved(dto.getQuantityReserved());
        appDto.setQuantityUnit(dto.getQuantityUnit());
        appDto.setReservedBy(dto.getReservedBy());
        // Convert Instant to LocalDateTime
        appDto.setReservedAt(LocalDateTime.ofInstant(dto.getReservedAt(), ZoneId.systemDefault()));
        appDto.setExpiresAt(LocalDateTime.ofInstant(dto.getExpiresAt(), ZoneId.systemDefault()));
        appDto.setStatus(dto.getStatus());
        return appDto;
    }
}