package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainStockLevelUpdatedDTO;
import ai.shreds.domain.entities.DomainComponentReservationEntity;
import java.util.List;

/**
 * Input port for validating reservations against stock level changes.
 * This port defines the contract for handling stock level updates and validating existing reservations.
 */
public interface DomainInputPortValidateReservationsAgainstStockLevel {
    
    /**
     * Handle stock level update and validate existing reservations
     * @param stockLevelUpdate the stock level update message
     * @return list of reservations that were affected by the stock level change
     */
    List<DomainComponentReservationEntity> handleStockLevelUpdate(DomainStockLevelUpdatedDTO stockLevelUpdate);
    
    /**
     * Validate all active reservations for a specific item and location against current stock level
     * @param stockLevelUpdate the stock level update containing item and location info
     * @return list of reservations that need attention due to stock level changes
     */
    List<DomainComponentReservationEntity> validateReservationsAgainstStockLevel(DomainStockLevelUpdatedDTO stockLevelUpdate);
}