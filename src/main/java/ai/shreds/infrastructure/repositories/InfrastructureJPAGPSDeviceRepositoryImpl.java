package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import ai.shreds.domain.ports.DomainOutputPortGPSDeviceRepository;
import ai.shreds.infrastructure.entities.InfrastructureGpsDeviceJpaEntity;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import ai.shreds.infrastructure.external_services.InfrastructureEntityMapper;
import ai.shreds.infrastructure.repositories.jpa.InfrastructureJPAGPSDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InfrastructureJPAGPSDeviceRepositoryImpl implements DomainOutputPortGPSDeviceRepository {
    
    private final InfrastructureJPAGPSDeviceRepository jpaRepository;
    private final InfrastructureEntityMapper entityMapper;

    @Override
    public Optional<DomainGPSDeviceEntity> findDeviceByImei(String imei) {
        try {
            return jpaRepository.findByImei(imei)
                    .map(entityMapper::toDomainGPSDevice);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to find GPS device by IMEI: " + imei, 
                    "findDeviceByImei", 
                    "DomainGPSDeviceEntity", 
                    e
            );
        }
    }
    
    @Override
    public Optional<DomainGPSDeviceEntity> findByImeiAndIsActive(String imei, boolean isActive) {
        try {
            return jpaRepository.findByImeiAndIsActive(imei, isActive)
                    .map(entityMapper::toDomainGPSDevice);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to find GPS device by IMEI: " + imei + " and active status: " + isActive,
                    "findByImeiAndIsActive",
                    "DomainGPSDeviceEntity",
                    e
            );
        }
    }
    
    @Override
    public void updateLastCommunicationTimestamp(String imei, Instant timestamp) {
        try {
            jpaRepository.updateLastCommunicationTimestamp(imei, timestamp);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to update last communication timestamp for IMEI: " + imei,
                    "updateLastCommunicationTimestamp",
                    "DomainGPSDeviceEntity",
                    e
            );
        }
    }
    
    @Override
    public DomainGPSDeviceEntity saveDevice(DomainGPSDeviceEntity device) {
        try {
            InfrastructureGpsDeviceJpaEntity jpaEntity = entityMapper.toJPAGPSDevice(device);
            InfrastructureGpsDeviceJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return entityMapper.toDomainGPSDevice(savedEntity);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to save GPS device with IMEI: " + device.getImei(),
                    "saveDevice",
                    "DomainGPSDeviceEntity",
                    e
            );
        }
    }
}