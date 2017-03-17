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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mysampleapp.R;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Seamus on 22/02/2017.
 */

public class SpeedCamera implements Serializable {

    private int id;
    private double startLatitude, startLongitude, endLatitude, endLongitude, midLatitude, midLongitude;
    private Date time;
    private static LinkedHashSet<SpeedCamera> cameras = new LinkedHashSet<>();

    private Location startLocation, endLocation, middleLocation, location1, location4;

    private static HashMap<Date, Boolean> reportedIncidents = new HashMap<>();

    private ArrayList<String> reportedTimes;

    private ArrayList<Location> cameraLocations;

    public SpeedCamera(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;

        cameraLocations = new ArrayList<>();
        initializeLocations();
    }

    public SpeedCamera(int id, double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        this.id = id;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        cameraLocations = new ArrayList<>();

        initializeLocations();

}

    public SpeedCamera(int id, double startLatitude, double startLongitude, double endLatitude, double endLongitude, ArrayList<String> dates) {
        this.id = id;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        reportedTimes = new ArrayList<>(dates);
        cameraLocations = new ArrayList<>();
        initializeLocations();
    }

    public SpeedCamera() {

    }

    public void initializeLocations(){
        startLocation = new Location("start");
        endLocation = new Location("end");
        middleLocation = new Location("middle");
        location1 = new Location("Location 1");
        location4 = new Location("Location 4");


        startLocation.setLatitude(startLatitude);
        startLocation.setLongitude(startLongitude);

        endLocation.setLatitude(endLatitude);
        endLocation.setLongitude(endLongitude);


        middleLocation = midPoint(startLocation, endLocation);
        location1 = midPoint(startLocation, middleLocation);
        location4 = midPoint(middleLocation, endLocation);

        cameraLocations.add(startLocation);
        cameraLocations.add(location1);
        cameraLocations.add(middleLocation);
        cameraLocations.add(location4);
        cameraLocations.add(endLocation);
    }

    public ArrayList<Location> getCameraLocations() {
        return cameraLocations;
    }

    public void addSpeedCameraIncident(Date time, Boolean vanInLocation) {
        reportedIncidents.put(time, vanInLocation);
    }

    public static LinkedHashSet<SpeedCamera> getCameras() {
        return cameras;
    }

    public static HashMap<Date, Boolean> getReportedIncidents() {
        return reportedIncidents;
    }

    public static void setCameras(LinkedHashSet<SpeedCamera> cameras) {
        SpeedCamera.cameras = cameras;
    }

    public static void setReportedIncidents(HashMap<Date, Boolean> reportedIncidents) {
        SpeedCamera.reportedIncidents = reportedIncidents;
    }

    public ArrayList<String> getReportedTimes() {
        return reportedTimes;
    }

    public void setReportedTimes(ArrayList<String> reportedTimes) {
        this.reportedTimes = reportedTimes;
    }

    public static void addSpeedCamera(SpeedCamera s) {
        cameras.add(s);
    }

    public static void removeSpeedCamera(int id) {
        for (SpeedCamera cam : cameras) {
            if (cam.getId() == id) {
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

    public double getMidLongitude() {
        return midLongitude;
    }

    public void setMidLongitude(double midLongitude) {
        this.midLongitude = midLongitude;
    }

    public double getMidLatitude() {
        return midLatitude;
    }

    public void setMidLatitude(double midLatitude) {
        this.midLatitude = midLatitude;
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




    public static void addSpeedVanLocatedRecord(FirebaseDatabase database, String vanID) {
        DatabaseReference myRef = database.getReference("speedVans");
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time =  ft.format(new Date());
        myRef.child(vanID).child("reportedTimes").push().setValue(time);
    }


    public static String getSpeedCameraAddress(Context context, double latitude, double longitude) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {

                Address fetchedAddress = addresses.get(0);
                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i));
                    if (i != fetchedAddress.getMaxAddressLineIndex() - 1) {
                        strAddress.append(",\n");
                    } else {
                        strAddress.append(".\n");
                    }
                }

                address = strAddress.toString();
            } else {
                address = "Unknown";
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return address;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                        append(startLatitude).
                        append(startLongitude).
                        append(endLatitude).
                        append(endLongitude).
                        toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SpeedCamera))
            return false;
        if (obj == this)
            return true;

        SpeedCamera rhs = (SpeedCamera) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                        append(startLatitude, rhs.startLatitude).
                        append(startLongitude, rhs.startLongitude).
                        append(endLatitude, rhs.endLatitude).
                        append(endLongitude, rhs.endLongitude).
                        isEquals();
    }

    public Location  midPoint(Location startLocation,Location endLocation ){

        double dLon = Math.toRadians(endLocation.getLongitude() - startLocation.getLongitude());

        //convert to radians
        double startLatitude = (Math.toRadians(startLocation.getLatitude()));
        double endLatitude = (Math.toRadians(endLocation.getLatitude()));
        double startLongitude  = ( Math.toRadians(startLocation.getLongitude()));

        double Bx = Math.cos(endLatitude) * Math.cos(dLon);
        double By = Math.cos(endLatitude) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(startLatitude) + Math.sin(endLatitude), Math.sqrt((Math.cos(startLatitude) + Bx) * (Math.cos(startLatitude) + Bx) + By * By));
        double lon3 = startLongitude + Math.atan2(By, Math.cos(startLatitude) + Bx);
//        mLatitude = Math.toDegrees(lat3);
//        mLongitude = Math.toDegrees(lon3);
        Location midPointLocation = new Location("");
        midPointLocation.setLatitude(Math.toDegrees(lat3));
        midPointLocation.setLongitude(Math.toDegrees(lon3));
        return midPointLocation;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Location getMiddleLocation() {
        return middleLocation;
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation4() {
        return location4;
    }
//    public void  midPoint(){
//
//        double dLon = Math.toRadians(endLongitude - startLongitude);
//
//        //convert to radians
//        startLatitude = Math.toRadians(startLatitude);
//        endLatitude = Math.toRadians(endLatitude);
//        startLongitude = Math.toRadians(startLongitude);
//
//        double Bx = Math.cos(endLatitude) * Math.cos(dLon);
//        double By = Math.cos(endLatitude) * Math.sin(dLon);
//        double lat3 = Math.atan2(Math.sin(startLatitude) + Math.sin(endLatitude), Math.sqrt((Math.cos(startLatitude) + Bx) * (Math.cos(startLatitude) + Bx) + By * By));
//        double lon3 = startLongitude + Math.atan2(By, Math.cos(startLatitude) + Bx);
//        midLatitude = Math.toDegrees(lat3);
//        midLongitude = Math.toDegrees(lon3);
//    }
}
