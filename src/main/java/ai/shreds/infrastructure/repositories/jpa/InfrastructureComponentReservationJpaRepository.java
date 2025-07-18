package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.entities.InfrastructureComponentReservationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InfrastructureComponentReservationJpaRepository extends JpaRepository<InfrastructureComponentReservationJpaEntity, UUID> {

    List<InfrastructureComponentReservationJpaEntity> findByProductionRunId(UUID productionRunId);

    @Query("SELECT r FROM InfrastructureComponentReservationJpaEntity r WHERE r.status = 'ACTIVE' AND r.expiresAt < :currentTime")
    List<InfrastructureComponentReservationJpaEntity> findExpiredActiveReservations(@Param("currentTime") LocalDateTime currentTime);

    List<InfrastructureComponentReservationJpaEntity> findByItemIdAndLocationIdAndStatus(UUID itemId, UUID locationId, String status);
}