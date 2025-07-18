package ai.shreds.infrastructure.entities;

import ai.shreds.domain.entities.DomainDeviceProtocolEntity;
import ai.shreds.domain.entities.SharedProtocolTypeEnum;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA entity representing a device protocol in the database.
 * Maps to the 'device_protocol' table and provides conversion methods to/from domain entities.
 */
@Entity
@Table(name = "device_protocol")
public class InfrastructureDeviceProtocolJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "protocol_type", nullable = false, length = 50)
    private String protocolType;
    
    @Column(name = "protocol_version", nullable = false, length = 50)
    private String protocolVersion;
    
    @Column(name = "default_port", nullable = false)
    private Integer defaultPort;
    
    @Column(name = "message_template", nullable = false, length = 512)
    private String messageTemplate;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @OneToMany(mappedBy = "protocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InfrastructureProtocolSupportedManufacturerJpaEntity> supportedManufacturers = new HashSet<>();
    
    /**
     * Default constructor for JPA.
     */
    public InfrastructureDeviceProtocolJpaEntity() {
    }
    
    /**
     * Constructor with all fields except supportedManufacturers.
     */
    public InfrastructureDeviceProtocolJpaEntity(Long id, String protocolType, String protocolVersion,
                                              Integer defaultPort, String messageTemplate, Boolean isActive) {
        this.id = id;
        this.protocolType = protocolType;
        this.protocolVersion = protocolVersion;
        this.defaultPort = defaultPort;
        this.messageTemplate = messageTemplate;
        this.isActive = isActive;
    }
    
    /**
     * Adds a supported manufacturer to this protocol.
     * 
     * @param manufacturerName the manufacturer name to add
     */
    public void addSupportedManufacturer(String manufacturerName) {
        InfrastructureProtocolSupportedManufacturerJpaEntity manufacturerEntity = 
            new InfrastructureProtocolSupportedManufacturerJpaEntity(this, manufacturerName);
        supportedManufacturers.add(manufacturerEntity);
    }
    
    /**
     * Removes a supported manufacturer from this protocol.
     * 
     * @param manufacturerName the manufacturer name to remove
     */
    public void removeSupportedManufacturer(String manufacturerName) {
        supportedManufacturers.removeIf(manufacturer -> 
            manufacturer.getManufacturerName().equals(manufacturerName));
    }
    
    /**
     * Converts this JPA entity to a domain entity.
     * 
     * @return the corresponding domain entity
     */
    public DomainDeviceProtocolEntity toDomainEntity() {
        Set<String> manufacturers = supportedManufacturers.stream()
            .map(InfrastructureProtocolSupportedManufacturerJpaEntity::getManufacturerName)
            .collect(Collectors.toSet());
        
        return new DomainDeviceProtocolEntity(
            this.id,
            SharedProtocolTypeEnum.valueOf(this.protocolType),
            this.protocolVersion,
            this.defaultPort,
            this.messageTemplate,
            this.isActive,
            manufacturers
        );
    }
    
    /**
     * Creates a JPA entity from a domain entity.
     * 
     * @param domain the domain entity to convert
     * @return the corresponding JPA entity
     */
    public static InfrastructureDeviceProtocolJpaEntity fromDomainEntity(DomainDeviceProtocolEntity domain) {
        InfrastructureDeviceProtocolJpaEntity jpaEntity = new InfrastructureDeviceProtocolJpaEntity(
            domain.getId(),
            domain.getProtocolType().name(),
            domain.getProtocolVersion(),
            domain.getDefaultPort(),
            domain.getMessageTemplate(),
            domain.getIsActive()
        );
        
        // Add supported manufacturers
        for (String manufacturer : domain.getSupportedManufacturers()) {
            jpaEntity.addSupportedManufacturer(manufacturer);
        }
        
        return jpaEntity;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProtocolType() { return protocolType; }
    public void setProtocolType(String protocolType) { this.protocolType = protocolType; }
    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    public Integer getDefaultPort() { return defaultPort; }
    public void setDefaultPort(Integer defaultPort) { this.defaultPort = defaultPort; }
    public String getMessageTemplate() { return messageTemplate; }
    public void setMessageTemplate(String messageTemplate) { this.messageTemplate = messageTemplate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Set<InfrastructureProtocolSupportedManufacturerJpaEntity> getSupportedManufacturers() { return supportedManufacturers; }
    public void setSupportedManufacturers(Set<InfrastructureProtocolSupportedManufacturerJpaEntity> supportedManufacturers) { 
        this.supportedManufacturers = supportedManufacturers != null ? new HashSet<>(supportedManufacturers) : new HashSet<>(); 
    }
}