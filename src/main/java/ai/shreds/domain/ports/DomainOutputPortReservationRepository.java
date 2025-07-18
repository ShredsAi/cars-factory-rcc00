package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Domain output port for reservation repository operations.
 * This port defines the contract for persisting and retrieving reservation entities.
 */
public interface DomainOutputPortReservationRepository {

    DomainComponentReservationEntity save(DomainComponentReservationEntity reservation);

    Optional<DomainComponentReservationEntity> findById(DomainValueReservationId reservationId);

    List<DomainComponentReservationEntity> findByProductionRunId(DomainValueProductionRunId productionRunId);

    List<DomainComponentReservationEntity> findExpiredActiveReservations(Instant currentTime);

    List<DomainComponentReservationEntity> findActiveReservationsByItemAndLocation(DomainValueItemId itemId, DomainValueLocationId locationId);

    DomainValueQuantity getTotalReservedQuantity(DomainValueItemId itemId, DomainValueLocationId locationId);

    void delete(DomainValueReservationId reservationId);

    boolean exists(DomainValueReservationId reservationId);
}