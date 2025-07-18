package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainDeviceProtocolEntity;
import ai.shreds.shared.enums.SharedProtocolTypeEnum;

import java.util.List;
import java.util.Optional;

public interface DomainOutputPortDeviceProtocolRepository {
    /**
     * Find a device protocol by its type and active status
     *
     * @param protocolType The protocol type to search for
     * @param isActive The active status to filter by
     * @return Optional containing the device protocol if found
     */
    Optional<DomainDeviceProtocolEntity> findByProtocolTypeAndIsActive(SharedProtocolTypeEnum protocolType, boolean isActive);

    /**
     * Find a device protocol by default port and active status
     *
     * @param port the default port
     * @param isActive the active status filter
     * @return Optional containing the device protocol if found
     */
    Optional<DomainDeviceProtocolEntity> findByDefaultPortAndIsActive(Integer port, boolean isActive);

    /**
     * Finds all active device protocols.
     *
     * @return List of active device protocol entities
     */
    List<DomainDeviceProtocolEntity> findAllActive();
}