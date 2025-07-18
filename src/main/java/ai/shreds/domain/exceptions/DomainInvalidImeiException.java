package ai.shreds.domain.exceptions;

public class DomainInvalidImeiException extends RuntimeException {
    private final String imei;

    public DomainInvalidImeiException(String imei, String message) {
        super(message);
        this.imei = imei;
    }

    public DomainInvalidImeiException(String imei, String message, Throwable cause) {
        super(message, cause);
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }
}
