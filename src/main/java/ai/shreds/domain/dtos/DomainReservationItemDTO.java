package ai.shreds.domain.dtos;

import ai.shreds.application.dtos.ApplicationReservationItemDTO;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain DTO representing a single item in a batch reservation request.
 */
public class DomainReservationItemDTO {
    
    private final UUID itemId;
    private final UUID locationId;
    private final BigDecimal quantityReserved;
    private final String quantityUnit;
    
    public DomainReservationItemDTO(
            UUID itemId,
            UUID locationId,
            BigDecimal quantityReserved,
            String quantityUnit) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.quantityReserved = quantityReserved;
        this.quantityUnit = quantityUnit;
    }
    
    /**
     * Create from application DTO
     */
    public static DomainReservationItemDTO fromApplicationDTO(ApplicationReservationItemDTO appDto) {
        if (appDto == null) {
            return null;
        }
        return new DomainReservationItemDTO(
                appDto.getItemId(),
                appDto.getLocationId(),
                appDto.getQuantityReserved(),
                appDto.getQuantityUnit()
        );
    }
    
    // Getters
    public UUID getItemId() {
        return itemId;
    }
    
    public UUID getLocationId() {
        return locationId;
    }
    
    public BigDecimal getQuantityReserved() {
        return quantityReserved;
    }
    
    public String getQuantityUnit() {
        return quantityUnit;
    }
    
    @Override
    public String toString() {
        return "DomainReservationItemDTO{" +
                "itemId=" + itemId +
                ", locationId=" + locationId +
                ", quantityReserved=" + quantityReserved +
                ", quantityUnit='" + quantityUnit + '\'' +
                '}';
    }
}