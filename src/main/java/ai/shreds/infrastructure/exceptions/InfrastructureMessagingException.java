package ai.shreds.infrastructure.exceptions;

public class InfrastructureMessagingException extends RuntimeException {
    
    private final String channelName;
    private final String messageId;
    
    public InfrastructureMessagingException(String message, String channelName, String messageId) {
        super(message);
        this.channelName = channelName;
        this.messageId = messageId;
    }
    
    public InfrastructureMessagingException(String message, String channelName, String messageId, Throwable cause) {
        super(message, cause);
        this.channelName = channelName;
        this.messageId = messageId;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    @Override
    public String toString() {
        return String.format("InfrastructureMessagingException{channelName='%s', messageId='%s', message='%s'}", 
                           channelName, messageId, getMessage());
    }
}