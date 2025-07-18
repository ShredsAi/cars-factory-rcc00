package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainStockLevelUpdatedDTO;

/**
 * Input port for handling stock level updates.
 * This port defines the contract for processing stock level changes.
 */
public interface DomainInputPortStockLevelUpdateHandler {
    
    /**
     * Handle an update to stock levels
     * @param message the stock level update details
     */
    void handleStockLevelUpdated(DomainStockLevelUpdatedDTO message);
}