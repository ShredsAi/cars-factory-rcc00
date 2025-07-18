package ai.shreds.domain.services;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.events.DomainReservationExpiredEvent;
import ai.shreds.domain.ports.DomainInputPortProcessExpiredReservations;
import ai.shreds.domain.ports.DomainOutputPortReservationRepository;
import ai.shreds.domain.ports.DomainOutputPortStockLevelRepository;
import ai.shreds.domain.ports.DomainOutputPortEventPublisher;
import ai.shreds.domain.value_objects.DomainValueReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

/**
 * Domain service for processing expired reservations.
 * This service handles the core business logic for expiration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DomainReservationExpirationService implements DomainInputPortProcessExpiredReservations {

    private final DomainOutputPortReservationRepository reservationRepository;
    private final DomainOutputPortStockLevelRepository stockLevelRepository;
    private final DomainOutputPortEventPublisher eventPublisher;

    @Override
    @Transactional
    public List<DomainComponentReservationEntity> processExpiredReservations() {
        log.debug("Starting to process expired reservations at {}", Instant.now());
        
        List<DomainComponentReservationEntity> expiredReservations = reservationRepository.findExpiredActiveReservations(Instant.now());
        List<DomainComponentReservationEntity> processedReservations = new ArrayList<>();
        
        for (DomainComponentReservationEntity reservation : expiredReservations) {
            try {
                expireReservation(reservation);
                processedReservations.add(reservation);
                log.debug("Expired reservation: {}", reservation.getReservationId().getValue());
            } catch (Exception e) {
                log.error("Failed to expire reservation: {}", reservation.getReservationId().getValue(), e);
                // Continue processing other reservations even if one fails
            }
        }
        
        // Register synchronization to publish events after transaction commits
        if (!processedReservations.isEmpty()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    processedReservations.forEach(reservation -> {
                        try {
                            eventPublisher.publish(new DomainReservationExpiredEvent(reservation));
                        } catch (Exception e) {
                            log.error("Failed to publish expired event for reservation: {}", 
                                reservation.getReservationId().getValue(), e);
                        }
                    });
                }
            });
        }
        
        log.info("Processed {} expired reservations", processedReservations.size());
        return processedReservations;
    }

    private void expireReservation(DomainComponentReservationEntity reservation) {
        // Mark reservation as expired
        reservation.expire();
        
        // Release the reserved stock
        releaseStockForReservation(reservation);
        
        // Save the expired reservation
        reservationRepository.save(reservation);
    }

    private void releaseStockForReservation(DomainComponentReservationEntity reservation) {
        stockLevelRepository
            .lockForUpdate(reservation.getItemId(), reservation.getLocationId())
            .ifPresent(stockLevel -> {
                stockLevel.releaseReservedQuantity(reservation.getQuantityReserved());
                stockLevelRepository.save(stockLevel);
                log.debug("Released {} {} of stock for item {} at location {}", 
                    reservation.getQuantityReserved().getValue(),
                    reservation.getQuantityReserved().getUnit(),
                    reservation.getItemId().getValue(),
                    reservation.getLocationId().getValue());
            });
    }
}