package com.carvis;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Seamus on 22/02/2017.
 */

public class SpeedCamera {

    private int id;
    private double startLatitude, startLongitude, endLatitude, endLongitude;
    private Date time;

    public SpeedCamera(int id,double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.id = id;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }

    public SpeedCamera(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
    }
    public SpeedCamera(){

    }

    public long getId() {
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

    public String getSpeedCameraAddress(Context context){
        String address="";
        Geocoder geocoder= new Geocoder(context, Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();

        try {
            List<Address> addresses = geocoder.getFromLocation(startLatitude,startLongitude, 1);
            if(addresses != null) {

                Address fetchedAddress = addresses.get(0);


                for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i));
                    if (i != fetchedAddress.getMaxAddressLineIndex() - 1) {
                        strAddress.append(",\t\t");
                    } else {
                        strAddress.append(".\t\t");
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
