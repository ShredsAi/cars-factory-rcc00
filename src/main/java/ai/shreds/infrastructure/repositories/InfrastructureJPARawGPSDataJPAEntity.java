package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainRawGPSDataEntity;
import ai.shreds.shared.enums.SharedProtocolTypeEnum;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing raw GPS data in the database.
 * Maps to the 'raw_gps_data' table and provides conversion methods to/from domain entities.
 */
@Entity
@Table(name = "raw_gps_data")
public class InfrastructureJPARawGPSDataJPAEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "device_imei", nullable = false, length = 15)
    private String deviceImei;
    
    @Lob
    @Column(name = "raw_payload", nullable = false, columnDefinition = "bytea")
    private byte[] rawPayload;
    
    @Column(name = "protocol_type", nullable = false, length = 50)
    private String protocolType;
    
    @Column(name = "received_timestamp", nullable = false)
    private Instant receivedTimestamp;
    
    @Column(name = "source_address", nullable = false, length = 45)
    private String sourceAddress;
    
    @Column(name = "source_port", nullable = false)
    private Integer sourcePort;
    
    @Column(name = "data_length", nullable = false)
    private Integer dataLength;
    
    /**
     * Default constructor for JPA.
     */
    public InfrastructureJPARawGPSDataJPAEntity() {
    }
    
    /**
     * Constructor with all fields.
     */
    public InfrastructureJPARawGPSDataJPAEntity(UUID id, String deviceImei, byte[] rawPayload,
                                              String protocolType, Instant receivedTimestamp,
                                              String sourceAddress, Integer sourcePort, Integer dataLength) {
        this.id = id;
        this.deviceImei = deviceImei;
        this.rawPayload = rawPayload != null ? rawPayload.clone() : null;
        this.protocolType = protocolType;
        this.receivedTimestamp = receivedTimestamp;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.dataLength = dataLength;
    }
    
    /**
     * Maps SharedProtocolTypeEnum to DomainRawGPSDataEntity.ProtocolType
     */
    private DomainRawGPSDataEntity.ProtocolType mapProtocolType(String protocolType) {
        SharedProtocolTypeEnum sharedType = SharedProtocolTypeEnum.valueOf(protocolType);
        switch (sharedType) {
            case TCP:
            case UDP:
                return DomainRawGPSDataEntity.ProtocolType.UBLOX;
            case HTTP:
            case MQTT:
                return DomainRawGPSDataEntity.ProtocolType.NMEA;
            case COAP:
                return DomainRawGPSDataEntity.ProtocolType.GPX;
            default:
                return DomainRawGPSDataEntity.ProtocolType.UNKNOWN;
        }
    }
    
    /**
     * Converts this JPA entity to a domain entity.
     * 
     * @return the corresponding domain entity
     */
    public DomainRawGPSDataEntity toDomainEntity() {
        return new DomainRawGPSDataEntity(
            this.id,
            this.deviceImei,
            this.rawPayload,
            mapProtocolType(this.protocolType),
            this.receivedTimestamp,
            this.sourceAddress,
            this.sourcePort,
            this.dataLength
        );
    }
    
    /**
     * Creates a JPA entity from a domain entity.
     * 
     * @param domain the domain entity to convert
     * @return the corresponding JPA entity
     */
    public static InfrastructureJPARawGPSDataJPAEntity fromDomainEntity(DomainRawGPSDataEntity domain) {
        return new InfrastructureJPARawGPSDataJPAEntity(
            domain.getId(),
            domain.getDeviceImei(),
            domain.getRawPayload(),
            domain.getProtocolType().name(),
            domain.getReceivedTimestamp(),
            domain.getSourceAddress(),
            domain.getSourcePort(),
            domain.getDataLength()
        );
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getDeviceImei() { return deviceImei; }
    public void setDeviceImei(String deviceImei) { this.deviceImei = deviceImei; }
    public byte[] getRawPayload() { return rawPayload != null ? rawPayload.clone() : null; }
    public void setRawPayload(byte[] rawPayload) { this.rawPayload = rawPayload != null ? rawPayload.clone() : null; }
    public String getProtocolType() { return protocolType; }
    public void setProtocolType(String protocolType) { this.protocolType = protocolType; }
    public Instant getReceivedTimestamp() { return receivedTimestamp; }
    public void setReceivedTimestamp(Instant receivedTimestamp) { this.receivedTimestamp = receivedTimestamp; }
    public String getSourceAddress() { return sourceAddress; }
    public void setSourceAddress(String sourceAddress) { this.sourceAddress = sourceAddress; }
    public Integer getSourcePort() { return sourcePort; }
    public void setSourcePort(Integer sourcePort) { this.sourcePort = sourcePort; }
    public Integer getDataLength() { return dataLength; }
    public void setDataLength(Integer dataLength) { this.dataLength = dataLength; }
}