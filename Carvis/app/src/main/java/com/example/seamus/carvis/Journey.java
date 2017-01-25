package com.example.seamus.carvis;

/**
 * Created by Seamus on 24/01/2017.
 */

public class Journey {

    public Journey(){
        longitude="";
        latitude="";
        currentSpeed=0.0;
        speedLimit="";

    }
    private String longitude;
    private String latitude;
    private double  currentSpeed;
    private String speedLimit;
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


    public String getSpeedLimit() {
        return speedLimit;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }
}
