package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.*;
import ai.shreds.domain.exceptions.*;
import java.time.Instant;

/**
 * Domain entity representing a component reservation.
 * This entity encapsulates the complete lifecycle of a component reservation
 * including creation, consumption, cancellation, and expiration.
 */
public class DomainComponentReservationEntity {
    
    private final DomainValueReservationId reservationId;
    private final DomainValueItemId itemId;
    private final DomainValueLocationId locationId;
    private final DomainValueProductionRunId productionRunId;
    private final DomainValueQuantity quantityReserved;
    private final DomainValueUserId reservedBy;
    private final Instant reservedAt;
    private final Instant expiresAt;
    private DomainValueReservationStatus status;
    
    public DomainComponentReservationEntity(
            DomainValueReservationId reservationId,
            DomainValueItemId itemId,
            DomainValueLocationId locationId,
            DomainValueProductionRunId productionRunId,
            DomainValueQuantity quantityReserved,
            DomainValueUserId reservedBy,
            Instant reservedAt,
            Instant expiresAt,
            DomainValueReservationStatus status) {
        
        validateReservationDates(reservedAt, expiresAt);
        validateQuantity(quantityReserved);
        validateRequiredFields(reservationId, itemId, locationId, productionRunId, reservedBy, status);
        
        this.reservationId = reservationId;
        this.itemId = itemId;
        this.locationId = locationId;
        this.productionRunId = productionRunId;
        this.quantityReserved = quantityReserved;
        this.reservedBy = reservedBy;
        this.reservedAt = reservedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }
    
    private void validateRequiredFields(DomainValueReservationId reservationId,
                                       DomainValueItemId itemId,
                                       DomainValueLocationId locationId,
                                       DomainValueProductionRunId productionRunId,
                                       DomainValueUserId reservedBy,
                                       DomainValueReservationStatus status) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID cannot be null");
        }
        if (productionRunId == null) {
            throw new IllegalArgumentException("Production run ID cannot be null");
        }
        if (reservedBy == null) {
            throw new IllegalArgumentException("Reserved by user ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Reservation status cannot be null");
        }
    }
    
    private void validateReservationDates(Instant reservedAt, Instant expiresAt) {
        if (reservedAt == null) {
            throw new IllegalArgumentException("Reserved at timestamp cannot be null");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expires at timestamp cannot be null");
        }
        if (expiresAt.isBefore(reservedAt)) {
            throw new IllegalArgumentException("Expiration date must be after reservation date");
        }
    }
    
    private void validateQuantity(DomainValueQuantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (quantity.getValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
    
    /**
     * Cancel the reservation if it's currently active
     */
    public void cancel() {
        if (!status.isActive()) {
            throw new DomainExceptionInvalidReservationState(reservationId, status, "cancel");
        }
        this.status = DomainValueReservationStatus.cancelled();
    }
    
    /**
     * Mark the reservation as consumed if it's currently active
     */
    public void consume() {
        if (!status.isActive()) {
            throw new DomainExceptionInvalidReservationState(reservationId, status, "consume");
        }
        this.status = DomainValueReservationStatus.consumed();
    }
    
    /**
     * Mark the reservation as expired if it's currently active
     */
    public void expire() {
        if (!status.isActive()) {
            throw new DomainExceptionInvalidReservationState(reservationId, status, "expire");
        }
        this.status = DomainValueReservationStatus.expired();
    }
    
    /**
     * Check if the reservation is expired based on current time
     */
    public boolean isExpired() {
        return status.isActive() && Instant.now().isAfter(expiresAt);
    }
    
    /**
     * Check if the reservation can be modified (cancelled or consumed)
     */
    public boolean canBeModified() {
        return status.isActive();
    }
    
    /**
     * Get the key for stock level operations (item + location)
     */
    public String getStockLevelKey() {
        return itemId.toString() + ":" + locationId.toString();
    }
    
    /**
     * Check if this reservation affects the same stock level as another reservation
     */
    public boolean affectsSameStockLevel(DomainComponentReservationEntity other) {
        return this.itemId.equals(other.itemId) && this.locationId.equals(other.locationId);
    }
    
    // Getters
    public DomainValueReservationId getReservationId() {
        return reservationId;
    }
    
    public DomainValueItemId getItemId() {
        return itemId;
    }
    
    public DomainValueLocationId getLocationId() {
        return locationId;
    }
    
    public DomainValueProductionRunId getProductionRunId() {
        return productionRunId;
    }
    
    public DomainValueQuantity getQuantityReserved() {
        return quantityReserved;
    }
    
    public DomainValueUserId getReservedBy() {
        return reservedBy;
    }
    
    public Instant getReservedAt() {
        return reservedAt;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public DomainValueReservationStatus getStatus() {
        return status;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainComponentReservationEntity that = (DomainComponentReservationEntity) o;
        return reservationId.equals(that.reservationId);
    }
    
    @Override
    public int hashCode() {
        return reservationId.hashCode();
    }
    
    @Override
    public String toString() {
        return "DomainComponentReservationEntity{" +
                "reservationId=" + reservationId +
                ", itemId=" + itemId +
                ", locationId=" + locationId +
                ", productionRunId=" + productionRunId +
                ", quantityReserved=" + quantityReserved +
                ", status=" + status +
                ", reservedAt=" + reservedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}