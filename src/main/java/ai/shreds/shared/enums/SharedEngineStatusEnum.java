package ai.shreds.shared.enums;

/**
 * Enum representing the possible states of an engine.
 */
public enum SharedEngineStatusEnum {
    RUNNING,
    IDLE,
    STOPPED,
    ERROR,
    UNKNOWN;

    /**
     * Converts a string to an engine status enum value.
     *
     * @param status The status string to convert
     * @return The corresponding SharedEngineStatusEnum value
     */
    public static SharedEngineStatusEnum fromString(String status) {
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}