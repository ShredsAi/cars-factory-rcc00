package ai.shreds.domain.exceptions;

public class DomainDeviceNotAuthorizedException extends RuntimeException {
    private final String imei;

    public DomainDeviceNotAuthorizedException(String imei, String message) {
        super(message);
        this.imei = imei;
    }

    public DomainDeviceNotAuthorizedException(String imei, String message, Throwable cause) {
        super(message, cause);
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }
}
