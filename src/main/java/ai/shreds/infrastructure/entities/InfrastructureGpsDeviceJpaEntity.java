package ai.shreds.infrastructure.entities;

import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * JPA entity representing a GPS device in the database.
 * Maps to the 'gps_device' table and provides conversion methods to/from domain entities.
 */
@Entity
@Table(name = "gps_device")
public class InfrastructureGpsDeviceJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "imei", nullable = false, unique = true, length = 15)
    private String imei;
    
    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;
    
    @Column(name = "model", nullable = false, length = 100)
    private String model;
    
    @Column(name = "protocol_id")
    private Long protocolId;
    
    @Column(name = "protocol_version", length = 50)
    private String protocolVersion;
    
    @Column(name = "registration_timestamp", nullable = false)
    private Instant registrationTimestamp;
    
    @Column(name = "last_communication_timestamp")
    private Instant lastCommunicationTimestamp;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", insertable = false, updatable = false)
    private InfrastructureDeviceProtocolJpaEntity protocol;
    
    /**
     * Default constructor for JPA.
     */
    public InfrastructureGpsDeviceJpaEntity() {
    }
    
    /**
     * Constructor with all fields.
     */
    public InfrastructureGpsDeviceJpaEntity(String imei, String manufacturer, String model, 
                                           Long protocolId, String protocolVersion, 
                                           Instant registrationTimestamp, Instant lastCommunicationTimestamp, 
                                           Boolean isActive) {
        this.imei = imei;
        this.manufacturer = manufacturer;
        this.model = model;
        this.protocolId = protocolId;
        this.protocolVersion = protocolVersion;
        this.registrationTimestamp = registrationTimestamp;
        this.lastCommunicationTimestamp = lastCommunicationTimestamp;
        this.isActive = isActive;
    }
    
    /**
     * Converts this JPA entity to a domain entity.
     * 
     * @return the corresponding domain entity
     */
    public DomainGPSDeviceEntity toDomainEntity() {
        return new DomainGPSDeviceEntity(
            this.imei,
            this.manufacturer,
            this.model,
            this.protocolVersion,
            this.registrationTimestamp,
            this.lastCommunicationTimestamp,
            this.isActive
        );
    }
    
    /**
     * Creates a JPA entity from a domain entity.
     * 
     * @param domain the domain entity to convert
     * @return the corresponding JPA entity
     */
    public static InfrastructureGpsDeviceJpaEntity fromDomainEntity(DomainGPSDeviceEntity domain) {
        return new InfrastructureGpsDeviceJpaEntity(
            domain.getImei(),
            domain.getManufacturer(),
            domain.getModel(),
            null, // protocolId will be set separately
            domain.getProtocolVersion(),
            domain.getRegistrationTimestamp(),
            domain.getLastCommunicationTimestamp(),
            domain.getIsActive()
        );
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }
    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    public Instant getRegistrationTimestamp() { return registrationTimestamp; }
    public void setRegistrationTimestamp(Instant registrationTimestamp) { this.registrationTimestamp = registrationTimestamp; }
    public Instant getLastCommunicationTimestamp() { return lastCommunicationTimestamp; }
    public void setLastCommunicationTimestamp(Instant lastCommunicationTimestamp) { this.lastCommunicationTimestamp = lastCommunicationTimestamp; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public InfrastructureDeviceProtocolJpaEntity getProtocol() { return protocol; }
    public void setProtocol(InfrastructureDeviceProtocolJpaEntity protocol) { this.protocol = protocol; }
}