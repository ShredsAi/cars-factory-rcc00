package ai.shreds.application.exceptions;

/**
 * Thrown when a requested application resource cannot be found.
 */
public class ApplicationResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a new ApplicationResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApplicationResourceNotFoundException(String message) {
        super(message);
    }
}
