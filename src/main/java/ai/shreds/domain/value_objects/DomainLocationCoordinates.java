package ai.shreds.domain.value_objects;

import java.time.Instant;

/**
 * Domain value object representing GPS location coordinates and related telemetry data.
 * This immutable object contains latitude, longitude, altitude, speed, heading, and other location-related metrics.
 */
public class DomainLocationCoordinates {
    
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Double speed;
    private final Integer heading;
    private final Integer satelliteCount;
    private final Instant timestamp;
    private final Double horizontalAccuracy;
    
    /**
     * Constructs a new DomainLocationCoordinates with the specified parameters.
     */
    public DomainLocationCoordinates(Double latitude, Double longitude, Double altitude, 
                                   Double speed, Integer heading, Integer satelliteCount, 
                                   Instant timestamp, Double horizontalAccuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.heading = heading;
        this.satelliteCount = satelliteCount;
        this.timestamp = timestamp;
        this.horizontalAccuracy = horizontalAccuracy;
    }
    
    /**
     * Validates if the coordinates are within valid ranges and requirements.
     * 
     * @return true if coordinates are valid, false otherwise
     */
    public boolean isValid() {
        return validateLatitude() && validateLongitude() && validateHeading() && 
               timestamp != null && satelliteCount != null && satelliteCount >= 0;
    }
    
    /**
     * Calculates a quality score for the location data based on various factors.
     * 
     * @return quality score from 0 to 100
     */
    public Integer calculateQualityScore() {
        int score = 0;
        
        // Satellite count contribution (0-40 points)
        if (satelliteCount != null) {
            score += Math.min(satelliteCount * 5, 40);
        }
        
        // Horizontal accuracy contribution (0-30 points)
        if (horizontalAccuracy != null && horizontalAccuracy > 0) {
            if (horizontalAccuracy <= 5) score += 30;
            else if (horizontalAccuracy <= 10) score += 20;
            else if (horizontalAccuracy <= 20) score += 10;
        }
        
        // Valid coordinates contribution (0-30 points)
        if (validateLatitude() && validateLongitude()) {
            score += 30;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * Validates latitude is within valid range (-90 to 90).
     * 
     * @return true if latitude is valid
     */
    private boolean validateLatitude() {
        return latitude != null && latitude >= -90.0 && latitude <= 90.0;
    }
    
    /**
     * Validates longitude is within valid range (-180 to 180).
     * 
     * @return true if longitude is valid
     */
    private boolean validateLongitude() {
        return longitude != null && longitude >= -180.0 && longitude <= 180.0;
    }
    
    /**
     * Validates heading is within valid range (0 to 360).
     * 
     * @return true if heading is valid
     */
    private boolean validateHeading() {
        return heading == null || (heading >= 0 && heading <= 360);
    }
    
    // Getters
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getAltitude() { return altitude; }
    public Double getSpeed() { return speed; }
    public Integer getHeading() { return heading; }
    public Integer getSatelliteCount() { return satelliteCount; }
    public Instant getTimestamp() { return timestamp; }
    public Double getHorizontalAccuracy() { return horizontalAccuracy; }
}