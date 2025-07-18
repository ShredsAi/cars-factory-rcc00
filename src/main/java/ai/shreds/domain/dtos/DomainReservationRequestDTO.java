package ai.shreds.domain.dtos;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.*;
import ai.shreds.application.dtos.ApplicationReservationRequestDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Domain DTO representing a reservation request.
 */
public class DomainReservationRequestDTO {
    
    private final UUID itemId;
    private final UUID locationId;
    private final UUID productionRunId;
    private final BigDecimal quantityReserved;
    private final String quantityUnit;
    private final UUID reservedBy;
    private final Instant expiresAt;
    
    public DomainReservationRequestDTO(
            UUID itemId,
            UUID locationId,
            UUID productionRunId,
            BigDecimal quantityReserved,
            String quantityUnit,
            UUID reservedBy,
            Instant expiresAt) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.productionRunId = productionRunId;
        this.quantityReserved = quantityReserved;
        this.quantityUnit = quantityUnit;
        this.reservedBy = reservedBy;
        this.expiresAt = expiresAt;
    }
    
    /**
     * Convert to domain value objects
     */
    public DomainValueItemId getItemIdValue() {
        return DomainValueItemId.from(itemId);
    }
    
    public DomainValueLocationId getLocationIdValue() {
        return DomainValueLocationId.from(locationId);
    }
    
    public DomainValueProductionRunId getProductionRunIdValue() {
        return DomainValueProductionRunId.from(productionRunId);
    }
    
    public DomainValueQuantity getQuantityReservedValue() {
        return DomainValueQuantity.of(quantityReserved, quantityUnit);
    }
    
    public DomainValueUserId getReservedByValue() {
        return DomainValueUserId.from(reservedBy);
    }
    
    /**
     * Create from application DTO
     */
    public static DomainReservationRequestDTO fromApplicationDTO(Object applicationDto) {
        if (applicationDto instanceof ApplicationReservationRequestDTO) {
            ApplicationReservationRequestDTO appDto = (ApplicationReservationRequestDTO) applicationDto;
            return new DomainReservationRequestDTO(
                    appDto.getItemId(),
                    appDto.getLocationId(),
                    appDto.getProductionRunId(),
                    appDto.getQuantityReserved(),
                    appDto.getQuantityUnit(),
                    appDto.getReservedBy(),
                    appDto.getExpiresAt().toInstant(ZoneOffset.UTC)
            );
        }
        throw new IllegalArgumentException("Invalid application DTO type: " + applicationDto.getClass().getName());
    }
    
    // Getters
    public UUID getItemId() {
        return itemId;
    }
    
    public UUID getLocationId() {
        return locationId;
    }
    
    public UUID getProductionRunId() {
        return productionRunId;
    }
    
    public BigDecimal getQuantityReserved() {
        return quantityReserved;
    }
    
    public String getQuantityUnit() {
        return quantityUnit;
    }
    
    public UUID getReservedBy() {
        return reservedBy;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    @Override
    public String toString() {
        return "DomainReservationRequestDTO{" +
                "itemId=" + itemId +
                ", locationId=" + locationId +
                ", productionRunId=" + productionRunId +
                ", quantityReserved=" + quantityReserved +
                ", quantityUnit='" + quantityUnit + '\'' +
                ", reservedBy=" + reservedBy +
                ", expiresAt=" + expiresAt +
                '}';
    }
}