package ai.shreds.application.services;

import ai.shreds.application.dtos.ApplicationBatchReservationRequestDTO;
import ai.shreds.application.dtos.ApplicationBatchReservationResponseDTO;
import ai.shreds.application.dtos.ApplicationProductionRunIdDTO;
import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationReservationRequestDTO;
import ai.shreds.application.dtos.ApplicationReservationResponseDTO;
import ai.shreds.application.exceptions.ApplicationConflictException;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.ports.ApplicationCancelReservationInputPort;
import ai.shreds.application.ports.ApplicationConsumeReservationInputPort;
import ai.shreds.application.ports.ApplicationCreateBatchReservationsInputPort;
import ai.shreds.application.ports.ApplicationCreateReservationInputPort;
import ai.shreds.application.ports.ApplicationGetReservationInputPort;
import ai.shreds.application.ports.ApplicationGetReservationsByProductionRunInputPort;
import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.domain.dtos.DomainBatchReservationResponseDTO;
import ai.shreds.domain.dtos.DomainReservationRequestDTO;
import ai.shreds.domain.dtos.DomainReservationResponseDTO;
import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.exceptions.DomainExceptionInsufficientStock;
import ai.shreds.domain.exceptions.DomainExceptionReservationNotFound;
import ai.shreds.domain.exceptions.DomainExceptionInvalidReservationState;
import ai.shreds.domain.ports.DomainInputPortCancelReservation;
import ai.shreds.domain.ports.DomainInputPortConsumeReservation;
import ai.shreds.domain.ports.DomainInputPortCreateBatchReservations;
import ai.shreds.domain.ports.DomainInputPortCreateReservation;
import ai.shreds.domain.ports.DomainInputPortGetReservation;
import ai.shreds.domain.ports.DomainInputPortGetReservationsByProductionRun;
import ai.shreds.domain.value_objects.DomainValueProductionRunId;
import ai.shreds.domain.value_objects.DomainValueReservationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service implementing all reservation-related use cases.
 * This service orchestrates calls to domain services and handles application-level concerns.
 */
@Service
@RequiredArgsConstructor
public class ApplicationReservationService implements
        ApplicationCreateReservationInputPort,
        ApplicationCreateBatchReservationsInputPort,
        ApplicationGetReservationInputPort,
        ApplicationGetReservationsByProductionRunInputPort,
        ApplicationCancelReservationInputPort,
        ApplicationConsumeReservationInputPort {

    private final DomainInputPortCreateReservation domainCreateReservationPort;
    private final DomainInputPortCreateBatchReservations domainCreateBatchReservationsPort;
    private final DomainInputPortGetReservation domainGetReservationPort;
    private final DomainInputPortGetReservationsByProductionRun domainGetReservationsByProductionRunPort;
    private final DomainInputPortCancelReservation domainCancelReservationPort;
    private final DomainInputPortConsumeReservation domainConsumeReservationPort;
    private final ai.shreds.application.ports.ApplicationReservationEventOutputPort eventOutputPort;

    @Override
    @Transactional
    public ApplicationReservationResponseDTO createReservation(ApplicationReservationRequestDTO request) {
        try {
            DomainReservationRequestDTO domainRequest = request.toDomainDTO();
            DomainReservationResponseDTO domainResponse = domainCreateReservationPort.createReservation(domainRequest);
            return ApplicationReservationResponseDTO.fromDomainDTO(domainResponse);
        } catch (DomainExceptionInsufficientStock e) {
            throw new ApplicationConflictException("Insufficient stock available. Requested: " + e.getRequestedQuantity() + ", Available: " + e.getAvailableQuantity());
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Reservation not found: " + e.getMessage());
        } catch (DomainExceptionInvalidReservationState e) {
            throw new ApplicationConflictException("Invalid reservation state: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApplicationBatchReservationResponseDTO createBatchReservations(ApplicationBatchReservationRequestDTO request) {
        try {
            DomainBatchReservationRequestDTO domainRequest = request.toDomainDTO();
            DomainBatchReservationResponseDTO domainResponse = domainCreateBatchReservationsPort.createBatchReservations(domainRequest);
            return ApplicationBatchReservationResponseDTO.fromDomainDTO(domainResponse);
        } catch (DomainExceptionInsufficientStock e) {
            throw new ApplicationConflictException("Insufficient stock available. Requested: " + e.getRequestedQuantity() + ", Available: " + e.getAvailableQuantity());
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Reservation not found: " + e.getMessage());
        } catch (DomainExceptionInvalidReservationState e) {
            throw new ApplicationConflictException("Invalid reservation state: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApplicationReservationResponseDTO cancelReservation(ApplicationReservationIdDTO reservationId) {
        try {
            DomainValueReservationId id = DomainValueReservationId.from(reservationId.getReservationId());
            DomainComponentReservationEntity entity = domainCancelReservationPort.cancelReservation(id);
            return ApplicationReservationResponseDTO.fromDomainDTO(DomainReservationResponseDTO.fromEntity(entity));
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Reservation not found: " + e.getMessage());
        } catch (DomainExceptionInvalidReservationState e) {
            throw new ApplicationConflictException("Invalid reservation state: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApplicationReservationResponseDTO consumeReservation(ApplicationReservationIdDTO reservationId) {
        try {
            DomainValueReservationId id = DomainValueReservationId.from(reservationId.getReservationId());
            DomainComponentReservationEntity entity = domainConsumeReservationPort.consumeReservation(id);
            return ApplicationReservationResponseDTO.fromDomainDTO(DomainReservationResponseDTO.fromEntity(entity));
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Reservation not found: " + e.getMessage());
        } catch (DomainExceptionInvalidReservationState e) {
            throw new ApplicationConflictException("Invalid reservation state: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationReservationResponseDTO getReservation(ApplicationReservationIdDTO reservationId) {
        try {
            DomainValueReservationId id = DomainValueReservationId.from(reservationId.getReservationId());
            DomainComponentReservationEntity entity = domainGetReservationPort.getReservationById(id)
                    .orElseThrow(() -> new ApplicationResourceNotFoundException("Reservation not found: " + id.getValue()));
            return ApplicationReservationResponseDTO.fromDomainDTO(DomainReservationResponseDTO.fromEntity(entity));
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Reservation not found: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationReservationResponseDTO> getReservationsByProductionRun(ApplicationProductionRunIdDTO productionRunId) {
        try {
            DomainValueProductionRunId id = DomainValueProductionRunId.from(productionRunId.getProductionRunId());
            return domainGetReservationsByProductionRunPort.getReservationsByProductionRun(id).stream()
                    .map(e -> ApplicationReservationResponseDTO.fromDomainDTO(DomainReservationResponseDTO.fromEntity(e)))
                    .collect(Collectors.toList());
        } catch (DomainExceptionReservationNotFound e) {
            throw new ApplicationResourceNotFoundException("Production run not found: " + e.getMessage());
        }
    }

    // Note: Validation logic removed as domain layer handles validation.
}
