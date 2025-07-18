package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.entities.InfrastructureGpsDeviceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface InfrastructureJPAGPSDeviceRepository extends JpaRepository<InfrastructureGpsDeviceJpaEntity, Long> {
    
    Optional<InfrastructureGpsDeviceJpaEntity> findByImei(String imei);

    Optional<InfrastructureGpsDeviceJpaEntity> findByImeiAndIsActive(String imei, boolean isActive);
    
    @Modifying
    @Transactional
    @Query("UPDATE InfrastructureGpsDeviceJpaEntity d SET d.lastCommunicationTimestamp = :timestamp WHERE d.imei = :imei")
    void updateLastCommunicationTimestamp(@Param("imei") String imei, @Param("timestamp") Instant timestamp);
}