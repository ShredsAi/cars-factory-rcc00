package ai.shreds.infrastructure.repositories.jpa;

import ai.shreds.infrastructure.entities.InfrastructureDeviceProtocolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InfrastructureJPADeviceProtocolRepository extends JpaRepository<InfrastructureDeviceProtocolJpaEntity, Long> {
    
    @Query("SELECT p FROM InfrastructureDeviceProtocolJpaEntity p LEFT JOIN FETCH p.supportedManufacturers WHERE p.defaultPort = :port AND p.isActive = :isActive")
    Optional<InfrastructureDeviceProtocolJpaEntity> findByDefaultPortAndIsActive(@Param("port") Integer port, @Param("isActive") boolean isActive);
    
    @Query("SELECT p FROM InfrastructureDeviceProtocolJpaEntity p LEFT JOIN FETCH p.supportedManufacturers WHERE p.protocolType = :protocolType AND p.isActive = :isActive")
    Optional<InfrastructureDeviceProtocolJpaEntity> findByProtocolTypeAndIsActive(@Param("protocolType") String protocolType, @Param("isActive") boolean isActive);
    
    @Query("SELECT p FROM InfrastructureDeviceProtocolJpaEntity p LEFT JOIN FETCH p.supportedManufacturers WHERE p.isActive = true")
    List<InfrastructureDeviceProtocolJpaEntity> findByIsActiveTrue();
}