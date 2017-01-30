package com.example.seamus.carvis;

/**
 * Created by Seamus on 26/01/2017.
 */

public class OverSpeedLimit {

    private String longitude, latitude, speedLimit;
    private double currentSpeed;

    public OverSpeedLimit(String longitude, String latitude, double currentSpeed, String speedLimit) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public String getSpeedLimit() {
        return speedLimit;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setSpeedLimit(String speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void InsertOverLimitDB(Journey j){
    }
}
