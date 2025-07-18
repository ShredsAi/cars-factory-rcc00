package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.entities.InfrastructureStockLevelJpaEntity;
import ai.shreds.infrastructure.entities.InfrastructureStockLevelId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InfrastructureStockLevelJpaRepository extends JpaRepository<InfrastructureStockLevelJpaEntity, InfrastructureStockLevelId> {

    Optional<InfrastructureStockLevelJpaEntity> findByItemIdAndLocationId(UUID itemId, UUID locationId);

    // Corrected the typo from PESSIMistic_WRITE to PESSIMISTIC_WRITE
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM InfrastructureStockLevelJpaEntity s WHERE s.itemId = :itemId AND s.locationId = :locationId")
    Optional<InfrastructureStockLevelJpaEntity> findAndLockByItemIdAndLocationId(@Param("itemId") UUID itemId, @Param("locationId") UUID locationId);
}