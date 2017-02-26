package com.carvis;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Seamus on 26/02/2017.
 */

public class TemporarySpeedCamera {
    private String latitude, longitude;
    private Date time;

    public TemporarySpeedCamera(String latitude, String longitude, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public static void addTemporaryCamera(String latitude, String longitude, Date time, Context context){
        FirebaseApp.initializeApp(context);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        final HashMap<String, String> map1 = new HashMap<>();
        map1.put("startLatitude", latitude);
        map1.put("startLongitude", longitude);
        //map1.put("time", String.valueOf(time));
        map1.put("endLatitude", latitude);
        map1.put("endLongitude", longitude);
        //myRef.push().setValue(map1);

        myRef.push().setValue(new SpeedCamera(Double.parseDouble(latitude),
 Double.parseDouble(longitude),Double.parseDouble(latitude),Double.parseDouble(longitude)));
    }

}
