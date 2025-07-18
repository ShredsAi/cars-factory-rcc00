package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.domain.dtos.DomainBatchReservationResponseDTO;
import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueProductionRunId;

import java.util.List;

/**
 * Domain input port for batch reservation operations.
 */
public interface DomainInputPortBatchReservation {
    
    /**
     * Create multiple reservations in a single batch.
     * 
     * @param request the batch reservation request
     * @return the batch reservation response
     * @throws ai.shreds.domain.exceptions.DomainExceptionInsufficientStock if insufficient stock for any item
     * @throws IllegalArgumentException if request validation fails
     */
    DomainBatchReservationResponseDTO createBatchReservations(DomainBatchReservationRequestDTO request);
    
    /**
     * Get all reservations for a production run.
     * 
     * @param productionRunId the production run ID
     * @return list of reservations for the production run
     */
    List<DomainComponentReservationEntity> getReservationsByProductionRun(DomainValueProductionRunId productionRunId);
    
    /**
     * Validate batch reservation request against available stock levels.
     * 
     * @param request the batch reservation request
     * @return true if all items have sufficient stock, false otherwise
     */
    boolean validateBatchReservationAgainstStock(DomainBatchReservationRequestDTO request);
}