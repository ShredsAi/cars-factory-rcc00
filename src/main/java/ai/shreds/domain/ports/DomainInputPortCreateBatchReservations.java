package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.domain.dtos.DomainBatchReservationResponseDTO;

/**
 * Input port for creating batch reservations.
 * This port defines the contract for batch reservation creation use cases.
 */
public interface DomainInputPortCreateBatchReservations {
    
    /**
     * Create multiple reservations in a single transaction
     * @param request batch reservation request DTO
     * @return domain batch reservation response DTO
     */
    DomainBatchReservationResponseDTO createBatchReservations(
            DomainBatchReservationRequestDTO request);
}