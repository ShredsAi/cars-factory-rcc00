package ai.shreds.application.dtos;

import java.util.UUID;
import ai.shreds.domain.value_objects.DomainValueProductionRunId;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a production run identifier in the application layer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationProductionRunIdDTO {
    private UUID productionRunId;

    /**
     * Convert this DTO to the domain value object.
     */
    public DomainValueProductionRunId toDomainValue() {
        return DomainValueProductionRunId.from(this.productionRunId);
    }
}