package ai.shreds.domain.value_objects;

import java.util.Objects;

public class DomainValueReservationStatus {
    public static final String ACTIVE = "ACTIVE";
    public static final String CONSUMED = "CONSUMED";
    public static final String EXPIRED = "EXPIRED";
    public static final String CANCELLED = "CANCELLED";

    private final String value;

    private DomainValueReservationStatus(String value) {
        validateStatus(value);
        this.value = value;
    }

    private void validateStatus(String value) {
        if (value == null ||
            (!value.equals(ACTIVE) && !value.equals(CONSUMED) && !value.equals(EXPIRED) && !value.equals(CANCELLED))) {
            throw new IllegalArgumentException("Invalid reservation status: " + value);
        }
    }

    public static DomainValueReservationStatus active() {
        return new DomainValueReservationStatus(ACTIVE);
    }

    public static DomainValueReservationStatus consumed() {
        return new DomainValueReservationStatus(CONSUMED);
    }

    public static DomainValueReservationStatus expired() {
        return new DomainValueReservationStatus(EXPIRED);
    }

    public static DomainValueReservationStatus cancelled() {
        return new DomainValueReservationStatus(CANCELLED);
    }

    public static DomainValueReservationStatus from(String value) {
        return new DomainValueReservationStatus(value);
    }

    public boolean isActive() {
        return ACTIVE.equals(value);
    }

    public boolean isConsumed() {
        return CONSUMED.equals(value);
    }

    public boolean isExpired() {
        return EXPIRED.equals(value);
    }

    public boolean isCancelled() {
        return CANCELLED.equals(value);
    }

    public boolean allowsModification() {
        return isActive();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainValueReservationStatus)) return false;
        DomainValueReservationStatus that = (DomainValueReservationStatus) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}