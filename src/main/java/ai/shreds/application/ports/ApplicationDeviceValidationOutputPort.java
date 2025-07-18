package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedDeviceValidationResult;
import java.time.Instant;

public interface ApplicationDeviceValidationOutputPort {
    /**
     * Checks if the device with the given IMEI is authorized.
     * @param imei the IMEI string
     * @return true if authorized, false otherwise
     */
    boolean isDeviceAuthorized(String imei);

    /**
     * Updates the last communication timestamp for the device.
     * @param imei the device IMEI
     * @param timestamp the new last communication time
     */
    void updateLastCommunication(String imei, Instant timestamp);

    /**
     * Retrieves detailed validation result for the device.
     * @param imei the device IMEI
     * @return SharedDeviceValidationResult with details
     */
    SharedDeviceValidationResult getDeviceDetails(String imei);
}
