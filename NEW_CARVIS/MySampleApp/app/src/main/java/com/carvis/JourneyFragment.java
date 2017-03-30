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
    int speed;
    int currentSpeed, speedLimit;
    String timeString;

    JourneyFragment(String lat, String lon,  int sp, int sl,Date d, String jID, String u){
 //       super(lat,lon,speed,speedLimit);
        latitude = lat;
        longitude = lon;
        speed = sp;
        speedLimit = sl;
        time = d;
        journeyID = jID;
        username = u;
    }

    JourneyFragment(String jid, String latitude,String lon, int currentSpeed, int speedLimit, String time ){
        this.latitude = latitude;
        longitude = lon;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.timeString = time;
        journeyID = jid;
    }

//    public JourneyFragment() {
//        this.latitude = "";
//        this.time = null;
//        this.longitude = "";
//        this.speedLimit = "";
//        this.journeyID = "";
//        this.username = "";
//        this.speed = "";
//        this.currentSpeed = "";
//        this.timeString ="";
//    }

    public String getJourneyID() {
        return journeyID;
    }

    public String getUsername() {
        return username;
    }

    public String getTimeString() {
        return timeString;
    }

    public int getCurrentSpeed(){
        return speed;
    }
    public void setTime(Date time) {
        this.time = time;
    }
//

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Date getTime() {
        return time;
    }
//

    public String getLatitude() {
        return latitude;
    }


    public int getSpeedLimit() {
        return speedLimit;
    }


    public String getLongitude() {
        return longitude;
    }

    public int getSpeed() {
        return speed;
    }

    public int getFragmentSpeed(){
        return currentSpeed;
    }


//    public void setTime(Date time) {
//        this.time = time;
//    }
//
//    public Date getTime() {
//        return time;
//    }


    public void setJourneyID(String journeyID) {
        this.journeyID = journeyID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

//    public static void AddJourneyFragments(RequestQueue queue, List<JourneyFragment> journies, String journeyID){
//        try {
//            for(JourneyFragment j : journies){
//                if(j.getJourneyID().equals("")){
//                    j.setJourneyID(journeyID);
//                }
//            }
//            System.out.println("AddJourneyFragments called");
//            Gson gson = new Gson();
//            String json = gson.toJson(journies);
//            System.out.println(json);
//            try {
//                //RequestQueue requestQueue = Volley.newRequestQueue(c);
//                String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment";
//                final String requestBody = json;
//
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        //result = response;
//                        Log.i("AddJourneyFragments", response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("Volley Error1 ",error.toString());
//                    }
//                }) {
//                    @Override
//                    public String getBodyContentType() {
//                        return "application/json; charset=utf-8";
//                    }
//
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//                        try {
//                            return requestBody == null ? null : requestBody.getBytes("utf-8");
//
//                        } catch (UnsupportedEncodingException uee) {
//                            // result = uee.toString();
//                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                            return null;
//                        }
//                    }
//
//                    @Override
//                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
//
//                        String responseString = "";
////                        if (response != null) {
////                            responseString = String.valueOf(response.statusCode);
////                            // can get more details such as response.headers
////                            //result = (response.toString());
////
//////                            try {
//////                                String str = new String(response.data, "UTF-8");
//////                                //journeyID = jID;
//////                                //System.out.println(str+"______________!!");
//////                            }
//////                            catch(UnsupportedEncodingException e){
//////                                System.out.println(e.getMessage());
//////                            }
////
////
////                        }
//                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                    }
//                };
//
//                queue.add(stringRequest);
//            } catch (Exception e) {
//                e.printStackTrace();
//                //result = e.toString();
//            }
//
//
//
//        }
//
//        catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//
//
//    }


}
