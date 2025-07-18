package ai.shreds.domain.services;

import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.domain.dtos.DomainBatchReservationResponseDTO;
import ai.shreds.domain.dtos.DomainReservationRequestDTO;
import ai.shreds.domain.dtos.DomainReservationResponseDTO;
import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.events.*;
import ai.shreds.domain.exceptions.*;
import ai.shreds.domain.ports.*;
import ai.shreds.domain.rules.*;
import ai.shreds.domain.value_objects.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class DomainServiceReservationManager implements
        DomainInputPortCreateReservation,
        DomainInputPortCreateBatchReservations,
        DomainInputPortCancelReservation,
        DomainInputPortConsumeReservation,
        DomainInputPortGetReservation,
        DomainInputPortGetReservationsByProductionRun {

    private final DomainOutputPortReservationRepository reservationRepository;
    private final DomainOutputPortStockLevelRepository stockLevelRepository;
    private final DomainOutputPortEventPublisher eventPublisher;

    @Override
    public DomainReservationResponseDTO createReservation(DomainReservationRequestDTO request) {
        DomainReservationValidationRules.validateReservationCreationRequest(
            request.getItemIdValue(),
            request.getLocationIdValue(),
            request.getProductionRunIdValue(),
            request.getQuantityReservedValue(),
            request.getReservedByValue(),
            request.getExpiresAt()
        );

        DomainStockLevelEntity stockLevel = stockLevelRepository
            .lockForUpdate(request.getItemIdValue(), request.getLocationIdValue())
            .orElseThrow(() -> new DomainExceptionInsufficientStock(
                request.getItemIdValue(), request.getLocationIdValue(),
                request.getQuantityReservedValue(), DomainValueQuantity.zero(request.getQuantityReservedValue().getUnit())));

        DomainStockAvailabilityRules.validateReservationRequest(stockLevel, request.getQuantityReservedValue());

        DomainComponentReservationEntity reservation = new DomainComponentReservationEntity(
            DomainValueReservationId.generate(),
            request.getItemIdValue(),
            request.getLocationIdValue(),
            request.getProductionRunIdValue(),
            request.getQuantityReservedValue(),
            request.getReservedByValue(),
            Instant.now(),
            request.getExpiresAt(),
            DomainValueReservationStatus.active()
        );

        stockLevel.reserveQuantity(request.getQuantityReservedValue());

        DomainComponentReservationEntity savedReservation = reservationRepository.save(reservation);
        stockLevelRepository.save(stockLevel);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publish(new DomainReservationCreatedEvent(savedReservation));
            }
        });

        return DomainReservationResponseDTO.fromEntity(savedReservation);
    }

    @Override
    public DomainBatchReservationResponseDTO createBatchReservations(DomainBatchReservationRequestDTO request) {
        DomainReservationValidationRules.validateBatchReservationRequests(request);

        Map<String, DomainStockLevelEntity> stockLevelMap = new HashMap<>();
        List<DomainComponentReservationEntity> reservationsToCreate = new ArrayList<>();

        for (var item : request.getItems()) {
            DomainValueItemId itemId = DomainValueItemId.from(item.getItemId());
            DomainValueLocationId locationId = DomainValueLocationId.from(item.getLocationId());
            DomainValueQuantity quantity = DomainValueQuantity.of(item.getQuantityReserved(), item.getQuantityUnit());
            String stockKey = itemId.getValue() + ":" + locationId.getValue();

            if (!stockLevelMap.containsKey(stockKey)) {
                DomainStockLevelEntity stockLevel = stockLevelRepository
                    .lockForUpdate(itemId, locationId)
                    .orElseThrow(() -> new DomainExceptionInsufficientStock(itemId, locationId, quantity, DomainValueQuantity.zero(quantity.getUnit())));
                stockLevelMap.put(stockKey, stockLevel);
            }
        }

        for (var item : request.getItems()) {
            DomainValueItemId itemId = DomainValueItemId.from(item.getItemId());
            DomainValueLocationId locationId = DomainValueLocationId.from(item.getLocationId());
            DomainValueQuantity quantity = DomainValueQuantity.of(item.getQuantityReserved(), item.getQuantityUnit());
            String stockKey = itemId.getValue() + ":" + locationId.getValue();
            DomainStockLevelEntity stockLevel = stockLevelMap.get(stockKey);

            DomainStockAvailabilityRules.validateReservationRequest(stockLevel, quantity);
            stockLevel.reserveQuantity(quantity);

            DomainComponentReservationEntity reservation = new DomainComponentReservationEntity(
                DomainValueReservationId.generate(),
                itemId,
                locationId,
                DomainValueProductionRunId.from(request.getProductionRunId()),
                quantity,
                DomainValueUserId.from(request.getReservedBy()),
                Instant.now(),
                request.getExpiresAt(),
                DomainValueReservationStatus.active()
            );
            reservationsToCreate.add(reservation);
        }

        List<DomainReservationResponseDTO> createdReservations = new ArrayList<>();
        List<DomainComponentReservationEntity> savedReservations = new ArrayList<>();
        for (DomainComponentReservationEntity reservation : reservationsToCreate) {
            DomainComponentReservationEntity savedReservation = reservationRepository.save(reservation);
            savedReservations.add(savedReservation);
            createdReservations.add(DomainReservationResponseDTO.fromEntity(savedReservation));
        }

        for (DomainStockLevelEntity stockLevel : stockLevelMap.values()) {
            stockLevelRepository.save(stockLevel);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                savedReservations.forEach(r -> eventPublisher.publish(new DomainReservationCreatedEvent(r)));
            }
        });

        return new DomainBatchReservationResponseDTO(
            request.getProductionRunId(),
            createdReservations,
            true
        );
    }

    @Override
    public DomainComponentReservationEntity cancelReservation(DomainValueReservationId reservationId) {
        DomainComponentReservationEntity reservation = findReservationById(reservationId);
        
        try {
            reservation.cancel();
        } catch (DomainExceptionInvalidReservationState e) {
            throw e; // Re-throw domain exception
        }
        
        releaseStockForReservation(reservation);

        DomainComponentReservationEntity savedReservation = reservationRepository.save(reservation);
        
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publish(new DomainReservationCancelledEvent(savedReservation));
            }
        });

        return savedReservation;
    }

    @Override
    public DomainComponentReservationEntity consumeReservation(DomainValueReservationId reservationId) {
        DomainComponentReservationEntity reservation = findReservationById(reservationId);
        
        try {
            reservation.consume();
        } catch (DomainExceptionInvalidReservationState e) {
            throw e; // Re-throw domain exception
        }
        
        consumeStockForReservation(reservation);

        DomainComponentReservationEntity savedReservation = reservationRepository.save(reservation);
        
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                eventPublisher.publish(new DomainReservationConsumedEvent(savedReservation));
            }
        });

        return savedReservation;
    }

    @Override
    public Optional<DomainComponentReservationEntity> getReservationById(DomainValueReservationId reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Override
    public List<DomainComponentReservationEntity> getReservationsByProductionRun(DomainValueProductionRunId productionRunId) {
        return reservationRepository.findByProductionRunId(productionRunId);
    }

    private DomainComponentReservationEntity findReservationById(DomainValueReservationId reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new DomainExceptionReservationNotFound(reservationId));
    }

    private void releaseStockForReservation(DomainComponentReservationEntity reservation) {
        stockLevelRepository
            .lockForUpdate(reservation.getItemId(), reservation.getLocationId())
            .ifPresent(stockLevel -> {
                stockLevel.releaseReservedQuantity(reservation.getQuantityReserved());
                stockLevelRepository.save(stockLevel);
            });
    }

    private void consumeStockForReservation(DomainComponentReservationEntity reservation) {
        stockLevelRepository
            .lockForUpdate(reservation.getItemId(), reservation.getLocationId())
            .ifPresent(stockLevel -> {
                stockLevel.consume(reservation.getQuantityReserved());
                stockLevelRepository.save(stockLevel);
            });
    }
}