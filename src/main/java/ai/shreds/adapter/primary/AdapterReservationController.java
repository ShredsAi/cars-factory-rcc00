package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationCreateReservationInputPort;
import ai.shreds.application.ports.ApplicationCreateBatchReservationsInputPort;
import ai.shreds.application.ports.ApplicationGetReservationInputPort;
import ai.shreds.application.ports.ApplicationGetReservationsByProductionRunInputPort;
import ai.shreds.application.ports.ApplicationCancelReservationInputPort;
import ai.shreds.application.ports.ApplicationConsumeReservationInputPort;
import ai.shreds.shared.dtos.SharedReservationRequestDTO;
import ai.shreds.shared.dtos.SharedBatchReservationRequestDTO;
import ai.shreds.shared.dtos.SharedReservationResponseDTO;
import ai.shreds.shared.dtos.SharedBatchReservationResponseDTO;
import ai.shreds.application.dtos.ApplicationReservationIdDTO;
import ai.shreds.application.dtos.ApplicationProductionRunIdDTO;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.exceptions.ApplicationResourceNotFoundException;
import ai.shreds.application.exceptions.ApplicationConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class AdapterReservationController {

    private final ApplicationCreateReservationInputPort createReservationInputPort;
    private final ApplicationCreateBatchReservationsInputPort createBatchReservationsInputPort;
    private final ApplicationGetReservationInputPort getReservationInputPort;
    private final ApplicationGetReservationsByProductionRunInputPort getReservationsByProductionRunInputPort;
    private final ApplicationCancelReservationInputPort cancelReservationInputPort;
    private final ApplicationConsumeReservationInputPort consumeReservationInputPort;

    @PostMapping
    public ResponseEntity<SharedReservationResponseDTO> createReservation(
            @Valid @RequestBody SharedReservationRequestDTO request) {
        try {
            log.info("Creating reservation for item: {}, location: {}, quantity: {}", 
                    request.getItemId(), request.getLocationId(), request.getQuantityReserved());
            
            var applicationRequest = request.toApplicationDTO();
            var applicationResponse = createReservationInputPort.createReservation(applicationRequest);
            var sharedResponse = applicationResponse.toSharedDTO();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(sharedResponse);
        } catch (ApplicationValidationException e) {
            log.error("Validation error creating reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (ApplicationConflictException | IllegalStateException e) {
            log.error("Conflict error creating reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error creating reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<SharedBatchReservationResponseDTO> createBatchReservations(
            @Valid @RequestBody SharedBatchReservationRequestDTO request) {
        try {
            log.info("Creating batch reservations for production run: {}, items count: {}", 
                    request.getProductionRunId(), request.getItems().size());
            
            var applicationRequest = request.toApplicationDTO();
            var applicationResponse = createBatchReservationsInputPort.createBatchReservations(applicationRequest);
            var sharedResponse = applicationResponse.toSharedDTO();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(sharedResponse);
        } catch (ApplicationValidationException e) {
            log.error("Validation error creating batch reservations: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (ApplicationConflictException | IllegalStateException e) {
            log.error("Conflict error creating batch reservations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error creating batch reservations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<SharedReservationResponseDTO> getReservation(@PathVariable UUID reservationId) {
        try {
            log.info("Getting reservation: {}", reservationId);
            
            var applicationRequest = new ApplicationReservationIdDTO(reservationId);
            var applicationResponse = getReservationInputPort.getReservation(applicationRequest);
            var sharedResponse = applicationResponse.toSharedDTO();
            
            return ResponseEntity.ok(sharedResponse);
        } catch (ApplicationResourceNotFoundException e) {
            log.error("Reservation not found: {}", reservationId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/production-run/{productionRunId}")
    public ResponseEntity<List<SharedReservationResponseDTO>> getReservationsByProductionRun(
            @PathVariable UUID productionRunId) {
        try {
            log.info("Getting reservations for production run: {}", productionRunId);
            
            var applicationRequest = new ApplicationProductionRunIdDTO(productionRunId);
            var applicationResponses = getReservationsByProductionRunInputPort.getReservationsByProductionRun(applicationRequest);
            var sharedResponses = applicationResponses.stream()
                    .map(response -> response.toSharedDTO())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(sharedResponses);
        } catch (ApplicationResourceNotFoundException e) {
            log.error("Production run not found: {}", productionRunId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Unexpected error getting reservations by production run: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<SharedReservationResponseDTO> cancelReservation(@PathVariable UUID reservationId) {
        try {
            log.info("Cancelling reservation: {}", reservationId);
            
            var applicationRequest = new ApplicationReservationIdDTO(reservationId);
            var applicationResponse = cancelReservationInputPort.cancelReservation(applicationRequest);
            var sharedResponse = applicationResponse.toSharedDTO();
            
            return ResponseEntity.ok(sharedResponse);
        } catch (ApplicationResourceNotFoundException e) {
            log.error("Reservation not found: {}", reservationId);
            return ResponseEntity.notFound().build();
        } catch (ApplicationConflictException e) {
            log.error("Conflict error cancelling reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error cancelling reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{reservationId}/consume")
    public ResponseEntity<SharedReservationResponseDTO> consumeReservation(@PathVariable UUID reservationId) {
        try {
            log.info("Consuming reservation: {}", reservationId);
            
            var applicationRequest = new ApplicationReservationIdDTO(reservationId);
            var applicationResponse = consumeReservationInputPort.consumeReservation(applicationRequest);
            var sharedResponse = applicationResponse.toSharedDTO();
            
            return ResponseEntity.ok(sharedResponse);
        } catch (ApplicationResourceNotFoundException e) {
            log.error("Reservation not found: {}", reservationId);
            return ResponseEntity.notFound().build();
        } catch (ApplicationConflictException e) {
            log.error("Conflict error consuming reservation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Unexpected error consuming reservation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}