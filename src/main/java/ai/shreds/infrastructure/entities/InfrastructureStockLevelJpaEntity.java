package ai.shreds.infrastructure.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_level")
@IdClass(InfrastructureStockLevelId.class)
@Getter
@Setter
public class InfrastructureStockLevelJpaEntity {

    @Id
    @Column(name = "item_id")
    private UUID itemId;

    @Id
    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "quantity_on_hand", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityOnHand;

    @Column(name = "reserved_qty", nullable = false, precision = 19, scale = 4)
    private BigDecimal reservedQty;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @Version
    private Long version;
}