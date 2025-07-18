package ai.shreds.domain.exceptions;

/**
 * Exception thrown when data integrity validation fails in domain entities.
 */
public class DomainDataIntegrityException extends RuntimeException {
    private final String field;
    
    /**
     * Constructs a new DomainDataIntegrityException with the specified field and message.
     * 
     * @param field the field that failed validation
     * @param message the detail message
     */
    public DomainDataIntegrityException(String field, String message) {
        super(message);
        this.field = field;
    }
    
    /**
     * Gets the field that failed validation.
     * 
     * @return the field name
     */
    public String getField() {
        return field;
    }
    
    @Override
    public String getMessage() {
        return "Data integrity violation in field '" + field + "': " + super.getMessage();
    }
}