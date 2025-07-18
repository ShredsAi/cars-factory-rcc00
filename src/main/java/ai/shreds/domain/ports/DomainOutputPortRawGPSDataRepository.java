package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainRawGPSDataEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainOutputPortRawGPSDataRepository {
    /**
     * Save raw GPS data
     *
     * @param data The GPS data to save
     * @return The saved GPS data
     */
    DomainRawGPSDataEntity save(DomainRawGPSDataEntity data);

    /**
     * Find raw GPS data by ID
     *
     * @param id The ID to search for
     * @return Optional containing the GPS data if found
     */
    Optional<DomainRawGPSDataEntity> findById(UUID id);

    /**
     * Find all raw GPS data for a device
     *
     * @param deviceId The device ID to search for
     * @return List of GPS data for the device
     */
    List<DomainRawGPSDataEntity> findByDeviceId(UUID deviceId);
}