package ai.shreds.application.exceptions;

/**
 * Thrown when an application-level conflict occurs, such as insufficient stock.
 */
public class ApplicationConflictException extends RuntimeException {
    /**
     * Constructs a new ApplicationConflictException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApplicationConflictException(String message) {
        super(message);
    }
}
