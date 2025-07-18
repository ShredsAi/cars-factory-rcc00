package ai.shreds.infrastructure.mappers;

import ai.shreds.domain.entities.DomainComponentReservationEntity;
import ai.shreds.domain.value_objects.*;
import ai.shreds.infrastructure.entities.InfrastructureComponentReservationJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface InfrastructureComponentReservationMapper {

    InfrastructureComponentReservationMapper INSTANCE = Mappers.getMapper(InfrastructureComponentReservationMapper.class);

    @Mapping(source = "reservationId.value", target = "reservationId")
    @Mapping(source = "itemId.value", target = "itemId")
    @Mapping(source = "locationId.value", target = "locationId")
    @Mapping(source = "productionRunId.value", target = "productionRunId")
    @Mapping(source = "quantityReserved", target = "quantityReserved", qualifiedByName = "quantityToBigDecimal")
    @Mapping(source = "quantityReserved.unit", target = "quantityUnit")
    @Mapping(source = "reservedBy.value", target = "reservedBy")
    @Mapping(source = "reservedAt", target = "reservedAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "expiresAt", target = "expiresAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(source = "status.value", target = "status")
    @Mapping(target = "version", ignore = true)
    InfrastructureComponentReservationJpaEntity toJpaEntity(DomainComponentReservationEntity domainEntity);

    @Mapping(source = "reservationId", target = "reservationId", qualifiedByName = "uuidToReservationId")
    @Mapping(source = "itemId", target = "itemId", qualifiedByName = "uuidToItemId")
    @Mapping(source = "locationId", target = "locationId", qualifiedByName = "uuidToLocationId")
    @Mapping(source = "productionRunId", target = "productionRunId", qualifiedByName = "uuidToProductionRunId")
    @Mapping(target = "quantityReserved", source = "entity", qualifiedByName = "entityToQuantity")
    @Mapping(source = "reservedBy", target = "reservedBy", qualifiedByName = "uuidToUserId")
    @Mapping(source = "reservedAt", target = "reservedAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(source = "expiresAt", target = "expiresAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToStatus")
    DomainComponentReservationEntity toDomainEntity(InfrastructureComponentReservationJpaEntity entity);

    List<DomainComponentReservationEntity> toDomainEntities(List<InfrastructureComponentReservationJpaEntity> jpaEntities);

    @Named("quantityToBigDecimal")
    default BigDecimal quantityToBigDecimal(DomainValueQuantity quantity) {
        return quantity.getValue();
    }

    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    @Named("uuidToReservationId")
    default DomainValueReservationId uuidToReservationId(UUID uuid) {
        return DomainValueReservationId.from(uuid);
    }

    @Named("uuidToItemId")
    default DomainValueItemId uuidToItemId(UUID uuid) {
        return DomainValueItemId.from(uuid);
    }

    @Named("uuidToLocationId")
    default DomainValueLocationId uuidToLocationId(UUID uuid) {
        return DomainValueLocationId.from(uuid);
    }

    @Named("uuidToProductionRunId")
    default DomainValueProductionRunId uuidToProductionRunId(UUID uuid) {
        return DomainValueProductionRunId.from(uuid);
    }

    @Named("entityToQuantity")
    default DomainValueQuantity entityToQuantity(InfrastructureComponentReservationJpaEntity entity) {
        return DomainValueQuantity.of(entity.getQuantityReserved(), entity.getQuantityUnit());
    }

    @Named("uuidToUserId")
    default DomainValueUserId uuidToUserId(UUID uuid) {
        return DomainValueUserId.from(uuid);
    }

    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }

    @Named("stringToStatus")
    default DomainValueReservationStatus stringToStatus(String status) {
        return DomainValueReservationStatus.from(status);
    }
}