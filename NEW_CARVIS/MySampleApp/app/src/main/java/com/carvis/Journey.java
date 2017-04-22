package com.carvis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Seamus on 24/01/2017.
 */

public class Journey extends Activity {
    private String journeyID, journeyFragmentID;
    private String longitude , endLongitude;
    private String latitude, endLatitude;
    private int  currentSpeed;
    private int speedLimit;
    private Date startTime;
    private Date endTime;
    private String start, end, time;
    private List<JourneyFragment> journeyFragmentList;
    private ArrayList<Journey> journeys;
    private ArrayList<JourneyFragment> journeyFragments;

    public Journey(String latitude, String longitude, int currentSpeed, int speedLimit, Date start, Date end) {
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
        this.startTime = start;
        this.endTime = end;
        journeyFragmentList = new ArrayList<>();
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }

    public Journey(String jIn, String latitude, String longitude, String endLatitude, String endLong, String startTime, String endTime) {
        journeyID = jIn;
        this.latitude = latitude;
        this.longitude = longitude;
        this.endLatitude = endLatitude;
        this.endLongitude  = endLong;
        this.start = startTime;
        this.end= endTime;
        journeyFragmentList = new ArrayList<>();
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }


    public String getTime() {
        return time;
    }

    public Journey(String latitude, String longitude, int currentSpeed, int speedLimit) {
        journeyID="";
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
        journeyFragmentList = new ArrayList<>();
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }

    public Journey(String jfid, String latitude, String longitude, int currentSpeed, int speedLimit, String time){
        journeyFragmentID = jfid;
        this.latitude =latitude;
        this.longitude = longitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.time = time;
        journeyFragments = new ArrayList<>();
    }

    public Journey(){
        journeyID="";
        longitude="";
        latitude="";
        currentSpeed=0;
        speedLimit=0;
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }

    public String getStart() {
        return start;
    }

    public String getEnd(){
        return end;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setCurrentSpeed(int currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }


    public int getSpeedLimit() {
        return speedLimit;
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(String journeyId) {
        journeyID = journeyId;
    }

    public void addToJourneys(Journey journey){
        journeys.add(journey);
    }

    public ArrayList<Journey> getListOfJourneys(){
        return journeys;
    }

    public ArrayList<JourneyFragment> getListOfJourneyFragments(){
        return journeyFragments;
    }

    public void addToJourneyFragments(JourneyFragment j){
        journeyFragments.add(j);
    }

    public String getJourneyDuration(){

            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        if(start!= null&& end!=null) {
            DateTime journeyBeginning = format.parseDateTime(start);
            DateTime journeyEnd = format.parseDateTime(end);
            System.out.println(); // Sat Jan 02 00:00:00 GMT 2010
            org.joda.time.Duration duration = new org.joda.time.Duration(journeyBeginning, journeyEnd);
            return duration.getStandardHours() + " hrs " + duration.getStandardMinutes() + " mins";
        }
        else{
            return "NA";
        }
    }

    public void clearJourneyFragments(){
        journeyFragments.clear();
    }

}
