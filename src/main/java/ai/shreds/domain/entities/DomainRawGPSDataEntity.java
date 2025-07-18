package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainDataIntegrityException;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Domain entity representing raw GPS data received from devices.
 * This entity encapsulates raw GPS data information, validation logic, and business rules.
 */
public class DomainRawGPSDataEntity {
    
    /**
     * Enumeration of supported protocol types for GPS data.
     */
    public enum ProtocolType {
        UNKNOWN,
        NMEA,
        GPX,
        UBLOX
    }
    
    private UUID id;
    private String deviceImei;
    private byte[] rawPayload;
    private ProtocolType protocolType;
    private Instant receivedTimestamp;
    private String sourceAddress;
    private Integer sourcePort;
    private Integer dataLength;
    
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    private static final Pattern IMEI_PATTERN = Pattern.compile("^[0-9]{15}$");
    
    /**
     * Constructs a new DomainRawGPSDataEntity with the specified parameters.
     */
    public DomainRawGPSDataEntity(UUID id, String deviceImei, byte[] rawPayload, 
                                ProtocolType protocolType, Instant receivedTimestamp, 
                                String sourceAddress, Integer sourcePort, Integer dataLength) {
        this.id = id;
        this.deviceImei = deviceImei;
        this.rawPayload = rawPayload != null ? rawPayload.clone() : null;
        this.protocolType = protocolType;
        this.receivedTimestamp = receivedTimestamp;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.dataLength = dataLength;
        validate();
    }
    
    /**
     * Validates the raw GPS data integrity.
     * 
     * @return true if data is valid
     * @throws DomainDataIntegrityException if data integrity validation fails
     */
    public boolean validateData() throws DomainDataIntegrityException {
        try {
            validate();
            return true;
        } catch (Exception e) {
            throw new DomainDataIntegrityException("RawGPSData", e.getMessage());
        }
    }
    
    /**
     * Validates the entire entity state.
     * 
     * @throws DomainDataIntegrityException if validation fails
     */
    public void validate() {
        validateDeviceImei();
        validateProtocolType();
        validateReceivedTimestamp();
        validateSourceInfo();
        validatePayload();
        validateDataLength();
    }
    
    /**
     * Validates the device IMEI field.
     * 
     * @throws DomainDataIntegrityException if IMEI is invalid
     */
    private void validateDeviceImei() {
        if (deviceImei == null || deviceImei.trim().isEmpty()) {
            throw new DomainDataIntegrityException("DeviceImei", "Device IMEI cannot be null or empty");
        }
        
        if (!IMEI_PATTERN.matcher(deviceImei).matches()) {
            throw new DomainDataIntegrityException("DeviceImei", "Device IMEI must be exactly 15 digits");
        }
    }
    
    /**
     * Validates the protocol type field.
     * 
     * @throws DomainDataIntegrityException if protocol type is invalid
     */
    private void validateProtocolType() {
        if (protocolType == null) {
            throw new DomainDataIntegrityException("ProtocolType", "Protocol type cannot be null");
        }
        
        if (protocolType == ProtocolType.UNKNOWN) {
            throw new DomainDataIntegrityException("ProtocolType", "Protocol type cannot be UNKNOWN");
        }
    }
    
    /**
     * Validates the received timestamp field.
     * 
     * @throws DomainDataIntegrityException if timestamp is invalid
     */
    private void validateReceivedTimestamp() {
        if (receivedTimestamp == null) {
            throw new DomainDataIntegrityException("ReceivedTimestamp", "Received timestamp cannot be null");
        }
        
        if (receivedTimestamp.isAfter(Instant.now())) {
            throw new DomainDataIntegrityException("ReceivedTimestamp", "Received timestamp cannot be in the future");
        }
    }
    
    /**
     * Validates the source information fields.
     * 
     * @throws DomainDataIntegrityException if source info is invalid
     */
    private void validateSourceInfo() {
        if (sourceAddress == null || sourceAddress.trim().isEmpty()) {
            throw new DomainDataIntegrityException("SourceAddress", "Source address cannot be null or empty");
        }
        
        if (!IP_PATTERN.matcher(sourceAddress).matches()) {
            throw new DomainDataIntegrityException("SourceAddress", "Source address must be a valid IP address");
        }
        
        if (sourcePort == null || sourcePort <= 0 || sourcePort > 65535) {
            throw new DomainDataIntegrityException("SourcePort", "Source port must be between 1 and 65535");
        }
    }
    
    /**
     * Validates the raw payload field.
     * 
     * @throws DomainDataIntegrityException if payload is invalid
     */
    private void validatePayload() {
        if (rawPayload == null || rawPayload.length == 0) {
            throw new DomainDataIntegrityException("RawPayload", "Raw payload cannot be null or empty");
        }
        
        if (rawPayload.length > 10240) { // 10KB max
            throw new DomainDataIntegrityException("RawPayload", "Raw payload size exceeds maximum allowed size");
        }
    }
    
    /**
     * Validates the data length field.
     * 
     * @throws DomainDataIntegrityException if data length is invalid
     */
    private void validateDataLength() {
        if (dataLength == null || dataLength <= 0) {
            throw new DomainDataIntegrityException("DataLength", "Data length must be greater than 0");
        }
        
        if (rawPayload != null && dataLength != rawPayload.length) {
            throw new DomainDataIntegrityException("DataLength", "Data length does not match raw payload length");
        }
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDeviceImei() { return deviceImei; }
    public void setDeviceImei(String deviceImei) { this.deviceImei = deviceImei; }
    public byte[] getRawPayload() { return rawPayload != null ? rawPayload.clone() : null; }
    public void setRawPayload(byte[] rawPayload) { this.rawPayload = rawPayload != null ? rawPayload.clone() : null; }
    public ProtocolType getProtocolType() { return protocolType; }
    public void setProtocolType(ProtocolType protocolType) { this.protocolType = protocolType; }
    public Instant getReceivedTimestamp() { return receivedTimestamp; }
    public void setReceivedTimestamp(Instant receivedTimestamp) { this.receivedTimestamp = receivedTimestamp; }
    public String getSourceAddress() { return sourceAddress; }
    public void setSourceAddress(String sourceAddress) { this.sourceAddress = sourceAddress; }
    public Integer getSourcePort() { return sourcePort; }
    public void setSourcePort(Integer sourcePort) { this.sourcePort = sourcePort; }
    public Integer getDataLength() { return dataLength; }
    public void setDataLength(Integer dataLength) { this.dataLength = dataLength; }
}