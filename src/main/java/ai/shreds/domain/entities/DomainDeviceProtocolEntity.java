package ai.shreds.domain.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Domain entity representing a device protocol configuration.
 * This entity encapsulates protocol information, validation logic, and business rules.
 */
public class DomainDeviceProtocolEntity {
    
    private Long id;
    private SharedProtocolTypeEnum protocolType;
    private String protocolVersion;
    private Integer defaultPort;
    private String messageTemplate;
    private Boolean isActive;
    private Set<String> supportedManufacturers;
    
    /**
     * Constructs a new DomainDeviceProtocolEntity with the specified parameters.
     */
    public DomainDeviceProtocolEntity(Long id, SharedProtocolTypeEnum protocolType, 
                                    String protocolVersion, Integer defaultPort, 
                                    String messageTemplate, Boolean isActive, 
                                    Set<String> supportedManufacturers) {
        this.id = id;
        this.protocolType = protocolType;
        this.protocolVersion = protocolVersion;
        this.defaultPort = defaultPort;
        this.messageTemplate = messageTemplate;
        this.isActive = isActive;
        this.supportedManufacturers = supportedManufacturers != null ? new HashSet<>(supportedManufacturers) : new HashSet<>();
        validate();
    }
    
    /**
     * Checks if the specified manufacturer is supported by this protocol.
     * 
     * @param manufacturer the manufacturer name to check
     * @return true if manufacturer is supported, false otherwise
     */
    public boolean isManufacturerSupported(String manufacturer) {
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            return false;
        }
        return supportedManufacturers.contains(manufacturer.trim().toUpperCase());
    }
    
    /**
     * Validates if the default port is within valid range.
     * 
     * @return true if port is valid, false otherwise
     */
    public boolean validatePort() {
        return defaultPort != null && defaultPort >= 1 && defaultPort <= 65535;
    }
    
    /**
     * Converts this protocol entity to a configuration map.
     * 
     * @return configuration map with protocol settings
     */
    public Map<String, String> toConfiguration() {
        Map<String, String> config = new HashMap<>();
        
        if (protocolType != null) {
            config.put("protocolType", protocolType.name());
        }
        if (protocolVersion != null) {
            config.put("protocolVersion", protocolVersion);
        }
        if (defaultPort != null) {
            config.put("defaultPort", defaultPort.toString());
        }
        if (messageTemplate != null) {
            config.put("messageTemplate", messageTemplate);
        }
        if (isActive != null) {
            config.put("isActive", isActive.toString());
        }
        if (!supportedManufacturers.isEmpty()) {
            config.put("supportedManufacturers", String.join(",", supportedManufacturers));
        }
        
        return config;
    }
    
    /**
     * Validates the entire entity state.
     * 
     * @throws IllegalStateException if entity state is invalid
     */
    public void validate() {
        validateProtocolSpecifics();
        validateManufacturers();
        validatePortRange();
        
        if (isActive == null) {
            throw new IllegalStateException("IsActive cannot be null");
        }
    }
    
    /**
     * Validates protocol-specific fields.
     * 
     * @throws IllegalStateException if protocol specifics are invalid
     */
    private void validateProtocolSpecifics() {
        if (protocolType == null) {
            throw new IllegalStateException("Protocol type cannot be null");
        }
        
        if (protocolType == SharedProtocolTypeEnum.UNKNOWN) {
            throw new IllegalStateException("Protocol type cannot be UNKNOWN");
        }
        
        if (protocolVersion == null || protocolVersion.trim().isEmpty()) {
            throw new IllegalStateException("Protocol version cannot be null or empty");
        }
        
        if (messageTemplate == null || messageTemplate.trim().isEmpty()) {
            throw new IllegalStateException("Message template cannot be null or empty");
        }
    }
    
    /**
     * Validates the supported manufacturers.
     * 
     * @throws IllegalStateException if manufacturers are invalid
     */
    private void validateManufacturers() {
        if (supportedManufacturers == null) {
            throw new IllegalStateException("Supported manufacturers cannot be null");
        }
        
        for (String manufacturer : supportedManufacturers) {
            if (manufacturer == null || manufacturer.trim().isEmpty()) {
                throw new IllegalStateException("Manufacturer name cannot be null or empty");
            }
        }
    }
    
    /**
     * Validates the port range.
     * 
     * @throws IllegalStateException if port is invalid
     */
    private void validatePortRange() {
        if (!validatePort()) {
            throw new IllegalStateException("Default port must be between 1 and 65535");
        }
    }
    
    /**
     * Adds a manufacturer to the supported manufacturers set.
     * 
     * @param manufacturer the manufacturer to add
     */
    public void addSupportedManufacturer(String manufacturer) {
        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            supportedManufacturers.add(manufacturer.trim().toUpperCase());
        }
    }
    
    /**
     * Removes a manufacturer from the supported manufacturers set.
     * 
     * @param manufacturer the manufacturer to remove
     */
    public void removeSupportedManufacturer(String manufacturer) {
        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            supportedManufacturers.remove(manufacturer.trim().toUpperCase());
        }
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SharedProtocolTypeEnum getProtocolType() { return protocolType; }
    public void setProtocolType(SharedProtocolTypeEnum protocolType) { this.protocolType = protocolType; }
    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    public Integer getDefaultPort() { return defaultPort; }
    public void setDefaultPort(Integer defaultPort) { this.defaultPort = defaultPort; }
    public String getMessageTemplate() { return messageTemplate; }
    public void setMessageTemplate(String messageTemplate) { this.messageTemplate = messageTemplate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Set<String> getSupportedManufacturers() { return new HashSet<>(supportedManufacturers); }
    public void setSupportedManufacturers(Set<String> supportedManufacturers) { 
        this.supportedManufacturers = supportedManufacturers != null ? new HashSet<>(supportedManufacturers) : new HashSet<>(); 
    }
}