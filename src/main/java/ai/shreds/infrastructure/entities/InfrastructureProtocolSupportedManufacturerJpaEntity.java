package ai.shreds.infrastructure.entities;

import jakarta.persistence.*;

/**
 * JPA entity representing a supported manufacturer for a device protocol.
 * Maps to the 'protocol_supported_manufacturer' table.
 */
@Entity
@Table(name = "protocol_supported_manufacturer", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"protocol_id", "manufacturer_name"}))
public class InfrastructureProtocolSupportedManufacturerJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "manufacturer_name", nullable = false, length = 100)
    private String manufacturerName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private InfrastructureDeviceProtocolJpaEntity protocol;
    
    /**
     * Default constructor for JPA.
     */
    public InfrastructureProtocolSupportedManufacturerJpaEntity() {
    }
    
    /**
     * Constructor with protocol and manufacturer name.
     * 
     * @param protocol the device protocol entity
     * @param manufacturerName the manufacturer name
     */
    public InfrastructureProtocolSupportedManufacturerJpaEntity(InfrastructureDeviceProtocolJpaEntity protocol, String manufacturerName) {
        this.protocol = protocol;
        this.manufacturerName = manufacturerName;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public InfrastructureDeviceProtocolJpaEntity getProtocol() { return protocol; }
    public void setProtocol(InfrastructureDeviceProtocolJpaEntity protocol) { this.protocol = protocol; }
}