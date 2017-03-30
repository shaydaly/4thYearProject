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
    public ArrayList<Journey> getListOfJourneys(){
        return journeys;
    }

    public ArrayList<JourneyFragment> getListOfJourneyFragments(){
        return journeyFragments;
    }

    public void setJourneyFragments(ArrayList<JourneyFragment> journeyFragments) {
        this.journeyFragments = journeyFragments;
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

//    public void getUsersJourneys(Context c, String username){
//        System.out.println("userJOurNeysCalled");
//        RequestQueue queue = Volley.newRequestQueue(c);
//        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/retrieveuserjourneys?username="+username;
//        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
//        JsonArrayRequest request = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray jsonArray) {
//                        for(int i = 0; i < jsonArray.length(); i++) {
//                            try {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                String startTime = jsonObject.get("starttime").toString();
//                                String endTime =  jsonObject.get("endtime").toString();
//                                String startLon =  jsonObject.get("startLon").toString();
//                                String startLat =  jsonObject.get("startLat").toString();
//                                String journeyID =  jsonObject.get("journeyID").toString();
//                                String endLon =  jsonObject.get("endLon").toString();
//                                String endLat =  jsonObject.get("endLat").toString();
//
//                                Journey j = new Journey(journeyID,startLat,startLon,endLat,endLon,startTime,endTime);
//                                journeys.add(j);
//                            }
//                            catch(JSONException e) {
//                                System.out.println(e.getMessage()+"----");
//                            }
////                            catch(ParseException e) {
////                                //j.add("Error: " + e.getLocalizedMessage());
////                            }
//                        }
//                        //goToJourneys(journeys);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        System.out.println("volley error in USe JouRS "+volleyError.toString());
//                        //Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//        //mEntries
//        queue.add(request);
//    }
//    public void getJourneyFragments(Context c, String username,String journeyID){
//        RequestQueue queue = Volley.newRequestQueue(c);
//        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment?username="+username+"&journeyID="+journeyID;
//        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
//        JsonArrayRequest request = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray jsonArray) {
//                        for(int i = 0; i < jsonArray.length(); i++) {
//                            try {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                //AddToJourneyList(jsonObject.toString());
//                                //journeys.add((jsonObject.get("starttime").toString()));
//                                String journeyFragID = jsonObject.get("journeyFragID").toString();
//                                String longitude =  jsonObject.get("longitude").toString();
//                                String latitude =  jsonObject.get("latitude").toString();
//                                String time =  jsonObject.get("time").toString();
//                                String speedLimit =  jsonObject.get("speedLimit").toString();
//                                String currentSpeed =  jsonObject.get("currentSpeed").toString();
//
//                                JourneyFragment j = new JourneyFragment(journeyFragID,latitude,longitude,currentSpeed,speedLimit,time);
//                               //System.out.println(j.getCurrentSpeed());
//                                    journeyFragments.add(j);
//
//                                //jarr.put(jsonObject);
//                            }
//                            catch(JSONException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
////                            catch(ParseException e) {
////                                //j.add("Error: " + e.getLocalizedMessage());
////                            }
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        //Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//        //mEntries
//        queue.add(request);
//    }



//    public  void addJourneyDB(RequestQueue queue, String username,String updateType){
//        try {
//            System.out.println("Add journey called");
//            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createjourneyobject";
//            Date dNow = new Date();
//            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//            JSONObject jsonBody = new JSONObject();
//            jsonBody.put("longitude", longitude);
//            jsonBody.put("latitude", latitude);
//            jsonBody.put("startTime",dNow);
//            jsonBody.put("endTime",dNow);
//            jsonBody.put("username", username);
//            jsonBody.put("sqlType",updateType);
//            jsonBody.put("journeyID",journeyID);
//            final String requestBody = jsonBody.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    //result = response;
//                    Log.i("JOURNEY VOLLEY", response.toString());
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    //result = error.toString();
//                    Log.i("JOURNEY VOLLEY", error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        return requestBody == null ? null : requestBody.getBytes("utf-8");
//
//                    } catch (UnsupportedEncodingException uee) {
//                       // result = uee.toString();
//                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
//                        return null;
//                    }
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//
//                    String responseString = "";
//                    if (response != null) {
//                        responseString = String.valueOf(response.statusCode);
//                        // can get more details such as response.headers
//                        //result = (response.toString());
//                        try {
//                            String str = new String(response.data, "UTF-8");
//                            String jID = str.replaceAll("[^\\d.]", "");
//                            journeyID = jID;
//                            System.out.println("JOURNEYID: "+journeyID);
//                        }
//                        catch(UnsupportedEncodingException e){
//
//                        }
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            queue.add(stringRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//            //result = e.toString();
//        }
//    }

//    public void getSpeedFromLambda(FirebaseDatabase database, RequestQueue queue, final SpeedSearch speedSearch) {
//        final DatabaseReference myRef = database.getReference("speedLimits");
//        System.out.println("GET SPEED CALLED");
//        //queue  = Volley.newRequestQueue(context);
//        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
//        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Log.i("speed lambda response", response.toString());
//                            JSONObject obj = new JSONObject(response.toString());
//                            //speedLimit = ((obj.get("speed").toString()));
//                            //speedLimitTextView.setText(newSpeed+"km/h");
//                            //System.out.println(speedLimit+ " from speed lambda");
////                            Location l = new Location("location");
////                            l.setLatitude(Double.parseDouble(latitude));
////                            l.setLongitude(Double.parseDouble(longitude));
//                            //myRef.push().setValue(new Road(Integer.parseInt(String.valueOf(obj.get("osm_id"))), Integer.parseInt(speedLimit), l));
//                            myRef.child(String.valueOf(obj.get("osm_id"))).push().setValue(new RoadRecord(latitude,longitude, Integer.parseInt(speedLimit)));
//                            //myRef.child(String.valueOf(obj.get("osm_id"))).child("location").child("speedLimit").push().setValue(speedLimit);
//                            speedSearch.setOsm_id(obj.getInt("osm_id"));
//                        } catch (JSONException e) {
//                            Log.i("GET SPEED EXCEPTIOM ", e.getMessage());
//                        }
//                        catch(Exception e){
//                            Log.i("sp ex ", "speed lambda exception");
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("Speed Lambda","ERROR");
//                    }
//                });
//        queue.add(jsObjRequest);
//    }

//    public void goToJourneys(ArrayList<Journey> journeys){
//        Intent myIntent = new Intent(this, ListJourney.class);
//        myIntent.putExtra("listOfJourneys", journeys); //Optional parameters
//        startActivity(myIntent);
//    }

}
