package ai.shreds.domain.exceptions;

import ai.shreds.domain.value_objects.DomainValueItemId;
import ai.shreds.domain.value_objects.DomainValueLocationId;
import ai.shreds.domain.value_objects.DomainValueQuantity;

/**
 * Exception thrown when there is insufficient stock for a reservation.
 */
public class DomainExceptionInsufficientStock extends RuntimeException {
    
    private final DomainValueItemId itemId;
    private final DomainValueLocationId locationId;
    private final DomainValueQuantity requestedQuantity;
    private final DomainValueQuantity availableQuantity;
    
    public DomainExceptionInsufficientStock(DomainValueItemId itemId, 
                                           DomainValueLocationId locationId,
                                           DomainValueQuantity requestedQuantity, 
                                           DomainValueQuantity availableQuantity) {
        super(String.format("Insufficient stock for item %s at location %s. Requested: %s, Available: %s",
                itemId, locationId, requestedQuantity, availableQuantity));
        this.itemId = itemId;
        this.locationId = locationId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public DomainValueItemId getItemId() {
        return itemId;
    }
    
    public DomainValueLocationId getLocationId() {
        return locationId;
    }
    
    public DomainValueQuantity getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public DomainValueQuantity getAvailableQuantity() {
        return availableQuantity;
    }
}