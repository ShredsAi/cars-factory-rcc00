package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import java.time.Instant;
import java.util.Optional;

/**
 * Domain output port for GPS device repository operations.
 * This port defines the contract for persisting and retrieving GPS device entities.
 */
public interface DomainOutputPortGPSDeviceRepository {
    
    /**
     * Finds a GPS device by its IMEI.
     *
     * @param imei the device IMEI
     * @return an Optional containing the GPS device entity if found
     */
    Optional<DomainGPSDeviceEntity> findDeviceByImei(String imei);

    /**
     * Finds a GPS device by IMEI and active status.
     * 
     * @param imei the device IMEI
     * @param isActive the active status filter
     * @return an Optional containing the GPS device entity if found
     */
    Optional<DomainGPSDeviceEntity> findByImeiAndIsActive(String imei, boolean isActive);
    
    /**
     * Updates the last communication timestamp for a device with the specified IMEI.
     * 
     * @param imei the device IMEI
     * @param timestamp the new last communication timestamp
     */
    void updateLastCommunicationTimestamp(String imei, Instant timestamp);
    
    /**
     * Saves a GPS device entity to the repository.
     * 
     * @param device the GPS device entity to save
     * @return the saved GPS device entity
     */
    DomainGPSDeviceEntity saveDevice(DomainGPSDeviceEntity device);
}