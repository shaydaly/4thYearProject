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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Seamus on 26/01/2017.
 */

public class OverSpeedLimit {

    private String longitude, latitude, speedLimit;
    private String currentSpeed;
    Date currentTime;

    public OverSpeedLimit(String longitude, String latitude, String currentSpeed, String speedLimit) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.currentSpeed = currentSpeed;
        this.speedLimit = speedLimit;
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

    public void InsertOverLimitDB(Context c, String journeyID,  String user){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(c);
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

//            jsonBody.put("journeyID", "196");
//            jsonBody.put("longitude","55555");
//            jsonBody.put("latitude","66666");
//            jsonBody.put("speedTravelling", "50");
//            jsonBody.put("speedLimit","40");
//            jsonBody.put("username", "fogo1991");
//            jsonBody.put("time",currentTime);

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

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }
}
