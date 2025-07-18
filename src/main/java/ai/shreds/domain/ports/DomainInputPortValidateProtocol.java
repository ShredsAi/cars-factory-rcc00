package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainDeviceProtocolEntity;
import ai.shreds.shared.enums.SharedProtocolTypeEnum;

/**
 * Domain input port for protocol validation operations.
 * This port defines the contract for validating protocol support, port configurations, and protocol settings.
 */
public interface DomainInputPortValidateProtocol {
    
    /**
     * Validates if the specified protocol type is supported by the system.
     * 
     * @param protocolType the protocol type to validate
     * @return true if protocol is supported, false otherwise
     */
    boolean validateProtocolSupport(SharedProtocolTypeEnum protocolType);
    
    /**
     * Validates if the specified port is valid for the given protocol type.
     * 
     * @param port the port number to validate
     * @param protocolType the protocol type
     * @return true if port is valid for the protocol, false otherwise
     */
    boolean validateProtocolPort(Integer port, SharedProtocolTypeEnum protocolType);
    
    /**
     * Retrieves the protocol configuration for the specified protocol type.
     * 
     * @param protocolType the protocol type
     * @return the device protocol entity containing configuration details
     * @throws ai.shreds.domain.exceptions.DomainProtocolNotSupportedException if protocol is not supported
     */
    DomainDeviceProtocolEntity getProtocolConfiguration(SharedProtocolTypeEnum protocolType);
}