package ai.shreds.shared.value_objects;

import java.time.LocalDateTime;

/**
 * Value object representing parsed GPS data.
 */
public class SharedParsedGpsData {
    private final double latitude;
    private final double longitude;
    private final double altitude;
    private final double speed;
    private final double heading;
    private final int satellites;
    private final double hdop;
    private final LocalDateTime timestamp;
    private final String deviceId;

    public SharedParsedGpsData(double latitude, double longitude, double altitude,
                               double speed, double heading, int satellites,
                               double hdop, LocalDateTime timestamp, String deviceId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.heading = heading;
        this.satellites = satellites;
        this.hdop = hdop;
        this.timestamp = timestamp;
        this.deviceId = deviceId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHeading() {
        return heading;
    }

    public int getSatellites() {
        return satellites;
    }

    public double getHdop() {
        return hdop;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDeviceId() {
        return deviceId;
    }
}