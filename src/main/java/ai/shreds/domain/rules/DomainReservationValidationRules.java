package ai.shreds.domain.rules;

import ai.shreds.domain.dtos.DomainBatchReservationRequestDTO;
import ai.shreds.domain.dtos.DomainReservationItemDTO;
import ai.shreds.domain.value_objects.*;
import ai.shreds.domain.entities.DomainComponentReservationEntity;

import java.time.Instant;
import java.util.List;

/**
 * Domain rules for reservation validation.
 * This class contains the business rules for validating reservation operations.
 */
public class DomainReservationValidationRules {
    
    /**
     * Validate reservation creation request
     * @param itemId the item to reserve
     * @param locationId the location to reserve from
     * @param productionRunId the production run
     * @param quantityReserved the quantity to reserve
     * @param reservedBy the user creating the reservation
     * @param expiresAt when the reservation expires
     * @throws IllegalArgumentException if any validation fails
     */
    public static void validateReservationCreationRequest(
            DomainValueItemId itemId,
            DomainValueLocationId locationId,
            DomainValueProductionRunId productionRunId,
            DomainValueQuantity quantityReserved,
            DomainValueUserId reservedBy,
            Instant expiresAt) {
        
        validateRequiredFields(itemId, locationId, productionRunId, quantityReserved, reservedBy, expiresAt);
        validateQuantityPositive(quantityReserved);
        validateExpirationDate(expiresAt);
    }
    
    /**
     * Validate that all required fields are present
     */
    private static void validateRequiredFields(
            DomainValueItemId itemId,
            DomainValueLocationId locationId,
            DomainValueProductionRunId productionRunId,
            DomainValueQuantity quantityReserved,
            DomainValueUserId reservedBy,
            Instant expiresAt) {
        
        if (itemId == null || itemId.getValue() == null) {
            throw new IllegalArgumentException("Item ID is required");
        }
        if (locationId == null || locationId.getValue() == null) {
            throw new IllegalArgumentException("Location ID is required");
        }
        if (productionRunId == null || productionRunId.getValue() == null) {
            throw new IllegalArgumentException("Production run ID is required");
        }
        if (quantityReserved == null) {
            throw new IllegalArgumentException("Quantity reserved is required");
        }
        if (reservedBy == null || reservedBy.getValue() == null) {
            throw new IllegalArgumentException("Reserved by user ID is required");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date is required");
        }
    }
    
    /**
     * Validate that quantity is positive
     * @param quantity the quantity to validate
     * @throws IllegalArgumentException if quantity is not positive
     */
    public static void validateQuantityPositive(DomainValueQuantity quantity) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
    
    /**
     * Validate that expiration date is in the future
     * @param expiresAt the expiration date to validate
     * @throws IllegalArgumentException if expiration date is not in the future
     */
    public static void validateExpirationDate(Instant expiresAt) {
        if (expiresAt == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
        
        if (expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }
    }
    
    /**
     * Validate that a reservation can be cancelled
     * @param reservation the reservation to validate
     * @throws IllegalStateException if reservation cannot be cancelled
     */
    public static void validateReservationCanBeCancelled(DomainComponentReservationEntity reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        if (!reservation.canBeModified()) {
            throw new IllegalStateException("Reservation cannot be cancelled in its current state: " + reservation.getStatus());
        }
    }
    
    /**
     * Validate that a reservation can be consumed
     * @param reservation the reservation to validate
     * @throws IllegalStateException if reservation cannot be consumed
     */
    public static void validateReservationCanBeConsumed(DomainComponentReservationEntity reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        if (!reservation.canBeModified()) {
            throw new IllegalStateException("Reservation cannot be consumed in its current state: " + reservation.getStatus());
        }
        
        if (reservation.isExpired()) {
            throw new IllegalStateException("Cannot consume an expired reservation");
        }
    }
    
    /**
     * Validate that a reservation can be expired
     * @param reservation the reservation to validate
     * @throws IllegalStateException if reservation cannot be expired
     */
    public static void validateReservationCanBeExpired(DomainComponentReservationEntity reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        
        if (!reservation.getStatus().isActive()) {
            throw new IllegalStateException("Only active reservations can be expired");
        }
        
        if (!reservation.isExpired()) {
            throw new IllegalStateException("Reservation has not yet expired");
        }
    }
    
    /**
     * Validate batch reservation requests
     * @param request the batch reservation request DTO
     * @throws IllegalArgumentException if any validation fails
     */
    public static void validateBatchReservationRequests(DomainBatchReservationRequestDTO request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Batch reservation requests cannot be null or empty");
        }

        validateBatchConsistency(request);

        for (DomainReservationItemDTO item : request.getItems()) {
            validateReservationCreationRequest(
                DomainValueItemId.from(item.getItemId()),
                DomainValueLocationId.from(item.getLocationId()),
                request.getProductionRunIdValue(),
                DomainValueQuantity.of(item.getQuantityReserved(), item.getQuantityUnit()),
                request.getReservedByValue(),
                request.getExpiresAt()
            );
        }
    }
    
    /**
     * Validate consistency of a batch request DTO.
     * @param request the batch reservation request DTO
     * @throws IllegalArgumentException if batch request is inconsistent
     */
    public static void validateBatchConsistency(DomainBatchReservationRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getProductionRunId() == null) {
            throw new IllegalArgumentException("Production Run ID is required for a batch request.");
        }
        if (request.getReservedBy() == null) {
            throw new IllegalArgumentException("Reserved By user is required for a batch request.");
        }
        if (request.getExpiresAt() == null) {
            throw new IllegalArgumentException("Expiration date is required for a batch request.");
        }
    }
}