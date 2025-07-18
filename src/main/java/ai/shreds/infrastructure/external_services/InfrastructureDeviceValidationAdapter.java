package ai.shreds.infrastructure.external_services;

import ai.shreds.application.ports.ApplicationDeviceValidationOutputPort;
import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import ai.shreds.domain.exceptions.DomainDeviceNotAuthorizedException;
import ai.shreds.domain.exceptions.DomainInvalidImeiException;
import ai.shreds.domain.ports.DomainInputPortValidateDevice;
import ai.shreds.shared.dtos.SharedDeviceValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Infrastructure adapter that implements the application device validation output port.
 * Delegates device validation operations to the domain validation service.
 */
@Component
public class InfrastructureDeviceValidationAdapter implements ApplicationDeviceValidationOutputPort {
    
    private final DomainInputPortValidateDevice deviceValidationService;
    private final InfrastructureEntityMapper entityMapper = new InfrastructureEntityMapper();
    
    @Autowired
    public InfrastructureDeviceValidationAdapter(
            DomainInputPortValidateDevice deviceValidationService) {
        this.deviceValidationService = deviceValidationService;
    }
    
    /**
     * Checks if a device with the specified IMEI is authorized.
     *
     * @param imei the device IMEI to validate
     * @return true if device is authorized, false otherwise
     */
    @Override
    public boolean isDeviceAuthorized(String imei) {
        try {
            return deviceValidationService.validateDeviceAuthorization(imei);
        } catch (DomainInvalidImeiException | DomainDeviceNotAuthorizedException e) {
            return false;
        }
    }
    
    /**
     * Updates the last communication timestamp for a device.
     *
     * @param imei the device IMEI
     * @param timestamp the new last communication timestamp
     */
    @Override
    public void updateLastCommunication(String imei, Instant timestamp) {
        deviceValidationService.updateDeviceCommunication(imei, timestamp);
    }
    
    /**
     * Retrieves detailed information about a device.
     *
     * @param imei the device IMEI
     * @return device validation result with detailed information
     */
    @Override
    public SharedDeviceValidationResult getDeviceDetails(String imei) {
        try {
            DomainGPSDeviceEntity device = deviceValidationService.checkDeviceRegistration(imei);
            boolean isAuthorized = deviceValidationService.validateDeviceAuthorization(imei);
            
            return new SharedDeviceValidationResult(
                device.getImei(),
                isAuthorized,
                device.isActive(),
                device.getManufacturer(),
                device.getModel(),
                device.getLastCommunicationTimestamp()
            );
        } catch (DomainDeviceNotAuthorizedException e) {
            return new SharedDeviceValidationResult(
                imei,
                false,
                false,
                "UNKNOWN",
                "UNKNOWN",
                null
            );
        } catch (DomainInvalidImeiException e) {
            return new SharedDeviceValidationResult(
                imei,
                false,
                false,
                "INVALID_IMEI",
                "INVALID_IMEI",
                null
            );
        }
    }
}