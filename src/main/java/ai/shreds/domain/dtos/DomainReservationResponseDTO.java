package ai.shreds.domain.dtos;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain DTO representing a reservation response.
 */
public class DomainReservationResponseDTO {
    
    private final UUID reservationId;
    private final UUID itemId;
    private final UUID locationId;
    private final UUID productionRunId;
    private final BigDecimal quantityReserved;
    private final String quantityUnit;
    private final UUID reservedBy;
    private final Instant reservedAt;
    private final Instant expiresAt;
    private final String status;
    
    public DomainReservationResponseDTO(
            UUID reservationId,
            UUID itemId,
            UUID locationId,
            UUID productionRunId,
            BigDecimal quantityReserved,
            String quantityUnit,
            UUID reservedBy,
            Instant reservedAt,
            Instant expiresAt,
            String status) {
        this.reservationId = reservationId;
        this.itemId = itemId;
        this.locationId = locationId;
        this.productionRunId = productionRunId;
        this.quantityReserved = quantityReserved;
        this.quantityUnit = quantityUnit;
        this.reservedBy = reservedBy;
        this.reservedAt = reservedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }
    
    /**
     * Create DTO from domain entity
     */
    public static DomainReservationResponseDTO fromEntity(DomainComponentReservationEntity entity) {
        return new DomainReservationResponseDTO(
                entity.getReservationId().getValue(),
                entity.getItemId().getValue(),
                entity.getLocationId().getValue(),
                entity.getProductionRunId().getValue(),
                entity.getQuantityReserved().getValue(),
                entity.getQuantityReserved().getUnit(),
                entity.getReservedBy().getValue(),
                entity.getReservedAt(),
                entity.getExpiresAt(),
                entity.getStatus().getValue()
        );
    }
    
    // Getters
    public UUID getReservationId() {
        return reservationId;
    }
    
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
    
    public Instant getReservedAt() {
        return reservedAt;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "DomainReservationResponseDTO{" +
                "reservationId=" + reservationId +
                ", itemId=" + itemId +
                ", locationId=" + locationId +
                ", productionRunId=" + productionRunId +
                ", quantityReserved=" + quantityReserved +
                ", quantityUnit='" + quantityUnit + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}