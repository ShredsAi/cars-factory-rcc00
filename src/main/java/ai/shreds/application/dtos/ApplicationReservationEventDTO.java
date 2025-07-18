package ai.shreds.application.dtos;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import ai.shreds.domain.events.DomainReservationEvent;
import ai.shreds.shared.dtos.SharedReservationEventMessageDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReservationEventDTO {
    private UUID reservationId;
    private UUID itemId;
    private UUID locationId;
    private UUID productionRunId;
    private BigDecimal quantityReserved;
    private String quantityUnit;
    private String status;
    private String eventType;
    private LocalDateTime timestamp;

    public SharedReservationEventMessageDTO toSharedDTO() {
        SharedReservationEventMessageDTO sharedDTO = new SharedReservationEventMessageDTO();
        sharedDTO.setReservationId(this.reservationId);
        sharedDTO.setItemId(this.itemId);
        sharedDTO.setLocationId(this.locationId);
        sharedDTO.setProductionRunId(this.productionRunId);
        sharedDTO.setQuantityReserved(this.quantityReserved);
        sharedDTO.setQuantityUnit(this.quantityUnit);
        sharedDTO.setStatus(this.status);
        sharedDTO.setEventType(this.eventType);
        sharedDTO.setTimestamp(this.timestamp);
        return sharedDTO;
    }

    public static ApplicationReservationEventDTO fromDomainEvent(DomainReservationEvent event) {
        ApplicationReservationEventDTO dto = new ApplicationReservationEventDTO();
        dto.setReservationId(event.getReservationId().getValue());
        dto.setItemId(event.getItemId().getValue());
        dto.setLocationId(event.getLocationId().getValue());
        dto.setProductionRunId(event.getProductionRunId().getValue());
        dto.setQuantityReserved(event.getQuantityReserved().getValue());
        dto.setQuantityUnit(event.getQuantityReserved().getUnit().toString());
        dto.setStatus(event.getStatus().toString());
        dto.setEventType(event.getEventType());
        // Convert Instant to LocalDateTime
        dto.setTimestamp(LocalDateTime.ofInstant(event.getTimestamp(), ZoneId.systemDefault()));
        return dto;
    }
}