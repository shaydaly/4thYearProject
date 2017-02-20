package com.carvis;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
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
import com.google.gson.JsonArray;
import com.mysampleapp.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.Duration;

/**
 * Created by Seamus on 24/01/2017.
 */

public class Journey extends Activity {
    private String journeyID, journeyFragmentID;
    private String longitude , endLongitude;
    private String latitude, endLatitude;
    private String  currentSpeed;
    private String speedLimit;
    private Date startTime;
    private Date endTime;
    private String start, end, time;
    private List<JourneyFragment> journeyFragmentList;
    private ArrayList<Journey> journeys;
    private ArrayList<JourneyFragment> journeyFragments;

    CognitoUserPoolsSignInProvider provider;



    public Journey(String latitude, String longitude, String currentSpeed, String speedLimit, Date start, Date end) {
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

    public Journey(String latitude, String longitude, String currentSpeed, String speedLimit) {
        journeyID="";
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
        journeyFragmentList = new ArrayList<>();
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }

    public Journey(String jfid, String latitude, String longitude, String currentSpeed, String speedLimit, String time){
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
        currentSpeed="";
        speedLimit="";
        journeys = new ArrayList<>();
        journeyFragments = new ArrayList<>();
    }

    public String getStart() {
        return start;
    }

    public String getEnd(){
        return end;
    }
    public void addJourneyFragment(JourneyFragment jf){
        journeyFragmentList.add(jf);
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getStartTime() {
        return startTime;
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


    public String getSpeedLimit() {
        return speedLimit;
    }

    public String getCurrentSpeed() {
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

//    public void setJourneyList(ArrayList a){
//        journeys = a;
//        System.out.println("set journeys called");
//        for(String s: journeys){
//            System.out.println(s);
//        }
//    }
//
//    public void AddToJourneyList(String s){
//        journeys.add(s);
//    }
    public ArrayList<Journey> getListOfJourneys(Context c){
//        System.out.println("list start");
//        for(String s: journeys){
//            System.out.println("__"+s);
//        }
//        System.out.println("list emd");
        return journeys;
    }

    public ArrayList<JourneyFragment> getListOfJourneyFragments(Context c){
        return journeyFragments;
    }

    public String getJourneyDuration(){
            DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");;
            DateTime journeyBeginning = format.parseDateTime(start);
            DateTime journeyEnd = format.parseDateTime(end);
            System.out.println(); // Sat Jan 02 00:00:00 GMT 2010
            org.joda.time.Duration duration = new org.joda.time.Duration(journeyBeginning,journeyEnd);
            return duration.getStandardHours()+" hrs "+duration.getStandardMinutes()+" mins";
    }

    public void clearJourneyFragments(){
        journeyFragments.clear();
    }

    public void getUsersJourneys(Context c, String username){

        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/retrieveuserjourneys?username="+username;
        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
        JsonArrayRequest request = new JsonArrayRequest("https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/retrieveuserjourneys?username="+username,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //AddToJourneyList(jsonObject.toString());
                                //journeys.add((jsonObject.get("starttime").toString()));


                                String startTime = jsonObject.get("starttime").toString();
                                String endTime =  jsonObject.get("endtime").toString();
                                String startLon =  jsonObject.get("startLon").toString();
                                String startLat =  jsonObject.get("startLat").toString();
                                String journeyID =  jsonObject.get("journeyID").toString();
                                String endLon =  jsonObject.get("endLon").toString();
                                String endLat =  jsonObject.get("endLat").toString();

                                Journey j = new Journey(journeyID,startLat,startLon,endLat,endLon,startTime,endTime);
                                journeys.add(j);




                                //jarr.put(jsonObject);
                            }
                            catch(JSONException e) {
                                //j.add("Error: " + e.getLocalizedMessage());
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
                            //System.out.println("++++"+journeys.size());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //mEntries
        queue.add(request);
    }
    public void getJourneyFragments(Context c, String username,String journeyID){
        RequestQueue queue = Volley.newRequestQueue(c);
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment?username="+username+"&journeyID="+journeyID;
        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //AddToJourneyList(jsonObject.toString());
                                //journeys.add((jsonObject.get("starttime").toString()));
                                String journeyFragID = jsonObject.get("journeyFragID").toString();
                                String longitude =  jsonObject.get("longitude").toString();
                                String latitude =  jsonObject.get("latitude").toString();
                                String time =  jsonObject.get("time").toString();
                                String speedLimit =  jsonObject.get("speedLimit").toString();
                                String currentSpeed =  jsonObject.get("currentSpeed").toString();

                                JourneyFragment j = new JourneyFragment(journeyFragID,latitude,longitude,currentSpeed,speedLimit,time);
                               //System.out.println(j.getCurrentSpeed());
                                    journeyFragments.add(j);
//                                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//                                Date date = format.parse(startTime);
//                                Date date2 = format.parse(startTime);
//                                System.out.println("date calc");
//                                System.out.println(date2.getTime() - date.getTime());



                                //jarr.put(jsonObject);
                            }
                            catch(JSONException e) {
                                //j.add("Error: " + e.getLocalizedMessage());
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
                        //System.out.println("++++"+journeyFragments.size());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //mEntries
        queue.add(request);
    }



    public  void addJourneyDB(Context c, String username,String updateType){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(c);
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createjourneyobject";
            Date dNow = new Date( );
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("longitude", longitude);
            jsonBody.put("latitude", latitude);
            jsonBody.put("startTime",dNow);
            jsonBody.put("endTime",dNow);
            jsonBody.put("username", username);
            jsonBody.put("sqlType",updateType);
            jsonBody.put("journeyID",journeyID);
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
                        try {
                            String str = new String(response.data, "UTF-8");
                            String jID = str.replaceAll("[^\\d.]", "");
                            journeyID = jID;
                            System.out.println(journeyID);
                        }
                        catch(UnsupportedEncodingException e){

                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }
}
