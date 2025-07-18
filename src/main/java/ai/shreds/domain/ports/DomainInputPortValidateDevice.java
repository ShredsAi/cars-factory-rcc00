package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import java.time.Instant;

/**
 * Domain input port for device validation operations.
 * This port defines the contract for validating device authorization, IMEI format, and managing device communication.
 */
public interface DomainInputPortValidateDevice {
    
    /**
     * Validates if a device with the specified IMEI is authorized to access the system.
     * 
     * @param imei the device IMEI to validate
     * @return true if device is authorized, false otherwise
     */
    boolean validateDeviceAuthorization(String imei);
    
    /**
     * Validates if the provided IMEI format is correct.
     * 
     * @param imei the IMEI to validate
     * @return true if IMEI format is valid, false otherwise
     */
    boolean validateImeiFormat(String imei);
    
    /**
     * Checks if a device with the specified IMEI is registered in the system.
     * 
     * @param imei the device IMEI to check
     * @return the GPS device entity if found
     * @throws ai.shreds.domain.exceptions.DomainDeviceNotAuthorizedException if device is not registered or inactive
     */
    DomainGPSDeviceEntity checkDeviceRegistration(String imei);
    
    /**
     * Updates the last communication timestamp for a device.
     * 
     * @param imei the device IMEI
     * @param timestamp the new last communication timestamp
     */
    void updateDeviceCommunication(String imei, Instant timestamp);
}