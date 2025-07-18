package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueReservationId;

import java.time.Instant;
import java.util.List;

/**
 * Domain input port for processing expired reservations.
 */
public interface DomainInputPortProcessExpiration {
    
    /**
     * Process all expired reservations.
     * This method finds all active reservations that have expired and processes them.
     * 
     * @return list of expired reservations that were processed
     */
    List<DomainComponentReservationEntity> processExpiredReservations();
    
    /**
     * Process expired reservations up to a specific cutoff time.
     * 
     * @param cutoffTime the cutoff time for expiration
     * @return list of expired reservations that were processed
     */
    List<DomainComponentReservationEntity> processExpiredReservations(Instant cutoffTime);
    
    /**
     * Check if a specific reservation is expired.
     * 
     * @param reservationId the reservation ID to check
     * @return true if the reservation is expired, false otherwise
     * @throws ai.shreds.domain.exceptions.DomainExceptionReservationNotFound if reservation not found
     */
    boolean isReservationExpired(DomainValueReservationId reservationId);
    
    /**
     * Get all reservations that will expire within a specific time period.
     * 
     * @param expirationTime the time to check for upcoming expirations
     * @return list of reservations that will expire by the specified time
     */
    List<DomainComponentReservationEntity> getReservationsExpiringBefore(Instant expirationTime);
}