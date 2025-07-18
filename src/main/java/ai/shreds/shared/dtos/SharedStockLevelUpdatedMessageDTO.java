package ai.shreds.shared.dtos;

import ai.shreds.application.dtos.ApplicationStockLevelUpdatedDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class SharedStockLevelUpdatedMessageDTO {
    private UUID itemId;
    private UUID locationId;
    private BigDecimal newStockLevel;
    private LocalDateTime timestamp;

    public SharedStockLevelUpdatedMessageDTO() {
    }

    public SharedStockLevelUpdatedMessageDTO(UUID itemId, UUID locationId, BigDecimal newStockLevel, LocalDateTime timestamp) {
        this.itemId = itemId;
        this.locationId = locationId;
        this.newStockLevel = newStockLevel;
        this.timestamp = timestamp;
    }

    public ApplicationStockLevelUpdatedDTO toApplicationDTO() {
        return new ApplicationStockLevelUpdatedDTO(
            this.itemId,
            this.locationId,
            this.newStockLevel,
            this.timestamp
        );
    }

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public BigDecimal getNewStockLevel() {
        return newStockLevel;
    }

    public void setNewStockLevel(BigDecimal newStockLevel) {
        this.newStockLevel = newStockLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
