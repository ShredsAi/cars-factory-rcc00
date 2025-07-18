package ai.shreds.domain.value_objects;

import ai.shreds.shared.enums.SharedEngineStatusEnum;
import java.util.Map;
import java.util.HashMap;

/**
 * Domain value object representing telemetry data from GPS devices.
 * This immutable object contains battery level, signal strength, engine status, and other sensor data.
 */
public class DomainTelemetryData {
    
    private final Integer batteryLevel;
    private final Integer signalStrength;
    private final SharedEngineStatusEnum engineStatus;
    private final Double fuelLevel;
    private final Double temperature;
    private final Long mileage;
    private final Map<String, Boolean> digitalInputs;
    private final Map<String, Double> analogInputs;
    
    /**
     * Constructs a new DomainTelemetryData with the specified parameters.
     */
    public DomainTelemetryData(Integer batteryLevel, Integer signalStrength, 
                             SharedEngineStatusEnum engineStatus, Double fuelLevel, 
                             Double temperature, Long mileage, 
                             Map<String, Boolean> digitalInputs, Map<String, Double> analogInputs) {
        this.batteryLevel = batteryLevel;
        this.signalStrength = signalStrength;
        this.engineStatus = engineStatus;
        this.fuelLevel = fuelLevel;
        this.temperature = temperature;
        this.mileage = mileage;
        this.digitalInputs = digitalInputs != null ? new HashMap<>(digitalInputs) : new HashMap<>();
        this.analogInputs = analogInputs != null ? new HashMap<>(analogInputs) : new HashMap<>();
    }
    
    /**
     * Validates if the telemetry data is within valid ranges and requirements.
     * 
     * @return true if telemetry data is valid, false otherwise
     */
    public boolean isValid() {
        return validateBatteryLevel() && validateFuelLevel() && validateTemperature() && 
               engineStatus != null && mileage != null && mileage >= 0;
    }
    
    /**
     * Validates battery level is within valid range (0 to 100).
     * 
     * @return true if battery level is valid
     */
    private boolean validateBatteryLevel() {
        return batteryLevel == null || (batteryLevel >= 0 && batteryLevel <= 100);
    }
    
    /**
     * Validates fuel level is within valid range (0 to 100).
     * 
     * @return true if fuel level is valid
     */
    private boolean validateFuelLevel() {
        return fuelLevel == null || (fuelLevel >= 0.0 && fuelLevel <= 100.0);
    }
    
    /**
     * Validates temperature is within reasonable range (-50 to 100 Celsius).
     * 
     * @return true if temperature is valid
     */
    private boolean validateTemperature() {
        return temperature == null || (temperature >= -50.0 && temperature <= 100.0);
    }
    
    // Getters
    public Integer getBatteryLevel() { return batteryLevel; }
    public Integer getSignalStrength() { return signalStrength; }
    public SharedEngineStatusEnum getEngineStatus() { return engineStatus; }
    public Double getFuelLevel() { return fuelLevel; }
    public Double getTemperature() { return temperature; }
    public Long getMileage() { return mileage; }
    public Map<String, Boolean> getDigitalInputs() { return new HashMap<>(digitalInputs); }
    public Map<String, Double> getAnalogInputs() { return new HashMap<>(analogInputs); }
}