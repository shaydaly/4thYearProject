package com.carvis;

/**
 * Created by Seamus on 06/03/2017.
 */

public class RoadRecord {
    private String latitude, longitude;
    private int speedLimit;


    public RoadRecord(String latitude, String longitude, int speedLimit) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedLimit = speedLimit;
    }



    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }
}
