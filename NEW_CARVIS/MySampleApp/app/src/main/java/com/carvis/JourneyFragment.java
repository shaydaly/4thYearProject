package com.carvis;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * Created by Seamus on 08/02/2017.
 */

public class JourneyFragment implements Serializable{
    private Date time;
    private String latitude, longitude, journeyID, username;
    int currentSpeed, speedLimit, osmID;
    String timeString;

    JourneyFragment(String lat, String lon,  int sp, int sl,Date d, String jID, String u, int osmID){
        latitude = lat;
        longitude = lon;
        currentSpeed = sp;
        speedLimit = sl;
        time = d;
        journeyID = jID;
        username = u;
        this.osmID = osmID;
    }

    JourneyFragment(String jid, String latitude,String lon, int currentSpeed, int speedLimit, String time ){
        this.latitude = latitude;
        longitude = lon;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.timeString = time;
        journeyID = jid;
    }

    public String getJourneyID() {
        return journeyID;
    }

    public String getUsername() {
        return username;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }


    public String getLatitude() {
        return latitude;
    }


    public int getSpeedLimit() {
        return speedLimit;
    }


    public String getLongitude() {
        return longitude;
    }



    public int getFragmentSpeed(){
        return currentSpeed;
    }

    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public void setUsername(String username) {
        this.username = username;
    }




}
