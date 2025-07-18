package ai.shreds.application.dtos;

import java.util.UUID;
import ai.shreds.domain.value_objects.DomainValueReservationId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a reservation identifier in the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReservationIdDTO {
    private UUID reservationId;

    /**
     * Convert this DTO to the domain value object.
     */
    public DomainValueReservationId toDomainValue() {
        return DomainValueReservationId.from(this.reservationId);
    }
}