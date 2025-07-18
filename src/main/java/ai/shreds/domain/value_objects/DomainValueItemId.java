package ai.shreds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public class DomainValueItemId {
    private final UUID value;

    private DomainValueItemId(UUID value) {
        this.value = Objects.requireNonNull(value, "Item ID cannot be null");
    }

    public static DomainValueItemId from(UUID value) {
        return new DomainValueItemId(value);
    }

    public static DomainValueItemId from(String value) {
        return new DomainValueItemId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainValueItemId)) return false;
        DomainValueItemId that = (DomainValueItemId) o;
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