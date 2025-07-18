package ai.shreds.shared.dtos;

import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
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
public class SharedReservationResponseDTO {
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
     * Convert from application DTO to shared DTO
     */
    public static SharedReservationResponseDTO fromApplicationDTO(ApplicationReservationResponseDTO dto) {
        return SharedReservationResponseDTO.builder()
            .reservationId(dto.getReservationId())
            .itemId(dto.getItemId())
            .locationId(dto.getLocationId())
            .productionRunId(dto.getProductionRunId())
            .quantityReserved(dto.getQuantityReserved())
            .quantityUnit(dto.getQuantityUnit())
            .reservedBy(dto.getReservedBy())
            .reservedAt(dto.getReservedAt())
            .expiresAt(dto.getExpiresAt())
            .status(dto.getStatus())
            .build();
    }
}