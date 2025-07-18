package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.ports.DomainOutputPortStockLevelRepository;
import ai.shreds.domain.value_objects.DomainValueItemId;
import ai.shreds.domain.value_objects.DomainValueLocationId;
import ai.shreds.infrastructure.entities.InfrastructureStockLevelJpaEntity;
import ai.shreds.infrastructure.mappers.InfrastructureStockLevelMapper;
import ai.shreds.infrastructure.repositories.jpa.InfrastructureStockLevelJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@RequiredArgsConstructor
public class InfrastructureStockLevelRepositoryImpl implements DomainOutputPortStockLevelRepository {

    private final InfrastructureStockLevelJpaRepository jpaRepository;
    private final InfrastructureStockLevelMapper mapper;
    
    // Cache to track locked JPA entities to avoid Hibernate session issues
    private final ConcurrentMap<String, InfrastructureStockLevelJpaEntity> lockedEntities = new ConcurrentHashMap<>();

    @Override
    public Optional<DomainStockLevelEntity> findByItemAndLocation(DomainValueItemId itemId, DomainValueLocationId locationId) {
        return jpaRepository.findByItemIdAndLocationId(itemId.getValue(), locationId.getValue())
                .map(mapper::toDomainEntity);
    }

    @Override
    public DomainStockLevelEntity save(DomainStockLevelEntity stockLevel) {
        String key = stockLevel.getItemId().getValue() + ":" + stockLevel.getLocationId().getValue();
        
        // Check if this entity was previously locked
        InfrastructureStockLevelJpaEntity lockedEntity = lockedEntities.get(key);
        if (lockedEntity != null) {
            // Update the locked entity directly to avoid Hibernate session issues
            lockedEntity.setQuantityOnHand(stockLevel.getQuantityOnHand().getValue());
            lockedEntity.setReservedQty(stockLevel.getReservedQuantity().getValue());
            var savedEntity = jpaRepository.save(lockedEntity);
            // Remove from cache after saving
            lockedEntities.remove(key);
            return mapper.toDomainEntity(savedEntity);
        } else {
            // Normal save for non-locked entities
            var jpaEntity = mapper.toJpaEntity(stockLevel);
            var savedEntity = jpaRepository.save(jpaEntity);
            return mapper.toDomainEntity(savedEntity);
        }
    }

    @Override
    public Optional<DomainStockLevelEntity> lockForUpdate(DomainValueItemId itemId, DomainValueLocationId locationId) {
        Optional<InfrastructureStockLevelJpaEntity> jpaEntity = jpaRepository.findAndLockByItemIdAndLocationId(itemId.getValue(), locationId.getValue());
        
        if (jpaEntity.isPresent()) {
            String key = itemId.getValue() + ":" + locationId.getValue();
            // Cache the locked entity to avoid Hibernate session issues
            lockedEntities.put(key, jpaEntity.get());
            return Optional.of(mapper.toDomainEntity(jpaEntity.get()));
        }
        
        return Optional.empty();
    }
}