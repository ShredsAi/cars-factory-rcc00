package ai.shreds.domain.entities;

import ai.shreds.domain.exceptions.DomainInvalidImeiException;
import java.time.Instant;
import java.util.regex.Pattern;

/**
 * Domain entity representing a GPS device in the system.
 * This entity encapsulates device information, validation logic, and business rules.
 */
public class DomainGPSDeviceEntity {
    
    private String imei;
    private String manufacturer;
    private String model;
    private String protocolVersion;
    private Instant registrationTimestamp;
    private Instant lastCommunicationTimestamp;
    private Boolean isActive;
    
    private static final Pattern IMEI_PATTERN = Pattern.compile("^[0-9]{15}$");
    
    /**
     * Constructs a new DomainGPSDeviceEntity with the specified parameters.
     */
    public DomainGPSDeviceEntity(String imei, String manufacturer, String model, 
                               String protocolVersion, Instant registrationTimestamp, 
                               Instant lastCommunicationTimestamp, Boolean isActive) {
        this.imei = imei;
        this.manufacturer = manufacturer;
        this.model = model;
        this.protocolVersion = protocolVersion;
        this.registrationTimestamp = registrationTimestamp;
        this.lastCommunicationTimestamp = lastCommunicationTimestamp;
        this.isActive = isActive;
        validate();
    }
    
    /**
     * Updates the last communication timestamp for this device.
     *
     * @param timestamp the new last communication timestamp
     */
    public void updateLastCommunication(Instant timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        this.lastCommunicationTimestamp = timestamp;
    }
    
    /**
     * Activates this device.
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Deactivates this device.
     */
    public void deactivate() {
        this.isActive = false;
    }
    
    /**
     * Validates if the given IMEI is valid.
     *
     * @param imei the IMEI to validate
     * @return true if IMEI is valid
     * @throws DomainInvalidImeiException if IMEI is invalid
     */
    public boolean isValidImei(String imei) throws DomainInvalidImeiException {
        if (imei == null || imei.trim().isEmpty()) {
            throw new DomainInvalidImeiException(imei, "IMEI cannot be null or empty");
        }
        
        if (!IMEI_PATTERN.matcher(imei).matches()) {
            throw new DomainInvalidImeiException(imei, "IMEI must be exactly 15 digits");
        }
        
        // Validate Luhn checksum
        if (!isValidLuhnChecksum(imei)) {
            throw new DomainInvalidImeiException(imei, "IMEI checksum validation failed");
        }
        
        return true;
    }
    
    /**
     * Validates the entire entity state.
     *
     * @throws DomainInvalidImeiException if IMEI is invalid
     * @throws IllegalStateException if entity state is invalid
     */
    public void validate() {
        validateImei();
        validateTimestamps();
        
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            throw new IllegalStateException("Manufacturer cannot be null or empty");
        }
        
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalStateException("Model cannot be null or empty");
        }
        
        if (isActive == null) {
            throw new IllegalStateException("IsActive cannot be null");
        }
    }
    
    /**
     * Validates the IMEI field.
     *
     * @throws DomainInvalidImeiException if IMEI is invalid
     */
    private void validateImei() {
        isValidImei(this.imei);
    }
    
    /**
     * Validates timestamp fields.
     *
     * @throws IllegalStateException if timestamps are invalid
     */
    private void validateTimestamps() {
        if (registrationTimestamp == null) {
            throw new IllegalStateException("Registration timestamp cannot be null");
        }
        
        if (lastCommunicationTimestamp != null && 
            lastCommunicationTimestamp.isBefore(registrationTimestamp)) {
            throw new IllegalStateException("Last communication timestamp cannot be before registration timestamp");
        }
    }
    
    /**
     * Validates IMEI using Luhn algorithm.
     *
     * @param imei the IMEI to validate
     * @return true if checksum is valid
     */
    private boolean isValidLuhnChecksum(String imei) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = imei.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(imei.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    // Getters and setters
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; validate(); }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    public Instant getRegistrationTimestamp() { return registrationTimestamp; }
    public void setRegistrationTimestamp(Instant registrationTimestamp) { this.registrationTimestamp = registrationTimestamp; }
    public Instant getLastCommunicationTimestamp() { return lastCommunicationTimestamp; }
    public void setLastCommunicationTimestamp(Instant lastCommunicationTimestamp) { this.lastCommunicationTimestamp = lastCommunicationTimestamp; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public boolean isActive() { return this.isActive; }
}