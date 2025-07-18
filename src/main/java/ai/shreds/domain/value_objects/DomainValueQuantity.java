package ai.shreds.domain.value_objects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing a quantity with its unit of measure.
 * This is immutable and ensures type safety for quantities.
 */
public final class DomainValueQuantity {
    
    private static final int DECIMAL_PLACES = 4;
    private final BigDecimal value;
    private final String unit;
    
    private DomainValueQuantity(BigDecimal value, String unit) {
        this.value = Objects.requireNonNull(value, "Quantity value cannot be null").setScale(DECIMAL_PLACES, RoundingMode.HALF_UP);
        this.unit = Objects.requireNonNull(unit, "Quantity unit cannot be null");
        
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity value cannot be negative");
        }
        
        if (unit.trim().isEmpty()) {
            throw new IllegalArgumentException("Quantity unit cannot be empty");
        }
    }
    
    /**
     * Create a quantity with value and unit
     */
    public static DomainValueQuantity of(BigDecimal value, String unit) {
        return new DomainValueQuantity(value, unit.trim().toUpperCase());
    }
    
    /**
     * Create a quantity with double value and unit
     */
    public static DomainValueQuantity of(double value, String unit) {
        return new DomainValueQuantity(BigDecimal.valueOf(value), unit.trim().toUpperCase());
    }
    
    /**
     * Create a quantity with string value and unit
     */
    public static DomainValueQuantity of(String value, String unit) {
        Objects.requireNonNull(value, "Quantity value string cannot be null");
        try {
            return new DomainValueQuantity(new BigDecimal(value), unit.trim().toUpperCase());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid quantity value format: " + value, e);
        }
    }
    
    /**
     * Create a zero quantity with specified unit
     */
    public static DomainValueQuantity zero(String unit) {
        return new DomainValueQuantity(BigDecimal.ZERO, unit.trim().toUpperCase());
    }
    
    /**
     * Add another quantity (must have same unit)
     */
    public DomainValueQuantity add(DomainValueQuantity other) {
        validateSameUnit(other);
        return new DomainValueQuantity(this.value.add(other.value), this.unit);
    }
    
    /**
     * Subtract another quantity (must have same unit)
     */
    public DomainValueQuantity subtract(DomainValueQuantity other) {
        validateSameUnit(other);
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Subtraction result cannot be negative");
        }
        return new DomainValueQuantity(result, this.unit);
    }
    
    /**
     * Check if this quantity is greater than another
     */
    public boolean isGreaterThan(DomainValueQuantity other) {
        validateSameUnit(other);
        return this.value.compareTo(other.value) > 0;
    }
    
    /**
     * Check if this quantity is greater than or equal to another
     */
    public boolean isGreaterThanOrEqual(DomainValueQuantity other) {
        validateSameUnit(other);
        return this.value.compareTo(other.value) >= 0;
    }
    
    /**
     * Check if this quantity is less than another
     */
    public boolean isLessThan(DomainValueQuantity other) {
        validateSameUnit(other);
        return this.value.compareTo(other.value) < 0;
    }
    
    /**
     * Check if this quantity is zero
     */
    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Check if this quantity is positive
     */
    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }
    
    private void validateSameUnit(DomainValueQuantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new IllegalArgumentException("Cannot operate on quantities with different units: " + this.unit + " and " + other.unit);
        }
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public String getUnit() {
        return unit;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainValueQuantity that = (DomainValueQuantity) o;
        return value.equals(that.value) && unit.equals(that.unit);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
    
    @Override
    public String toString() {
        return value + " " + unit;
    }
}