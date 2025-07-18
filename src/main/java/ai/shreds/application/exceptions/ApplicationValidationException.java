package ai.shreds.application.exceptions;

/**
 * Thrown when application-level validation fails.
 */
public class ApplicationValidationException extends RuntimeException {
    /**
     * Constructs a new ApplicationValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApplicationValidationException(String message) {
        super(message);
    }
}
