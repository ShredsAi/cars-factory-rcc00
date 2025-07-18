package ai.shreds.domain.dtos;

import ai.shreds.domain.entities.DomainComponentReservationEntity;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Domain DTO representing a batch reservation response.
 */
public class DomainBatchReservationResponseDTO {
    
    private final UUID productionRunId;
    private final List<DomainReservationResponseDTO> createdReservations;
    private final int totalReservations;
    private final boolean success;
    
    public DomainBatchReservationResponseDTO(
            UUID productionRunId,
            List<DomainReservationResponseDTO> createdReservations,
            boolean success) {
        this.productionRunId = productionRunId;
        this.createdReservations = createdReservations;
        this.totalReservations = createdReservations != null ? createdReservations.size() : 0;
        this.success = success;
    }
    
    /**
     * Create from list of domain entities
     */
    public static DomainBatchReservationResponseDTO fromEntities(
            List<DomainComponentReservationEntity> entities) {
        
        if (entities == null || entities.isEmpty()) {
            return new DomainBatchReservationResponseDTO(null, java.util.Collections.emptyList(), false);
        }
        
        UUID productionRunId = entities.get(0).getProductionRunId().getValue();
        
        List<DomainReservationResponseDTO> reservationDTOs = entities.stream()
                .map(DomainReservationResponseDTO::fromEntity)
                .collect(Collectors.toList());
        
        return new DomainBatchReservationResponseDTO(productionRunId, reservationDTOs, true);
    }
    
    // Getters
    public UUID getProductionRunId() {
        return productionRunId;
    }
    
    public List<DomainReservationResponseDTO> getCreatedReservations() {
        return createdReservations;
    }
    
    public int getTotalReservations() {
        return totalReservations;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    @Override
    public String toString() {
        return "DomainBatchReservationResponseDTO{" +
                "productionRunId=" + productionRunId +
                ", totalReservations=" + totalReservations +
                ", success=" + success +
                '}';
    }
}