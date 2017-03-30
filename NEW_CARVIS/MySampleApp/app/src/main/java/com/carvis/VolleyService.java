package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mysampleapp.R;

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

import static android.R.attr.value;

/**
 * Created by Seamus on 23/03/2017.
 */

public class VolleyService {

    private RequestQueue queue;
    private Context context;
    String url;

    VolleyService(Context context){
        this.context = context;
        queue = Volley.newRequestQueue(context);
        url = "";
    }

    public  void addJourneyDB(final Journey journey, String username,String updateType){
        try {
            System.out.println("Add journey called");
            url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createjourneyobject";
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("longitude", journey.getLongitude());
            jsonBody.put("latitude", journey.getLatitude());
            jsonBody.put("startTime",dNow);
            jsonBody.put("endTime",dNow);
            jsonBody.put("username", username);
            jsonBody.put("sqlType",updateType);
            jsonBody.put("journeyID",journey.getJourneyID());
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //result = response;
                    Log.i("JOURNEY VOLLEY", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //result = error.toString();
                    Log.i("JOURNEY VOLLEY", error.toString());
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
                            journey.setJourneyID(jID);
                            System.out.println("JOURNEYID: "+journey.getJourneyID());
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



    public void getSpeedFromLambda(FirebaseDatabase database, final SpeedSearch speedSearch, final String latitude,final  String longitude) {
        final DatabaseReference myRef = database.getReference("speedLimits");
        System.out.println("GET SPEED CALLED");
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("speed lambda response", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            //speedLimit = ((obj.get("speed").toString()));
                            //speedLimitTextView.setText(newSpeed+"km/h");
                            //System.out.println(speedLimit+ " from speed lambda");
//                            Location l = new Location("location");
//                            l.setLatitude(Double.parseDouble(latitude));
//                            l.setLongitude(Double.parseDouble(longitude));
                            int speedLimit = obj.getInt("speed");
                            //myRef.child(String.valueOf(obj.get("osm_id"))).push().setValue(new RoadRecord(latitude,longitude, speedLimit));
                            speedSearch.setOsm_id(obj.getInt("osm_id"));
                        } catch (JSONException e) {
                            Log.i("GET SPEED EXCEPTIOM ", e.getMessage());
                        }
                        catch(Exception e){
                            Log.i("sp ex ", "speed lambda exception");
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Speed Lambda","ERROR");
                    }
                });
        queue.add(jsObjRequest);
    }

    public void getJourneyFragments(String username,String journeyID, final Journey journey){
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment?username="+username+"&journeyID="+journeyID;
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
                                String journeyFragID = jsonObject.getString("journeyFragID");
                                String longitude =  jsonObject.getString("longitude");
                                String latitude =  jsonObject.getString("latitude");
                                String time =  jsonObject.getString("time");
                                int speedLimit =  jsonObject.getInt("speedLimit");
                                int currentSpeed =  jsonObject.getInt("currentSpeed");

                                JourneyFragment j = new JourneyFragment(journeyFragID,latitude,longitude,currentSpeed,speedLimit,time);
                                //System.out.println(j.getCurrentSpeed());
                                //journeyFragments.add(j);
                                journey.addToJourneyFragments(j);
                                //jarr.put(jsonObject);
                            }
                            catch(JSONException e) {
                                //j.add("Error: " + e.getLocalizedMessage());
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
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

    public void getUsersJourneys(String username, final Journey journey){
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/retrieveuserjourneys?username="+username;
        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String startTime = jsonObject.get("starttime").toString();
                                String endTime =  jsonObject.get("endtime").toString();
                                String startLon =  jsonObject.get("startLon").toString();
                                String startLat =  jsonObject.get("startLat").toString();
                                String journeyID =  jsonObject.get("journeyID").toString();
                                String endLon =  jsonObject.get("endLon").toString();
                                String endLat =  jsonObject.get("endLat").toString();

                                Journey j = new Journey(journeyID,startLat,startLon,endLat,endLon,startTime,endTime);
                                //journeys.add(j);
                                journey.addToJourneys(j);
                            }
                            catch(JSONException e) {
                                System.out.println(e.getMessage()+"----");
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
                        //goToJourneys(journeys);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("volley error in USe JouRS "+volleyError.toString());
                        //Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //mEntries
        queue.add(request);
    }

    public  void addJourneyFragments(List<JourneyFragment> journies, String journeyID){
        try {
            for(JourneyFragment j : journies){
                if(j.getJourneyID().equals("")){
                    j.setJourneyID(journeyID);
                }
            }
            System.out.println("AddJourneyFragments called");
            Gson gson = new Gson();
            String json = gson.toJson(journies);
            System.out.println(json);
            try {
                //RequestQueue requestQueue = Volley.newRequestQueue(c);
                url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment";
                final String requestBody = json;

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //result = response;
                        Log.i("AddJourneyFragments", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley Error1 ",error.toString());
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
//                        if (response != null) {
//                            responseString = String.valueOf(response.statusCode);
//                            // can get more details such as response.headers
//                            //result = (response.toString());
//
////                            try {
////                                String str = new String(response.data, "UTF-8");
////                                //journeyID = jID;
////                                //System.out.println(str+"______________!!");
////                            }
////                            catch(UnsupportedEncodingException e){
////                                System.out.println(e.getMessage());
////                            }
//
//
//                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                queue.add(stringRequest);
            } catch (Exception e) {
                e.printStackTrace();
                //result = e.toString();
            }



        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }


    }

    public void getUserStatistics(String username){
        final DateTimeFormatter dateWithTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/getuserstatistics?username="+username;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<JourneyInfo> journeys = new ArrayList<>();
                            ArrayList<DateTime> dates = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int numJourneys = Integer.parseInt(jsonObject.get("numJourneys").toString());
                            int journeysWithOverSpeed = jsonObject.getInt("journeysWithOverSpeed");
                            String overSpeedRoad = jsonObject.getString("overSpeedRoad");
                            String roadAddress = jsonObject.getString("roadAddress");
                            //System.out.println(numJourneys+" num journeys\n"+journeysWithOverSpeed+"journeys");
                            JSONArray jsonArray = jsonObject.getJSONArray("journeys");
                            for(int i = 0; i < jsonArray.length(); i++){
                                String endTime = String.valueOf(jsonArray.getJSONObject(i).get("endTime"));
                                String startTime = String.valueOf(jsonArray.getJSONObject(i).get("startTime"));
                                double startLatitude =  Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).get("startLatitude")));
                                double startLongitude =  Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).get("startLongitude")));
                                double endLatitude =  Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).get("endLatitude")));
                                double endLongitude =  Double.parseDouble(String.valueOf(jsonArray.getJSONObject(i).get("endLongitude")));

//                                Location start = new Location("start");
//                                start.setLatitude(Double.parseDouble(startLatitude));
//                                start.setLongitude(Double.parseDouble(startLongitude));
//                                System.out.println(start.getLatitude()+" _ _ _ "+start.getLongitude());
//                                Location end = new Location("end");
//                                end.setLatitude(Double.parseDouble(endLatitude));
//                                end.setLongitude(Double.parseDouble(endLongitude));
                                journeys.add(new JourneyInfo(dateWithTimeFormatter.parseDateTime(startTime), dateWithTimeFormatter.parseDateTime(endTime), startLatitude, startLongitude, endLatitude, endLongitude));
                            }
                            JSONArray overSpeedDates = jsonObject.getJSONArray("overSpeedDates");
                            for(int i = 0; i < overSpeedDates.length(); i++){
                                String date = String.valueOf(overSpeedDates.getJSONObject(i).get("overSpeedDate"));
                                dates.add(dateFormatter.parseDateTime(date));
                            }

                            String date = String.valueOf(jsonObject.get("memberSince"));
                            DateTime memberSince = dateFormatter.parseDateTime(date);



                            UserStat userStat = new UserStat(overSpeedRoad,journeys,journeysWithOverSpeed,numJourneys,dates, memberSince, roadAddress);
                            Intent myIntent = new Intent(context, UserStatActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            myIntent.putExtra("userStat", userStat); //Optional parameters
                            context.startActivity(myIntent);
                        } catch (JSONException e) {
                            Log.i("user stat", e.getMessage());
                        }
                        catch(Exception e){
                            Log.i("user stat", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Speed Lambda","ERROR");
                    }
                });
        queue.add(jsObjRequest);
    }


    public  void addOverSpeedLimits(List<OverSpeedLimit> overSpeedLimits, String journeyID, String user) {

        Log.i("over speed size ",String.valueOf(overSpeedLimits.size()));
        for(OverSpeedLimit o : overSpeedLimits){
            if(o.getJourneyid().equals("")){
                o.setJourneyid(journeyID);
            }
        }


        System.out.println("oversped called");
        Gson gson = new Gson();
        String json = gson.toJson(overSpeedLimits);


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


    public void getDaysSinceLastOverSpeed(final UserStat  u) {

        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/daysinceoverspeed?username="+u.getUsername();
        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("days", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            int daysSinceOverSpeed = obj.getInt("daysOverSpeed");
                            u.setDaysSinceOverSpeed(daysSinceOverSpeed);

                        } catch (JSONException e) {
                            Log.i("days", e.getMessage());
                        }
                        catch(Exception e){
                            Log.i("days ", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Sdays","ERROR");
                    }
                });
        queue.add(jsObjRequest);
    }
}
