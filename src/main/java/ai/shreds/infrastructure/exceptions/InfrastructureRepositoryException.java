package ai.shreds.infrastructure.exceptions;

/**
 * Exception thrown when repository operations fail in the infrastructure layer.
 * Provides detailed information about the operation that failed and the entity type involved.
 */
public class InfrastructureRepositoryException extends RuntimeException {
    
    private final String operation;
    private final String entityType;
    
    /**
     * Constructs a new InfrastructureRepositoryException with the specified details.
     * 
     * @param message the detail message
     * @param operation the repository operation that failed
     * @param entityType the type of entity involved in the operation
     */
    public InfrastructureRepositoryException(String message, String operation, String entityType) {
        super(message);
        this.operation = operation;
        this.entityType = entityType;
    }
    
    /**
     * Constructs a new InfrastructureRepositoryException with the specified details and cause.
     * 
     * @param message the detail message
     * @param operation the repository operation that failed
     * @param entityType the type of entity involved in the operation
     * @param cause the cause of the exception
     */
    public InfrastructureRepositoryException(String message, String operation, String entityType, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.entityType = entityType;
    }
    
    /**
     * Gets the repository operation that failed.
     * 
     * @return the operation name
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Gets the entity type involved in the failed operation.
     * 
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }
}