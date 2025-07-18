package ai.shreds.shared.dtos;

import java.time.Instant;

public class SharedDeviceValidationResult {
    private String imei;
    private boolean authorized;
    private boolean active;
    private String manufacturer;
    private String model;
    private Instant lastCommunicationTimestamp;

    public SharedDeviceValidationResult() {
    }

    public SharedDeviceValidationResult(String imei,
                                        boolean authorized,
                                        boolean active,
                                        String manufacturer,
                                        String model,
                                        Instant lastCommunicationTimestamp) {
        this.imei = imei;
        this.authorized = authorized;
        this.active = active;
        this.manufacturer = manufacturer;
        this.model = model;
        this.lastCommunicationTimestamp = lastCommunicationTimestamp;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Instant getLastCommunicationTimestamp() {
        return lastCommunicationTimestamp;
    }

    public void setLastCommunicationTimestamp(Instant lastCommunicationTimestamp) {
        this.lastCommunicationTimestamp = lastCommunicationTimestamp;
    }
}
