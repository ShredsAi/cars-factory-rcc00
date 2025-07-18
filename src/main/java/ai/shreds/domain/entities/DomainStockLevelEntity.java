package ai.shreds.domain.entities;

import ai.shreds.domain.value_objects.*;
import ai.shreds.domain.rules.DomainStockAvailabilityRules;

/**
 * Domain entity representing a stock level.
 * This entity represents the shared stock_level table and manages stock quantities.
 */
public class DomainStockLevelEntity {
    
    private final DomainValueItemId itemId;
    private final DomainValueLocationId locationId;
    private DomainValueQuantity quantityOnHand;
    private DomainValueQuantity reservedQuantity;
    
    public DomainStockLevelEntity(
            DomainValueItemId itemId,
            DomainValueLocationId locationId,
            DomainValueQuantity quantityOnHand,
            DomainValueQuantity reservedQuantity) {
        
        validateRequiredFields(itemId, locationId, quantityOnHand, reservedQuantity);
        validateQuantityConstraints(quantityOnHand, reservedQuantity);
        
        this.itemId = itemId;
        this.locationId = locationId;
        this.quantityOnHand = quantityOnHand;
        this.reservedQuantity = reservedQuantity;
    }
    
    private void validateRequiredFields(DomainValueItemId itemId,
                                       DomainValueLocationId locationId,
                                       DomainValueQuantity quantityOnHand,
                                       DomainValueQuantity reservedQuantity) {
        if (itemId == null) {
            throw new IllegalArgumentException("Item ID cannot be null");
        }
        if (locationId == null) {
            throw new IllegalArgumentException("Location ID cannot be null");
        }
        if (quantityOnHand == null) {
            throw new IllegalArgumentException("Quantity on hand cannot be null");
        }
        if (reservedQuantity == null) {
            throw new IllegalArgumentException("Reserved quantity cannot be null");
        }
    }
    
    private void validateQuantityConstraints(DomainValueQuantity quantityOnHand, DomainValueQuantity reservedQuantity) {
        if (!quantityOnHand.getUnit().equals(reservedQuantity.getUnit())) {
            throw new IllegalArgumentException("Quantity on hand and reserved quantity must have the same unit");
        }
        
        DomainStockAvailabilityRules.validateReservedQuantityConstraint(quantityOnHand, reservedQuantity);
    }
    
    /**
     * Reserve additional quantity
     * @param additionalQuantity quantity to reserve
     * @throws IllegalStateException if insufficient stock is available
     */
    public void reserveQuantity(DomainValueQuantity additionalQuantity) {
        DomainStockAvailabilityRules.validateReservationRequest(this, additionalQuantity);
        this.reservedQuantity = this.reservedQuantity.add(additionalQuantity);
    }
    
    /**
     * Release reserved quantity back to available stock
     * @param quantityToRelease quantity to release
     * @throws IllegalArgumentException if trying to release more than reserved
     */
    public void releaseReservedQuantity(DomainValueQuantity quantityToRelease) {
        if (quantityToRelease.isGreaterThan(this.reservedQuantity)) {
            throw new IllegalArgumentException("Cannot release more than reserved quantity");
        }
        this.reservedQuantity = this.reservedQuantity.subtract(quantityToRelease);
    }

    /**
     * Consume a reserved quantity, reducing both on-hand and reserved stock.
     * @param quantityToConsume The quantity that has been consumed.
     */
    public void consume(DomainValueQuantity quantityToConsume) {
        if (quantityToConsume.isGreaterThan(this.reservedQuantity)) {
            throw new IllegalArgumentException("Cannot consume more than the reserved quantity.");
        }
        if (quantityToConsume.isGreaterThan(this.quantityOnHand)) {
            throw new IllegalArgumentException("Cannot consume more than the quantity on hand.");
        }
        this.quantityOnHand = this.quantityOnHand.subtract(quantityToConsume);
        this.reservedQuantity = this.reservedQuantity.subtract(quantityToConsume);
    }
    
    /**
     * Update the quantity on hand (typically from external stock updates)
     * @param newQuantityOnHand new quantity on hand
     */
    public void updateQuantityOnHand(DomainValueQuantity newQuantityOnHand) {
        if (newQuantityOnHand == null) {
            throw new IllegalArgumentException("New quantity on hand cannot be null");
        }
        
        if (!newQuantityOnHand.getUnit().equals(this.quantityOnHand.getUnit())) {
            throw new IllegalArgumentException("New quantity on hand must have the same unit as current quantity");
        }
        
        // Validate that new quantity on hand is not less than reserved quantity
        DomainStockAvailabilityRules.validateReservedQuantityConstraint(newQuantityOnHand, this.reservedQuantity);
        
        this.quantityOnHand = newQuantityOnHand;
    }
    
    /**
     * Get the available quantity for new reservations
     * @return available quantity (quantity_on_hand - reserved_qty)
     */
    public DomainValueQuantity getAvailableQuantity() {
        return DomainStockAvailabilityRules.calculateAvailableQuantity(this);
    }
    
    /**
     * Check if this stock level has any available quantity
     * @return true if there is available quantity, false otherwise
     */
    public boolean hasAvailableStock() {
        return DomainStockAvailabilityRules.hasAvailableStock(this);
    }
    
    /**
     * Get the unique key for this stock level (item + location)
     * @return unique key string
     */
    public String getStockLevelKey() {
        return itemId.toString() + ":" + locationId.toString();
    }
    
    // Getters
    public DomainValueItemId getItemId() {
        return itemId;
    }
    
    public DomainValueLocationId getLocationId() {
        return locationId;
    }
    
    public DomainValueQuantity getQuantityOnHand() {
        return quantityOnHand;
    }
    
    public DomainValueQuantity getReservedQuantity() {
        return reservedQuantity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainStockLevelEntity that = (DomainStockLevelEntity) o;
        return itemId.equals(that.itemId) && locationId.equals(that.locationId);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(itemId, locationId);
    }
    
    @Override
    public String toString() {
        return "DomainStockLevelEntity{" +
                "itemId=" + itemId +
                ", locationId=" + locationId +
                ", quantityOnHand=" + quantityOnHand +
                ", reservedQuantity=" + reservedQuantity +
                ", availableQuantity=" + getAvailableQuantity() +
                '}';
    }
}