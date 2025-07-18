package ai.shreds.domain.dtos;

import ai.shreds.application.dtos.ApplicationBatchReservationRequestDTO;
import ai.shreds.domain.value_objects.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Domain DTO representing a batch reservation request.
 */
public class DomainBatchReservationRequestDTO {
    
    private final UUID productionRunId;
    private final UUID reservedBy;
    private final Instant expiresAt;
    private final List<DomainReservationItemDTO> items;
    
    public DomainBatchReservationRequestDTO(
            UUID productionRunId,
            UUID reservedBy,
            Instant expiresAt,
            List<DomainReservationItemDTO> items) {
        this.productionRunId = productionRunId;
        this.reservedBy = reservedBy;
        this.expiresAt = expiresAt;
        this.items = items;
    }
    
    /**
     * Convert to domain value objects
     */
    public DomainValueProductionRunId getProductionRunIdValue() {
        return DomainValueProductionRunId.from(productionRunId);
    }
    
    public DomainValueUserId getReservedByValue() {
        return DomainValueUserId.from(reservedBy);
    }
    
    /**
     * Create from application DTO
     */
    public static DomainBatchReservationRequestDTO fromApplicationDTO(ApplicationBatchReservationRequestDTO appDto) {
        if (appDto == null) {
            return null;
        }

        List<DomainReservationItemDTO> domainItems = appDto.getItems().stream()
                .map(DomainReservationItemDTO::fromApplicationDTO)
                .collect(Collectors.toList());

        return new DomainBatchReservationRequestDTO(
                appDto.getProductionRunId(),
                appDto.getReservedBy(),
                appDto.getExpiresAt().toInstant(ZoneOffset.UTC),
                domainItems
        );
    }
    
    // Getters
    public UUID getProductionRunId() {
        return productionRunId;
    }
    
    public UUID getReservedBy() {
        return reservedBy;
    }
    
    public Instant getExpiresAt() {
        return expiresAt;
    }
    
    public List<DomainReservationItemDTO> getItems() {
        return items;
    }
    
    @Override
    public String toString() {
        return "DomainBatchReservationRequestDTO{" +
                "productionRunId=" + productionRunId +
                ", reservedBy=" + reservedBy +
                ", expiresAt=" + expiresAt +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}