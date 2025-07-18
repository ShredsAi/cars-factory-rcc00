package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import java.util.List;

/**
 * Input port for processing expired reservations.
 * This port defines the contract for expiration processing use cases.
 */
public interface DomainInputPortProcessExpiredReservations {
    
    /**
     * Process all expired reservations
     * @return list of expired reservation entities that were processed
     */
    List<DomainComponentReservationEntity> processExpiredReservations();
}