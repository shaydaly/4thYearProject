package com.carvis;

import android.content.Context;
import android.location.Location;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by Seamus on 26/02/2017.
 */

public class TemporarySpeedCamera {
    private double latitude, longitude;
    private String time;
    static LinkedHashSet<TemporarySpeedCamera> temporarySpeedCameras = new LinkedHashSet<>();

    public TemporarySpeedCamera(double latitude, double longitude, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static void addTemporaryCamera(TemporarySpeedCamera t){
        temporarySpeedCameras.add(t);
    }



    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static void addTemporaryCamera(String latitude, String longitude, String date, Context context){
        FirebaseApp.initializeApp(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("reportedSpeedCameras");



//        final HashMap<String, String> map1 = new HashMap<>();
//        map1.put("startLatitude", latitude);
//        map1.put("startLongitude", longitude);
//        //map1.put("time", String.valueOf(time));
//        map1.put("endLatitude", latitude);
//        map1.put("endLongitude", longitude);
//        //myRef.push().setValue(map1);

        Location cameraLocation = new Location("New Location");
        cameraLocation.setLatitude(Double.parseDouble(latitude));
        cameraLocation.setLongitude(Double.parseDouble(longitude));
        if(!checkCameraDistance(cameraLocation)) {
            myRef.push().setValue(new TemporarySpeedCamera(Double.parseDouble(latitude), Double.parseDouble(longitude), date));
        }
    }

    public static boolean checkCameraDistance(Location location) {
        Location cameraLocation;
        for (TemporarySpeedCamera t : temporarySpeedCameras) {
            cameraLocation = new Location("cameraLocation");
            cameraLocation.setLatitude(t.getLatitude());
            cameraLocation.setLongitude(t.getLongitude());

            if ((location.distanceTo(cameraLocation) / 1000) < 0.5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(latitude).
                        append(longitude).
                        append(time).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemporarySpeedCamera))
            return false;
        if (obj == this)
            return true;

        TemporarySpeedCamera rhs = (TemporarySpeedCamera) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(latitude, rhs.latitude).
                        append(longitude, rhs.longitude).
                        append(time, rhs.time).
                        isEquals();
    }

}
