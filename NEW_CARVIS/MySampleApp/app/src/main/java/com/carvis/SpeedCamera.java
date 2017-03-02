package com.carvis;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.mysampleapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Seamus on 22/02/2017.
 */

public class SpeedCamera {

    private int id;
    private double startLatitude, startLongitude, endLatitude, endLongitude;
    private Date time;
    public static HashSet<SpeedCamera> cameras = new HashSet<>();

//    public SpeedCamera(int id,double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
//        this.id = id;
//        this.startLatitude = startLatitude;
//        this.startLongitude = startLongitude;
//        this.endLatitude = endLatitude;
//        this.endLongitude = endLongitude;
//    }

    public SpeedCamera(int id, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.id= id;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }
    public SpeedCamera(){

    }

    public static void addSpeedCamera(SpeedCamera s){
        cameras.add(s);
    }

    public static void removeSpeedCamera(int id){
        for (SpeedCamera cam : cameras) {
            if(cam.getId()==id){
                cameras.remove(cam);
                return;
            }
        }
    }
    public int getId() {
        return id;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setId(int id) {
        this.id = id;
    }
//
    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }





    public static String getSpeedCameraAddress(Context context, double latitude, double longitude){
        String address="";
        Geocoder geocoder= new Geocoder(context, Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude, 1);
            if(addresses != null) {

                Address fetchedAddress = addresses.get(0);
                for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i));
                    if (i != fetchedAddress.getMaxAddressLineIndex() - 1) {
                        strAddress.append(",\n");
                    } else {
                        strAddress.append(".\n");
                    }
                }

                address = strAddress.toString();
            }
            else{
                address ="Unknown";
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return address;
    }

}
