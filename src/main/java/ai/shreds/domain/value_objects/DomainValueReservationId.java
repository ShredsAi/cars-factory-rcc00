package ai.shreds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public class DomainValueReservationId {
    private final UUID value;

    private DomainValueReservationId(UUID value) {
        this.value = Objects.requireNonNull(value, "Reservation ID cannot be null");
    }

    public static DomainValueReservationId generate() {
        return new DomainValueReservationId(UUID.randomUUID());
    }

    public static DomainValueReservationId from(UUID value) {
        return new DomainValueReservationId(value);
    }

    public static DomainValueReservationId from(String value) {
        return new DomainValueReservationId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainValueReservationId)) return false;
        DomainValueReservationId that = (DomainValueReservationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}