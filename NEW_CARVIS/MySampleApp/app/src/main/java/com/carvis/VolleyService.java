package com.carvis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mysampleapp.R;
import com.mysampleapp.demo.HomeDemoFragment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.R.attr.outlineProvider;
import static android.R.attr.value;

/**
 * Created by Seamus on 23/03/2017.
 */

public class VolleyService extends Activity {

    public static  String DAYS_OVER_SPEED ="DAYS_OVER_SPEED";
    public static  String OVERSPEEDDAY ="OVER_SPEED_DAY";
    public static  String NUMTRAFFICINCIDENTS ="NUMTRAFFICINCIDENTS";
    public static  String ROADSWITHINCIDENTS ="ROADSWITHINCIDENTS";
    public static  String ROADSTOAVOID ="ROADSTOAVOID";
    public static String JOURNEYRESPFULL= "";
    public static String JOURNEYRESPEMPTY = "";
    private RequestQueue queue;
    private Context context;
    String url;
    View view;

    public VolleyService(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
        url = "";
        view = new View(context);
    }

    public void addJourneyDB(final Journey journey,final CognitoUserPoolsSignInProvider provider, String updateType) {
        try {
            System.out.println("Add journey called");
            url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createjourneyobject";
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("longitude", journey.getLongitude());
            jsonBody.put("latitude", journey.getLatitude());
            jsonBody.put("startTime", dNow);
            jsonBody.put("endTime", dNow);
            jsonBody.put("username", provider.getUserName());
            jsonBody.put("sqlType", updateType);
            jsonBody.put("journeyID", journey.getJourneyID());
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", provider.getToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
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
                            System.out.println("JOURNEYID: " + journey.getJourneyID());
                        } catch (UnsupportedEncodingException e) {

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


    public void createTrafficIncident(int roadid, String date, final CognitoUserPoolsSignInProvider provider) {
        try {
            System.out.println("Add journey called");
            url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createTrafficIncident";
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("roadid", roadid);
            jsonBody.put("timeofincident", date);
            jsonBody.put("username", provider.getUserName());

            final String requestBody = jsonBody.toString();

            Log.wtf("volleyTraffic", requestBody);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.d(TAG, "Response:" + response.toString());
                            //Log.d(TAG,"Setting Response to string:\n" + response.toString());
                            Log.wtf("Response", response.toString());
                        }
                    },
                    null) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", provider.getToken());
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }


    public void getSpeedFromLambda(final MyLocationService t, final SpeedSearch speedSearch, final String latitude, final String longitude, final String token) {
        System.out.println("GET SPEED CALLED");
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
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
//                            int speedLimit = obj.getInt("speed");
                            String city = obj.getString("locale");
                            if(!t.getLocale().equals(city)) {
                                t.createSpeedLimitReference(city);
                            }
                            //myRef.child(String.valueOf(obj.get("osm_id"))).push().setValue(new RoadRecord(latitude,longitude, speedLimit));
                            speedSearch.setOsm_id(obj.getInt("osm_id"));
                        } catch (JSONException e) {
                            Log.i("GET SPEED EXCEPTIOM ", e.getMessage());
                        } catch (Exception e) {
                            Log.i("sp ex ", "speed lambda exception");
                        }
                    }
                },null) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        }; new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                       //Log.i("Speed Lambda", error.getMessage());
                    }
                };
        queue.add(jsObjRequest);
    }

    public void getJourneyFragments(final CognitoUserPoolsSignInProvider provider, String journeyID, final Journey journey) {
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/journeyfragment?username=" + provider.getUserName() + "&journeyID=" + journeyID;
        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //AddToJourneyList(jsonObject.toString());
                                //journeys.add((jsonObject.get("starttime").toString()));
                                String journeyFragID = jsonObject.getString("journeyFragID");
                                String longitude = jsonObject.getString("longitude");
                                String latitude = jsonObject.getString("latitude");
                                String time = jsonObject.getString("time");
                                int speedLimit = jsonObject.getInt("speedLimit");
                                int currentSpeed = jsonObject.getInt("currentSpeed");

                                JourneyFragment j = new JourneyFragment(journeyFragID, latitude, longitude, currentSpeed, speedLimit, time);
                                //System.out.println(j.getCurrentSpeed());
                                //journeyFragments.add(j);
                                journey.addToJourneyFragments(j);
                                //jarr.put(jsonObject);
                            } catch (JSONException e) {
                                //j.add("Error: " + e.getLocalizedMessage());
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
                    }
                },null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", provider.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        //mEntries
        queue.add(request);
    }

    public void getUsersJourneys(final CognitoUserPoolsSignInProvider provider, final Journey journey) {
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/retrieveuserjourneys?username=" + provider.getUserName();
        //JsonArrayRequest jsObjRequest = new JsonArrayRequest
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.wtf("ARR RESP",String.valueOf(jsonArray.length()));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String startTime = jsonObject.get("starttime").toString();
                                String endTime = jsonObject.get("endtime").toString();
                                String startLon = jsonObject.get("startLon").toString();
                                String startLat = jsonObject.get("startLat").toString();
                                String journeyID = jsonObject.get("journeyID").toString();
                                String endLon = jsonObject.get("endLon").toString();
                                String endLat = jsonObject.get("endLat").toString();

                                Journey j = new Journey(journeyID, startLat, startLon, endLat, endLon, startTime, endTime);
                                //journeys.add(j);
                                journey.addToJourneys(j);
                            } catch (JSONException e) {
                                System.out.println(e.getMessage() + "----");
                            }
//                            catch(ParseException e) {
//                                //j.add("Error: " + e.getLocalizedMessage());
//                            }
                        }
                        Intent intent = new Intent();
                        if(jsonArray.length()!=0){
                            intent.setAction(JOURNEYRESPFULL);
                        }
                        else{
                            intent.setAction(JOURNEYRESPEMPTY);
                        }
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        intent.setPackage(context.getPackageName());
                        context.sendBroadcast(intent);
                        //goToJourneys(journeys);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Error1 ", error.toString());
            }
        })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", provider.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        //mEntries
        queue.add(request);
    }

    public void addJourneyFragments(List<JourneyFragment> journies, String journeyID, final String token) {
        try {
            for (JourneyFragment j : journies) {
                if (j.getJourneyID().equals("")) {
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
                        Log.i("Volley Error1 ", error.toString());
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
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", token);
                        headers.put("Content-Type", "application/json");
                        return headers;
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


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public void getUserStatistics(final CognitoUserPoolsSignInProvider provider) {

        final DateTimeFormatter dateWithTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/getuserstatistics?username=" + provider.getUserName();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<JourneyInfo> journeys = new ArrayList<>();
                            ArrayList<DateTime> dates = new ArrayList<>();
//                            JSONObject jsonObject = new JSONObject(response.toString());
                            //int numJourneys = response.getInt("numJourneys");
                            int journeysWithOverSpeed = response.getInt("journeysWithOverSpeed");
                            String overSpeedRoad = response.getString("overSpeedRoad");
                            String roadAddress = response.getString("roadAddress");
                            int averageSpeed = 0;
                            //System.out.println(numJourneys+" num journeys\n"+journeysWithOverSpeed+"journeys");
                            if(!String.valueOf(response.get("averageSpeed")).equals("None")) {
                                averageSpeed = response.getInt("averageSpeed");
                            }
                            else{
                                 averageSpeed = 0;
                            }
                            JSONArray jsonArray = response.getJSONArray("journeys");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String endTime = jsonArray.getJSONObject(i).getString("endTime");
                                String startTime = jsonArray.getJSONObject(i).getString("startTime");
                                double startLatitude = jsonArray.getJSONObject(i).getDouble("startLatitude");
                                double startLongitude = jsonArray.getJSONObject(i).getDouble("startLongitude");
                                double endLatitude = jsonArray.getJSONObject(i).getDouble("endLatitude");
                                double endLongitude = jsonArray.getJSONObject(i).getDouble("endLongitude");

//                                Location start = new Location("start");
//                                start.setLatitude(Double.parseDouble(startLatitude));
//                                start.setLongitude(Double.parseDouble(startLongitude));
//                                System.out.println(start.getLatitude()+" _ _ _ "+start.getLongitude());
//                                Location end = new Location("end");
//                                end.setLatitude(Double.parseDouble(endLatitude));
//                                end.setLongitude(Double.parseDouble(endLongitude));
                                journeys.add(new JourneyInfo(dateWithTimeFormatter.parseDateTime(startTime), dateWithTimeFormatter.parseDateTime(endTime), startLatitude, startLongitude, endLatitude, endLongitude));
                            }
                            JSONArray overSpeedDates = response.getJSONArray("overSpeedDates");
                            for (int i = 0; i < overSpeedDates.length(); i++) {
                                String date = overSpeedDates.getJSONObject(i).getString("overSpeedDate");
                                dates.add(dateFormatter.parseDateTime(date));
                            }

////                            String date = String.valueOf(jsonObject.get("memberSince"));
//                            DateTime memberSince = dateFormatter.parseDateTime(DateTime.now().toString());


                            UserStat userStat = new UserStat(overSpeedRoad, journeys, journeysWithOverSpeed, dates, roadAddress, averageSpeed);
                            Intent myIntent = new Intent(context, UserStatActivity.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            myIntent.putExtra("userStat", userStat); //Optional parameters
                            context.startActivity(myIntent);
                        } catch (JSONException e) {
                            Log.i("user stat", e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.i("user stat", e.getMessage());
                            e.printStackTrace();
                        }
                    }
               },null) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", provider.getToken());
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };
        queue.add(jsObjRequest);
    }

    public void addOverSpeedLimits(List<OverSpeedLimit> overSpeedLimits, String journeyID, final String token) {

        Log.i("over speed size ", String.valueOf(overSpeedLimits.size()));
        for (OverSpeedLimit o : overSpeedLimits) {
            if (o.getJourneyid().equals("")) {
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", token);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
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
//                            //System.out.println("overspeed "+ str);
//                        } catch (UnsupportedEncodingException e) {
//
//                        }
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
            };

            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }

    public void getDaysSinceLastOverSpeed(final CognitoUserPoolsSignInProvider provider, final Context contextIn) {
        Log.wtf("getDaysincespeed", " called");
        final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/daysinceoverspeed?username=" + provider.getUserName();
        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Log.wtf("response ",response.toString());
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            Intent intent = new Intent();
                            intent.setAction(DAYS_OVER_SPEED);
                            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                            intent.putExtra("hello", "");
//
//                            contextIn.sendBroadcast(intent);
////                            int randomNumber = random.nextInt(5-1) + 1;
//
//
//                                Log.i("days", response.toString());
                            //                               JSONObject obj = new JSONObject(response.toString());
                            int daysSinceOverSpeed = response.getInt("daysOverSpeed");

                            prefs.edit()
                                    .putInt("daysSinceOverSpeed", daysSinceOverSpeed)
                                    .commit();
                            intent.putExtra("hello", "shaymus");

                            UserStat userStat = new UserStat();
                            JSONArray overSpeedDates = response.getJSONArray("overSpeedDates");
                            for (int i = 0; i < overSpeedDates.length(); i++) {
                                String date = String.valueOf(overSpeedDates.getJSONObject(i).get("overSpeedDate"));
                                userStat.addOverSpeedDate(dateFormatter.parseDateTime(date));
                                //dates.add(dateFormatter.parseDateTime(date));
                            }


                            intent.putExtra("overSpeedDay", userStat.getOverSpeedDay());
                            prefs.edit()
                                    .putString("overSpeedDate", userStat.getMostOverSpedDay())
                                    .commit();

                            int numTrafficIncidentsReported = response.getInt("numTrafficIncidentsReported");
                            prefs.edit()
                                    .putInt("numTrafficIncidentsReported", numTrafficIncidentsReported)
                                    .commit();


                            try {
                                JSONArray roadWithTraffic = response.getJSONArray("roadWithTraffic");
                                if (roadWithTraffic.length() > 0) {
                                    ArrayList<String> addresses = new ArrayList<>();
                                    for (int i = 0; i < roadWithTraffic.length(); i++) {
                                        String address = String.valueOf(roadWithTraffic.getJSONObject(i).get("address"));
                                        if (!addresses.contains(address)) {
                                            addresses.add(address);
                                        }
                                    }

                                    intent.putStringArrayListExtra("trafficAddresses", addresses);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                e.printStackTrace();

                            }
                            try {
                                JSONArray roadsToAvoidWithTraffic = response.getJSONArray("roadsToAvoid");
                                if (roadsToAvoidWithTraffic.length() > 0) {
                                    ArrayList<String> roadAddress = new ArrayList<>();
                                    for (int i = 0; i < roadsToAvoidWithTraffic.length(); i++) {
                                        String address = String.valueOf(roadsToAvoidWithTraffic.getJSONObject(i).get("roadAddress"));
                                        if (!roadAddress.contains(address)) {
                                            roadAddress.add(address);
                                        }
                                    }
                                    intent.putStringArrayListExtra("addresses", roadAddress);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                e.printStackTrace();

                            }
                            contextIn.sendBroadcast(intent);
                            //final Intent intent = new Intent();
                            // sets keyword to listen out for for this broadcast

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf("VOLLEY DAY JSON", e.getMessage());
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.wtf("VOLLEY DAY", e.getMessage());

                        }
                    }
                }, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", provider.getToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue.add(jsObjRequest);
    }

    public void createTrafficNotification(String trafficAddress) {
        try {
            url = "https://fcm.googleapis.com/fcm/send";
            JSONObject jsonBody = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", "Bad Traffic Reported");
            notification.put("body", trafficAddress);
            jsonBody.put("notification", notification);
            jsonBody.put("to", "/topics/trafficUpdates");

            jsonBody.toString().replace("\\\\","");
            Log.wtf("BODY", jsonBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Log.d(TAG, "Response:" + response.toString());
                            //Log.d(TAG,"Setting Response to string:\n" + response.toString());
                            Log.wtf("RESPOMSE", response.toString());
                        }
                    },
                    null) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "key=AAAADMY4DF0:APA91bF-6FzxakqhfIG1Zc2O2auOhnjSBHcIYmqm2RJVcRnT_tcE6Lz6LqLlujGQazCwh4XSDVxI_vXRrHZ10OMc9s-XeRtbenFVw4GbpqbqCMSVmEpv8T-8VxgdqiUwGLIKbASydy5h");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void getDaysSinceLastOverSpeed(final String username, final Context contextIn) {
//        Log.i("getDaysincespeed", " called");
//        final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
//
//        url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/daysinceoverspeed?username=" + username;
//        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Random random = new Random();
//                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                            Intent intent = new Intent();
////                            int randomNumber = random.nextInt(5-1) + 1;
//
//                            int randomNumber =1;
//                            if(randomNumber == 1) {
//
//                                Log.i("days", response.toString());
//                                JSONObject obj = new JSONObject(response.toString());
//                                int daysSinceOverSpeed = obj.getInt("daysOverSpeed");
//
//                                prefs.edit()
//                                        .putInt("daysSinceOverSpeed", daysSinceOverSpeed)
//                                        .commit();
//
//                                //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                                intent.setAction(DAYS_OVER_SPEED);
//                                intent.putExtra("hello", "shaymus");
//                            }
//                            else if(randomNumber == 2) {
//                                intent.setAction(OVERSPEEDDAY);
//                                UserStat userStat = new UserStat();
//                                JSONArray overSpeedDates = response.getJSONArray("overSpeedDates");
//                                for (int i = 0; i < overSpeedDates.length(); i++) {
//                                    String date = String.valueOf(overSpeedDates.getJSONObject(i).get("overSpeedDate"));
//                                    userStat.addOverSpeedDate(dateFormatter.parseDateTime(date));
//                                    //dates.add(dateFormatter.parseDateTime(date));
//                                }
//
//                                prefs.edit()
//                                        .putString("overSpeedDate", userStat.getOverSpeedDay())
//                                        .commit();
//                            }
//
//                            else if (randomNumber == 3){
//                                intent.setAction(NUMTRAFFICINCIDENTS);
//                                int numTrafficIncidentsReported = response.getInt("numTrafficIncidentsReported");
//                                prefs.edit()
//                                        .putInt("numTrafficIncidentsReported", numTrafficIncidentsReported)
//                                        .commit();
//                            }
//                            else if (randomNumber == 4){
//                                intent.setAction(ROADSWITHINCIDENTS);
//                                JSONArray roadWithTraffic = response.getJSONArray("roadWithTraffic");
//                                ArrayList<String> addresses = new ArrayList<>();
//                                for (int i = 0; i < roadWithTraffic.length(); i++) {
//                                    String address = String.valueOf(roadWithTraffic.getJSONObject(i).get("address"));
//                                    if(!addresses.contains(address)) {
//                                        addresses.add(address);
//                                    }
//                                }
//                                intent.putStringArrayListExtra("addresses", addresses);
//                            }
//                            else{
//                                intent.setAction(ROADSTOAVOID);
//                                JSONArray roadWithTraffic = response.getJSONArray("roadsToAvoid");
//                                ArrayList<String> addresses = new ArrayList<>();
//                                for (int i = 0; i < roadWithTraffic.length(); i++) {
//                                    String address = String.valueOf(roadWithTraffic.getJSONObject(i).get("roadAddress"));
//                                    if(!addresses.contains(address)) {
//                                        addresses.add(address);
//                                    }
//                                }
//                                intent.putStringArrayListExtra("addresses",addresses);
//                            }
//                            contextIn.sendBroadcast(intent);
//                            //final Intent intent = new Intent();
//                            // sets keyword to listen out for for this broadcast
//
//                        } catch (JSONException e) {
//                            Log.i("days", e.getMessage());
//                        } catch (Exception e) {
//                            Log.i("days ", e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("Sdays", "ERROR");
//                    }
//                });
//        queue.add(jsObjRequest);
//    }


//    private void sendBroadcastMessage(WeakReference<Context> weakContext, String intentFilterName) {
//        Intent intent = new Intent(intentFilterName);
//        intent.putExtra("response", "hello");
//        weakContext.get().sendBroadcast(intent);
//    }
}
//class Notification implements Serializable{
//    String title, body;
//
//    public Notification(String title, String body) {
//        this.title = title;
//        this.body = body;
//    }
//
//
//}
