package com.example.seamus.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

public class TrackSpeedActivity extends Activity {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
//    private String speedLimit = "";
//    private String longitude = "";
//    private String latitude = "";
//    private double currentSpeed = 0.0;


    Journey journey = new Journey();
    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);

        final ImageView imageView50 = (ImageView) findViewById(R.id.speed50km);
        final ImageView imageView60 = (ImageView) findViewById(R.id.speed60km);
        final ImageView imageView80 = (ImageView) findViewById(R.id.speed80km);
        final ImageView imageView100 = (ImageView) findViewById(R.id.speed100km);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.GET_PERMISSIONS) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
        }


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

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        journey.setSpeedLimit((response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //speedLimitTextView.setText("error getting speed");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "CARVIS");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();

    }
}

