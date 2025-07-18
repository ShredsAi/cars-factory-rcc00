package ai.shreds.domain.dtos;

import ai.shreds.domain.value_objects.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Domain DTO representing a stock level update message.
 */
public class DomainStockLevelUpdatedDTO {
    
    private final UUID itemId;
    private final UUID locationId;
    private final BigDecimal newQuantityOnHand;
    private final String quantityUnit;
    
    public DomainStockLevelUpdatedDTO(
            UUID itemId,
            UUID locationId,
            BigDecimal newQuantityOnHand,
            String quantityUnit) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.newQuantityOnHand = newQuantityOnHand;
        this.quantityUnit = quantityUnit;
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
    
    public DomainValueQuantity getNewQuantityOnHandValue() {
        return DomainValueQuantity.of(newQuantityOnHand, quantityUnit);
    }
    
    // Getters
    public UUID getItemId() {
        return itemId;
    }
    
    public UUID getLocationId() {
        return locationId;
    }
    
    public BigDecimal getNewQuantityOnHand() {
        return newQuantityOnHand;
    }
    
    public String getQuantityUnit() {
        return quantityUnit;
    }
    
    @Override
    public String toString() {
        return "DomainStockLevelUpdatedDTO{" +
                "itemId=" + itemId +
                ", locationId=" + locationId +
                ", newQuantityOnHand=" + newQuantityOnHand +
                ", quantityUnit='" + quantityUnit + '\'' +
                '}';
    }
}