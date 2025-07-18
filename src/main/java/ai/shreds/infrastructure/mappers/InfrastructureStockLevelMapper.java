package ai.shreds.infrastructure.mappers;

import ai.shreds.domain.entities.DomainStockLevelEntity;
import ai.shreds.domain.value_objects.DomainValueItemId;
import ai.shreds.domain.value_objects.DomainValueLocationId;
import ai.shreds.domain.value_objects.DomainValueQuantity;
import ai.shreds.infrastructure.entities.InfrastructureStockLevelJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface InfrastructureStockLevelMapper {

    InfrastructureStockLevelMapper INSTANCE = Mappers.getMapper(InfrastructureStockLevelMapper.class);

    @Mapping(source = "itemId.value", target = "itemId")
    @Mapping(source = "locationId.value", target = "locationId")
    @Mapping(source = "quantityOnHand.value", target = "quantityOnHand")
    @Mapping(source = "reservedQuantity.value", target = "reservedQty")
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "version", ignore = true)
    InfrastructureStockLevelJpaEntity toJpaEntity(DomainStockLevelEntity domainEntity);

    @Mapping(source = "itemId", target = "itemId", qualifiedByName = "uuidToDomainItemId")
    @Mapping(source = "locationId", target = "locationId", qualifiedByName = "uuidToDomainLocationId")
    @Mapping(target = "quantityOnHand", expression = "java(DomainValueQuantity.of(jpaEntity.getQuantityOnHand(), \"PIECES\"))") // Assuming a default unit
    @Mapping(target = "reservedQuantity", expression = "java(DomainValueQuantity.of(jpaEntity.getReservedQty(), \"PIECES\"))") // Assuming a default unit
    DomainStockLevelEntity toDomainEntity(InfrastructureStockLevelJpaEntity jpaEntity);

    @Named("uuidToDomainItemId")
    default DomainValueItemId uuidToDomainItemId(UUID uuid) {
        return DomainValueItemId.from(uuid);
    }

    @Named("uuidToDomainLocationId")
    default DomainValueLocationId uuidToDomainLocationId(UUID uuid) {
        return DomainValueLocationId.from(uuid);
    }
}