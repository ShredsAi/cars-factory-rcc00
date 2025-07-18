package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.DomainValueProductionRunId;
import java.util.List;

/**
 * Input port for retrieving reservations by production run.
 * This port defines the contract for retrieving all reservations associated with a production run.
 */
public interface DomainInputPortGetReservationsByProductionRun {
    
    /**
     * Retrieve all reservations for a production run
     * @param productionRunId the production run ID
     * @return list of reservation entities
     */
    List<DomainComponentReservationEntity> getReservationsByProductionRun(DomainValueProductionRunId productionRunId);
}