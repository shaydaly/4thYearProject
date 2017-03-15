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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Seamus on 26/01/2017.
 */

public class OverSpeedLimit implements Serializable{

    private String longitude, latitude, speedLimit;
    private String currentSpeed;
    private Date currentTime;
    private String journeyid, username;

    public OverSpeedLimit(String latitude,String longitude, String currentSpeed, String speedLimit) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
    }

    public OverSpeedLimit(String latitude,String longitude, String currentSpeed, String speedLimit, Date currentTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.currentTime = currentTime;
    }

    public OverSpeedLimit(String latitude,String longitude, String currentSpeed, String speedLimit, Date currentTime, String username, String journeyid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.currentTime = currentTime;
        this.username = username;
        this.journeyid = journeyid;
    }

    public String getJourneyid() {
        return journeyid;
    }

    public void setJourneyid(String journeyid) {
        this.journeyid = journeyid;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getCurrentSpeed() {
        return currentSpeed;
    }

    public String getSpeedLimit() {
        return speedLimit;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setCurrentSpeed(String currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setSpeedLimit(String speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void InsertOverLimitDB(RequestQueue queue , String journeyID,  String user){
        try {
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createoverspeedobject";
//            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//            currentTime = new Date();
            Date dNow = new Date( );
            SimpleDateFormat ft =
                    new SimpleDateFormat ("HH:mm:ss");


            JSONObject jsonBody = new JSONObject();
            jsonBody.put("latitude",latitude);
            jsonBody.put("longitude",longitude);
            jsonBody.put("time",ft.format(dNow));
            jsonBody.put("username", user);
            jsonBody.put("journeyID", journeyID);
            jsonBody.put("speedTravelling", currentSpeed);
            jsonBody.put("speedLimit",speedLimit);


            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //result = response;
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //result = error.toString();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        // result = uee.toString();
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        //result = (response.toString());
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }

    public static void addOverSpeedLimits(RequestQueue queue,Context context,  List<OverSpeedLimit> overSpeedLimits, String journeyID, String user) {

        for(OverSpeedLimit o : overSpeedLimits){
            if(o.getJourneyid().equals("")){
                o.setJourneyid(journeyID);
            }
        }


                System.out.println("oversped called");
                Gson gson = new Gson();
                String json = gson.toJson(overSpeedLimits);

////        JsonObject journeyid = new JsonObject();
////        journeyid.addProperty("journeyid",journeyID);
////
////        JsonObject username = new JsonObject();
////        username.addProperty("username",user);
//
////        HashMap<String, String> details = new HashMap<String, String>();
////        details.put("journeyid",journeyID);
////        details.put("username",user);
////        String json2 = gson.toJson(details);
//        String username = "\"username\" : \""+user+"\"";
//        String journey = ",\"journeyid\" : \""+journeyID+"\"";
//        json += ","+username+journey;

        System.out.println(json);

        try {
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createoverspeedobject";
            final String requestBody = json;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //result = response;
                    Log.i("overspeed  VOLLEY", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //result = error.toString();
                    Log.i("overspeed  VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");

                    } catch (UnsupportedEncodingException uee) {
                        // result = uee.toString();
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {

                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        //result = (response.toString());
                        try {
                            String str = new String(response.data, "UTF-8");
                            //System.out.println("overspeed "+ str);
                        }
                        catch(UnsupportedEncodingException e){

                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }


    }




