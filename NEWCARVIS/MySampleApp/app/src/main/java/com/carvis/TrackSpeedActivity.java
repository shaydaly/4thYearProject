package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mysampleapp.R;
public class TrackSpeedActivity extends Activity {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
//    private String speedLimit = "";
//    private String longitude = "";
//    private String latitude = "";
//    private double currentSpeed = 0.0;

    private static Context context;
    Intent intent = getIntent();
    Journey journey = new Journey();
    CognitoUserPoolsSignInProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);




        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //journey.addJourneyDB();
        //addJourneyDB();
        context= getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);

        journey.addJourneyDB(context, provider.getUserName());

        Intent i = getIntent();
        Customer myParcelableObject = (Customer) i.getParcelableExtra("CustomerBundle");



        final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);

        System.out.println(provider.getUserName()+"__________!!!1");
        final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        final ImageView imageView50 = (ImageView) findViewById(R.id.speed50km);
        final ImageView imageView60 = (ImageView) findViewById(R.id.speed60km);
        final ImageView imageView80 = (ImageView) findViewById(R.id.speed80km);
        final ImageView imageView100 = (ImageView) findViewById(R.id.speed100km);

        LocationListener locationListener;
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                journey.setLatitude(String.valueOf(location.getLatitude()));
                journey.setLongitude(String.valueOf(location.getLongitude()));
                // Called when a new location is found by the network location provider.
                if(location.hasSpeed()==true) {
                    journey.setCurrentSpeed(Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0);
                    currentSpeedTextView.setText(String.valueOf(journey.getCurrentSpeed())+"km/h");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        String locationProvider = LocationManager.GPS_PROVIDER;
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        catch(Exception e){

        }

//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
//        }

//        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
//
//            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
//                    LocationService.ACCESS_FINE_LOCATION );
//        }


        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                getSpeedFromLambda(journey.getLatitude(), journey.getLongitude());
                ha.postDelayed(this, 5000);
                //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
                try {
                    String newSpeed= journey.getSpeedLimit().replaceAll("[^\\d.]", "");
                    //speedLimitTextView.setText(newSpeed+"km/h");
                    int limit = Integer.parseInt(newSpeed);
                    if(journey.getCurrentSpeed() > limit){
                        OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(),journey.getLongitude(),journey.getCurrentSpeed(),journey.getSpeedLimit());
                        o.InsertOverLimitDB(journey);
                    }
                    while(journey.getCurrentSpeed() > limit) {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    // Vibrate for 400 milliseconds
                        v.vibrate(1000);
                    }

                    if(limit == 50){
                        showImage(imageView50);
                    }
                    if(limit == 60){
                        showImage(imageView60);
                    }
                    if(limit == 80){
                        showImage(imageView80);
                    }
                    if(limit == 100){
                        showImage(imageView100);
                    }
                }
                catch(Exception e){
                    speedLimitTextView.setText(journey.getSpeedLimit());
                }

            }
        }, 5000);
    }

public void getSpeedFromLambda(String latitude, String longitude) {
    RequestQueue queue = Volley.newRequestQueue(this);
    String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
    final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
    JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try{
                    JSONObject obj = new JSONObject(response.toString());
                        journey.setSpeedLimit((obj.get("speed").toString()));
                }

                    catch(JSONException e){

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    journey.setSpeedLimit("NA");

                }
            });
    queue.add(jsObjRequest);
}

    public void addJourneyDB(){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createjourneyobject";
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
            Date date = new Date();
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("longitude", "9999");
            jsonBody.put("latitude", "5555");
            jsonBody.put("time",date);
            jsonBody.put("username",provider.getUserName());
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
                        //result = uee.toString();
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

    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

}

