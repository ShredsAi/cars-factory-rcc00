package ai.shreds.domain.ports;

import ai.shreds.shared.enums.SharedProtocolTypeEnum;
import ai.shreds.shared.value_objects.SharedParsedGpsData;

/**
 * Domain input port for data integrity validation operations.
 * This port defines the contract for validating raw payload, checksum integrity, and data completeness.
 */
public interface DomainInputPortValidateDataIntegrity {
    
    /**
     * Validates the raw payload data for the specified protocol type.
     * 
     * @param rawPayload the raw payload data to validate
     * @param protocolType the protocol type
     * @return true if raw payload is valid, false otherwise
     */
    boolean validateRawPayload(byte[] rawPayload, SharedProtocolTypeEnum protocolType);
    
    /**
     * Validates the checksum integrity of the data for the specified protocol type.
     * 
     * @param data the data to validate
     * @param checksum the checksum to verify against
     * @param protocolType the protocol type
     * @return true if checksum is valid, false otherwise
     */
    boolean validateChecksumIntegrity(byte[] data, byte[] checksum, SharedProtocolTypeEnum protocolType);
    
    /**
     * Validates the completeness of parsed GPS data.
     * 
     * @param data the parsed GPS data to validate
     * @return true if data is complete, false otherwise
     */
    boolean validateDataCompleteness(SharedParsedGpsData data);
}