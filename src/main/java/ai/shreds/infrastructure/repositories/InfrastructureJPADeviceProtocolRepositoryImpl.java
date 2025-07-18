package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainDeviceProtocolEntity;
import ai.shreds.domain.ports.DomainOutputPortDeviceProtocolRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureRepositoryException;
import ai.shreds.infrastructure.external_services.InfrastructureEntityMapper;
import ai.shreds.infrastructure.repositories.jpa.InfrastructureJPADeviceProtocolRepository;
import ai.shreds.shared.enums.SharedProtocolTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the domain device protocol repository port using JPA.
 * Provides concrete implementation for device protocol persistence operations.
 */
@Repository
@RequiredArgsConstructor
public class InfrastructureJPADeviceProtocolRepositoryImpl implements DomainOutputPortDeviceProtocolRepository {
    
    private final InfrastructureJPADeviceProtocolRepository jpaRepository;
    private final InfrastructureEntityMapper entityMapper;
    
    /**
     * Finds a device protocol by default port and active status.
     *
     * @param port the default port
     * @param isActive the active status filter
     * @return an Optional containing the device protocol entity if found
     */
    @Override
    public Optional<DomainDeviceProtocolEntity> findByDefaultPortAndIsActive(Integer port, boolean isActive) {
        try {
            return jpaRepository.findByDefaultPortAndIsActive(port, isActive)
                    .map(entityMapper::toDomainDeviceProtocol);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to find device protocol by port: " + port,
                    "findByDefaultPortAndIsActive",
                    "DomainDeviceProtocolEntity",
                    e
            );
        }
    }
    
    /**
     * Finds a device protocol by protocol type and active status.
     *
     * @param protocolType the protocol type
     * @param isActive the active status filter
     * @return an Optional containing the device protocol entity if found
     */
    @Override
    public Optional<DomainDeviceProtocolEntity> findByProtocolTypeAndIsActive(SharedProtocolTypeEnum protocolType, boolean isActive) {
        try {
            return jpaRepository.findByProtocolTypeAndIsActive(protocolType.name(), isActive)
                    .map(entityMapper::toDomainDeviceProtocol);
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to find device protocol by type: " + protocolType,
                    "findByProtocolTypeAndIsActive",
                    "DomainDeviceProtocolEntity",
                    e
            );
        }
    }
    
    /**
     * Finds all active device protocols.
     *
     * @return list of active device protocol entities
     */
    @Override
    public List<DomainDeviceProtocolEntity> findAllActive() {
        try {
            return jpaRepository.findByIsActiveTrue().stream()
                    .map(entityMapper::toDomainDeviceProtocol)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InfrastructureRepositoryException(
                    "Failed to find all active device protocols",
                    "findAllActive",
                    "DomainDeviceProtocolEntity",
                    e
            );
        }
    }
}