package ai.shreds.infrastructure.external_services;

import ai.shreds.domain.entities.DomainDeviceProtocolEntity;
import ai.shreds.domain.entities.DomainGPSDeviceEntity;
import ai.shreds.domain.entities.DomainRawGPSDataEntity;
import ai.shreds.infrastructure.entities.InfrastructureDeviceProtocolJpaEntity;
import ai.shreds.infrastructure.entities.InfrastructureGpsDeviceJpaEntity;
import ai.shreds.infrastructure.repositories.InfrastructureJPARawGPSDataJPAEntity;
import ai.shreds.shared.enums.SharedProtocolTypeEnum;
import org.springframework.stereotype.Component;

/**
 * Utility class for mapping between domain entities and JPA entities.
 * Provides bi-directional conversion methods for all entity types.
 */
@Component
public class InfrastructureEntityMapper {
    
    /**
     * Converts a JPA GPS device entity to a domain GPS device entity.
     *
     * @param jpaEntity the JPA entity to convert
     * @return the corresponding domain entity
     */
    public DomainGPSDeviceEntity toDomainGPSDevice(InfrastructureGpsDeviceJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return jpaEntity.toDomainEntity();
    }
    
    /**
     * Converts a domain GPS device entity to a JPA GPS device entity.
     *
     * @param domainEntity the domain entity to convert
     * @return the corresponding JPA entity
     */
    public InfrastructureGpsDeviceJpaEntity toJPAGPSDevice(DomainGPSDeviceEntity domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        
        return InfrastructureGpsDeviceJpaEntity.fromDomainEntity(domainEntity);
    }
    
    /**
     * Converts a JPA device protocol entity to a domain device protocol entity.
     *
     * @param jpaEntity the JPA entity to convert
     * @return the corresponding domain entity
     */
    public DomainDeviceProtocolEntity toDomainDeviceProtocol(InfrastructureDeviceProtocolJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return jpaEntity.toDomainEntity();
    }
    
    /**
     * Converts a domain device protocol entity to a JPA device protocol entity.
     *
     * @param domainEntity the domain entity to convert
     * @return the corresponding JPA entity
     */
    public InfrastructureDeviceProtocolJpaEntity toJPADeviceProtocol(DomainDeviceProtocolEntity domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        
        return InfrastructureDeviceProtocolJpaEntity.fromDomainEntity(domainEntity);
    }
    
    /**
     * Converts a JPA raw GPS data entity to a domain raw GPS data entity.
     *
     * @param jpaEntity the JPA entity to convert
     * @return the corresponding domain entity
     */
    public DomainRawGPSDataEntity toDomainRawGPSData(InfrastructureJPARawGPSDataJPAEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return jpaEntity.toDomainEntity();
    }
    
    /**
     * Converts a domain raw GPS data entity to a JPA raw GPS data entity.
     *
     * @param domainEntity the domain entity to convert
     * @return the corresponding JPA entity
     */
    public InfrastructureJPARawGPSDataJPAEntity toJPARawGPSData(DomainRawGPSDataEntity domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        
        return InfrastructureJPARawGPSDataJPAEntity.fromDomainEntity(domainEntity);
    }
    
    /**
     * Converts a protocol type string to SharedProtocolTypeEnum.
     *
     * @param protocolTypeString the protocol type as string
     * @return the corresponding SharedProtocolTypeEnum
     */
    public SharedProtocolTypeEnum toProtocolTypeEnum(String protocolTypeString) {
        if (protocolTypeString == null || protocolTypeString.trim().isEmpty()) {
            return SharedProtocolTypeEnum.UNKNOWN;
        }
        
        try {
            return SharedProtocolTypeEnum.valueOf(protocolTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SharedProtocolTypeEnum.UNKNOWN;
        }
    }
    
    /**
     * Converts a SharedProtocolTypeEnum to string.
     *
     * @param protocolType the protocol type enum
     * @return the corresponding string representation
     */
    public String toProtocolTypeString(SharedProtocolTypeEnum protocolType) {
        if (protocolType == null) {
            return SharedProtocolTypeEnum.UNKNOWN.name();
        }
        
        return protocolType.name();
    }
}