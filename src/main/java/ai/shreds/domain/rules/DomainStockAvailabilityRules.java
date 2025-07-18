package ai.shreds.domain.rules;

import ai.shreds.domain.value_objects.*;
import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.exceptions.DomainExceptionInsufficientStock;

/**
 * Domain rules for stock availability validation.
 * This class contains the business rules for determining stock availability.
 */
public class DomainStockAvailabilityRules {
    
    /**
     * Check if sufficient stock is available for a reservation
     * @param stockLevel the current stock level
     * @param requestedQuantity the quantity being requested
     * @return true if sufficient stock is available, false otherwise
     */
    public static boolean isStockAvailable(DomainStockLevelEntity stockLevel, DomainValueQuantity requestedQuantity) {
        if (stockLevel == null) {
            return false;
        }
        
        DomainValueQuantity availableQuantity = calculateAvailableQuantity(stockLevel);
        return availableQuantity.isGreaterThanOrEqual(requestedQuantity);
    }
    
    /**
     * Calculate the available quantity from a stock level
     * @param stockLevel the stock level entity
     * @return the available quantity (quantity_on_hand - reserved_qty)
     */
    public static DomainValueQuantity calculateAvailableQuantity(DomainStockLevelEntity stockLevel) {
        if (stockLevel == null) {
            throw new IllegalArgumentException("Stock level cannot be null");
        }
        
        return stockLevel.getQuantityOnHand().subtract(stockLevel.getReservedQuantity());
    }
    
    /**
     * Validate that a reservation can be made without exceeding available stock
     * @param stockLevel the current stock level
     * @param requestedQuantity the quantity being requested
     * @throws DomainExceptionInsufficientStock if insufficient stock is available
     */
    public static void validateReservationRequest(DomainStockLevelEntity stockLevel, DomainValueQuantity requestedQuantity) {
        if (!isStockAvailable(stockLevel, requestedQuantity)) {
            DomainValueQuantity availableQuantity = calculateAvailableQuantity(stockLevel);
            throw new DomainExceptionInsufficientStock(
                stockLevel.getItemId(),
                stockLevel.getLocationId(),
                requestedQuantity,
                availableQuantity
            );
        }
    }
    
    /**
     * Check if a stock level allows for a specific quantity to be reserved
     * @param currentOnHand current quantity on hand
     * @param currentReserved current reserved quantity
     * @param additionalReservation additional quantity to reserve
     * @return true if the additional reservation can be made
     */
    public static boolean canReserveAdditionalQuantity(DomainValueQuantity currentOnHand, 
                                                      DomainValueQuantity currentReserved, 
                                                      DomainValueQuantity additionalReservation) {
        DomainValueQuantity availableQuantity = currentOnHand.subtract(currentReserved);
        return availableQuantity.isGreaterThanOrEqual(additionalReservation);
    }
    
    /**
     * Validate that reserved quantity never exceeds quantity on hand
     * @param quantityOnHand the total quantity on hand
     * @param reservedQuantity the reserved quantity
     * @throws IllegalStateException if reserved quantity exceeds quantity on hand
     */
    public static void validateReservedQuantityConstraint(DomainValueQuantity quantityOnHand, 
                                                         DomainValueQuantity reservedQuantity) {
        if (reservedQuantity.isGreaterThan(quantityOnHand)) {
            throw new IllegalStateException(
                String.format("Reserved quantity %s cannot exceed quantity on hand %s",
                    reservedQuantity, quantityOnHand)
            );
        }
    }
    
    /**
     * Check if stock level has any available quantity
     * @param stockLevel the stock level to check
     * @return true if there is available quantity, false otherwise
     */
    public static boolean hasAvailableStock(DomainStockLevelEntity stockLevel) {
        if (stockLevel == null) {
            return false;
        }
        
        DomainValueQuantity availableQuantity = calculateAvailableQuantity(stockLevel);
        return availableQuantity.isPositive();
    }
}