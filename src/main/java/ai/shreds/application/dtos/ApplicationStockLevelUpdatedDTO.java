package ai.shreds.application.dtos;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import ai.shreds.shared.dtos.SharedStockLevelUpdatedMessageDTO;
import ai.shreds.domain.dtos.DomainStockLevelUpdatedDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for stock level update events at the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStockLevelUpdatedDTO {
    private UUID itemId;
    private UUID locationId;
    private BigDecimal newStockLevel;
    private LocalDateTime timestamp;

    /**
     * Create an application DTO from a shared JMS message DTO.
     */
    public static ApplicationStockLevelUpdatedDTO fromSharedDTO(SharedStockLevelUpdatedMessageDTO dto) {
        ApplicationStockLevelUpdatedDTO appDto = new ApplicationStockLevelUpdatedDTO();
        appDto.setItemId(dto.getItemId());
        appDto.setLocationId(dto.getLocationId());
        appDto.setNewStockLevel(dto.getNewStockLevel());
        appDto.setTimestamp(dto.getTimestamp());
        return appDto;
    }

    /**
     * Convert this application DTO to the domain DTO.
     */
    public DomainStockLevelUpdatedDTO toDomainDTO() {
        return new DomainStockLevelUpdatedDTO(
            this.itemId,
            this.locationId,
            this.newStockLevel,
            "PIECES" // Default unit, should be properly handled in a real scenario
        );
    }
}