package com.carvis;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.LinkedHashSet;

/**
 * Created by Seamus on 07/04/2017.
 */

public class TrafficUpdate {
    double latitude,  longitude;
    String time;
    static LinkedHashSet<TrafficUpdate> trafficUpdates = new LinkedHashSet<>();

    public TrafficUpdate(double latitude, double longitude, String time) {
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


    public static void addTrafficUpdateToList(TrafficUpdate update){
        trafficUpdates.add(update);
    }

    public static void AddTrafficUpdate(double latitude, double longitude, String date, Context context){
        FirebaseApp.initializeApp(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("reportedTrafficIncident");

        Location updateLocation  = new Location("Traffic Location");
        updateLocation.setLatitude(latitude);
        updateLocation.setLongitude(longitude);
        if(!checkTrafficDistance(updateLocation)) {
            myRef.push().setValue(new TrafficUpdate(latitude, longitude, date));
            addTrafficUpdateToList(new TrafficUpdate(latitude, longitude, date));
        }
        else{

        }
    }

    public static boolean checkTrafficDistance(Location location){
        Location cameraLocation;
        for (TrafficUpdate t : trafficUpdates) {
            cameraLocation = new Location("cameraLocation");
            cameraLocation.setLatitude(t.getLatitude());
            cameraLocation.setLongitude(t.getLongitude());

            if ((location.distanceTo(cameraLocation) / 1000) < 1.5) {
                return true;
            }
        }
        return false;
    }



}
