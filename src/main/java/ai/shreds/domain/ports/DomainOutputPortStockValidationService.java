package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.value_objects.DomainValueItemId;
import ai.shreds.domain.value_objects.DomainValueLocationId;
import ai.shreds.domain.value_objects.DomainValueQuantity;

/**
 * Domain output port for stock validation service operations.
 * This port defines the contract for validating stock availability and constraints.
 */
public interface DomainOutputPortStockValidationService {
    
    /**
     * Validate if sufficient stock is available for a reservation.
     * 
     * @param itemId the item ID
     * @param locationId the location ID
     * @param requestedQuantity the requested quantity
     * @return true if sufficient stock is available, false otherwise
     */
    boolean validateStockAvailability(DomainValueItemId itemId, 
                                     DomainValueLocationId locationId, 
                                     DomainValueQuantity requestedQuantity);
    
    /**
     * Get current stock level for an item at a location.
     * 
     * @param itemId the item ID
     * @param locationId the location ID
     * @return the current stock level entity
     */
    DomainStockLevelEntity getCurrentStockLevel(DomainValueItemId itemId, 
                                               DomainValueLocationId locationId);
    
    /**
     * Validate that reserved quantity constraints are met.
     * 
     * @param stockLevel the stock level entity
     * @param additionalReservation the additional quantity to reserve
     * @return true if constraints are met, false otherwise
     */
    boolean validateReservedQuantityConstraints(DomainStockLevelEntity stockLevel, 
                                              DomainValueQuantity additionalReservation);
    
    /**
     * Calculate available quantity for reservation.
     * 
     * @param stockLevel the stock level entity
     * @return the available quantity for new reservations
     */
    DomainValueQuantity calculateAvailableQuantity(DomainStockLevelEntity stockLevel);
}