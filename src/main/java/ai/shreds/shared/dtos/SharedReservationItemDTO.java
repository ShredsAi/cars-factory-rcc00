package ai.shreds.shared.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for individual reservation items used in batch reservation requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedReservationItemDTO {
    @NotNull(message = "Item ID is required")
    private UUID itemId;
    
    @NotNull(message = "Location ID is required")
    private UUID locationId;
    
    @NotNull(message = "Quantity must be specified")
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantityReserved;
    
    @NotNull(message = "Quantity unit is required")
    private String quantityUnit;
    
    /**
     * Converts this SharedReservationItemDTO to ApplicationReservationItemDTO
     * @return ApplicationReservationItemDTO
     */
    public ai.shreds.application.dtos.ApplicationReservationItemDTO toApplicationDTO() {
        return ai.shreds.application.dtos.ApplicationReservationItemDTO.builder()
                .itemId(itemId)
                .locationId(locationId)
                .quantityReserved(quantityReserved)
                .quantityUnit(quantityUnit)
                .build();
    }
}