package ai.shreds.domain.value_objects;

import java.util.Objects;
import java.util.UUID;

public class DomainValueUserId {
    private final UUID value;

    private DomainValueUserId(UUID value) {
        this.value = Objects.requireNonNull(value, "User ID cannot be null");
    }

    public static DomainValueUserId from(UUID value) {
        return new DomainValueUserId(value);
    }

    public static DomainValueUserId from(String value) {
        return new DomainValueUserId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainValueUserId)) return false;
        DomainValueUserId that = (DomainValueUserId) o;
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