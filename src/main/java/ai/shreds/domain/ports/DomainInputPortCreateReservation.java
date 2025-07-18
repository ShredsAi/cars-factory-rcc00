package ai.shreds.domain.ports;

import ai.shreds.domain.dtos.DomainReservationRequestDTO;
import ai.shreds.domain.dtos.DomainReservationResponseDTO;

/**
 * Input port for creating a new reservation.
 * This port defines the contract for reservation creation use cases.
 */
public interface DomainInputPortCreateReservation {
    
    /**
     * Create a new reservation
     * @param request the reservation request details
     * @return the created reservation response
     */
    DomainReservationResponseDTO createReservation(DomainReservationRequestDTO request);
}