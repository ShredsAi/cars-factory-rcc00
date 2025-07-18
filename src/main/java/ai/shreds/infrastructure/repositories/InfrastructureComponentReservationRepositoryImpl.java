package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.ports.DomainOutputPortReservationRepository;
import ai.shreds.domain.value_objects.*;
import ai.shreds.infrastructure.entities.InfrastructureComponentReservationJpaEntity;
import ai.shreds.infrastructure.mappers.InfrastructureComponentReservationMapper;
import ai.shreds.infrastructure.repositories.jpa.InfrastructureComponentReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InfrastructureComponentReservationRepositoryImpl implements DomainOutputPortReservationRepository {

    private final InfrastructureComponentReservationJpaRepository jpaRepository;
    private final InfrastructureComponentReservationMapper mapper;
    private final EntityManager entityManager;

    @Override
    public DomainComponentReservationEntity save(DomainComponentReservationEntity reservation) {
        // Check if entity already exists in database
        Optional<InfrastructureComponentReservationJpaEntity> existingEntity = 
                jpaRepository.findById(reservation.getReservationId().getValue());
        
        if (existingEntity.isPresent()) {
            // Update existing entity
            InfrastructureComponentReservationJpaEntity jpaEntity = existingEntity.get();
            updateJpaEntityFromDomain(jpaEntity, reservation);
            InfrastructureComponentReservationJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return mapper.toDomainEntity(savedEntity);
        } else {
            // Create new entity
            InfrastructureComponentReservationJpaEntity jpaEntity = mapper.toJpaEntity(reservation);
            InfrastructureComponentReservationJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return mapper.toDomainEntity(savedEntity);
        }
    }

    private void updateJpaEntityFromDomain(InfrastructureComponentReservationJpaEntity jpaEntity, DomainComponentReservationEntity domainEntity) {
        jpaEntity.setItemId(domainEntity.getItemId().getValue());
        jpaEntity.setLocationId(domainEntity.getLocationId().getValue());
        jpaEntity.setProductionRunId(domainEntity.getProductionRunId().getValue());
        jpaEntity.setQuantityReserved(domainEntity.getQuantityReserved().getValue());
        jpaEntity.setQuantityUnit(domainEntity.getQuantityReserved().getUnit());
        jpaEntity.setReservedBy(domainEntity.getReservedBy().getValue());
        jpaEntity.setReservedAt(LocalDateTime.ofInstant(domainEntity.getReservedAt(), ZoneOffset.UTC));
        jpaEntity.setExpiresAt(LocalDateTime.ofInstant(domainEntity.getExpiresAt(), ZoneOffset.UTC));
        jpaEntity.setStatus(domainEntity.getStatus().getValue());
    }

    @Override
    public Optional<DomainComponentReservationEntity> findById(DomainValueReservationId reservationId) {
        return jpaRepository.findById(reservationId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<DomainComponentReservationEntity> findByProductionRunId(DomainValueProductionRunId productionRunId) {
        List<InfrastructureComponentReservationJpaEntity> jpaEntities = jpaRepository.findByProductionRunId(productionRunId.getValue());
        return mapper.toDomainEntities(jpaEntities);
    }

    @Override
    public List<DomainComponentReservationEntity> findExpiredActiveReservations(Instant currentTime) {
        LocalDateTime currentLocalDateTime = LocalDateTime.ofInstant(currentTime, ZoneOffset.UTC);
        List<InfrastructureComponentReservationJpaEntity> jpaEntities = jpaRepository.findExpiredActiveReservations(currentLocalDateTime);
        return mapper.toDomainEntities(jpaEntities);
    }

    @Override
    public List<DomainComponentReservationEntity> findActiveReservationsByItemAndLocation(DomainValueItemId itemId, DomainValueLocationId locationId) {
        List<InfrastructureComponentReservationJpaEntity> jpaEntities = jpaRepository.findByItemIdAndLocationIdAndStatus(
                itemId.getValue(), locationId.getValue(), "ACTIVE");
        return mapper.toDomainEntities(jpaEntities);
    }

    @Override
    public DomainValueQuantity getTotalReservedQuantity(DomainValueItemId itemId, DomainValueLocationId locationId) {
        // This is a simplified implementation. A more efficient approach would be a custom query to sum the quantity.
        List<DomainComponentReservationEntity> reservations = findActiveReservationsByItemAndLocation(itemId, locationId);
        return reservations.stream()
                .map(DomainComponentReservationEntity::getQuantityReserved)
                .reduce(DomainValueQuantity.zero("PIECES"), DomainValueQuantity::add);
    }

    @Override
    public void delete(DomainValueReservationId reservationId) {
        jpaRepository.deleteById(reservationId.getValue());
    }

    @Override
    public boolean exists(DomainValueReservationId reservationId) {
        return jpaRepository.existsById(reservationId.getValue());
    }
}