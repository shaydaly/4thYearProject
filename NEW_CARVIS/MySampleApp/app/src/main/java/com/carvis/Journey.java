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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Seamus on 24/01/2017.
 */

public class Journey extends Activity {
    private String journeyID;
    private String longitude;
    private String latitude;
    private double  currentSpeed;
    private String speedLimit;
    private Date startTime;
    private Date endTime;
    List<JourneyFragment> journeyFragmentList;

    CognitoUserPoolsSignInProvider provider;

    public Journey(String latitude, String longitude, double currentSpeed, String speedLimit, Date start, Date end) {
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
        this.startTime = start;
        this.endTime = end;
        journeyFragmentList = new ArrayList<>();

    }

    public Journey(String latitude, String longitude, double currentSpeed, String speedLimit) {
        journeyID="";
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
    }

    public Journey(){
        journeyID="";
        longitude="";
        latitude="";
        currentSpeed=0.0;
        speedLimit="";
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

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void setSpeedLimit(String speedLimit) {
        this.speedLimit = speedLimit;
    }


    public String getSpeedLimit() {
        return speedLimit;
    }

    public double getCurrentSpeed() {
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

    final ArrayList<String> mEntries = new ArrayList<>();
    public ArrayList<String> getUsersJourneys(Context c, String username){

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
                                mEntries.add(jsonObject.toString());
                                System.out.println(jsonObject.toString());
                            }
                            catch(JSONException e) {
                                mEntries.add("Error: " + e.getLocalizedMessage());
                            }
                        }

                       // allDone();
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

        return mEntries;
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
