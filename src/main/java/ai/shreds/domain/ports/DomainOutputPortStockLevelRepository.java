package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.value_objects.DomainValueItemId;
import ai.shreds.domain.value_objects.DomainValueLocationId;
import java.util.Optional;

public interface DomainOutputPortStockLevelRepository {
    Optional<DomainStockLevelEntity> findByItemAndLocation(DomainValueItemId itemId, DomainValueLocationId locationId);
    DomainStockLevelEntity save(DomainStockLevelEntity stockLevel);
    Optional<DomainStockLevelEntity> lockForUpdate(DomainValueItemId itemId, DomainValueLocationId locationId);
}