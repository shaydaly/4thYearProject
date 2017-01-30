package com.example.seamus.carvis;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Seamus on 24/01/2017.
 */

public class Journey extends Activity{
    private String longitude;
    private String latitude;
    private double  currentSpeed;
    private String speedLimit;

    public Journey(String latitude,String longitude, double currentSpeed, String speedLimit) {
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
        this.longitude = longitude;
    }


    public Journey(){
        longitude="";
        latitude="";
        currentSpeed=0.0;
        speedLimit="";
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
private String result;
    public void setResult(String s){
        result= s;
    }

    public String getResult(){
        return result;
    }
    public String addJourneyDB(){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/createjourneyobject";
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
            currentSpeedTextView.setText("called");
            Date date = new Date();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("longitude", longitude);
            jsonBody.put("latitude", latitude);
            jsonBody.put("time",date);
            final String requestBody = jsonBody.toString();
            final

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    result = response;
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    result = error.toString();
                    currentSpeedTextView.setText(error.toString());
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
                        result = uee.toString();
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
                        result = (response.toString());
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
