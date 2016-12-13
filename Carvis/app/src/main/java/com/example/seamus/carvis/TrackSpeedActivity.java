package com.example.seamus.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    public static final double SPEED_LIMIT = 25.0;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    String maxSpeed = "";
    static boolean locationChecking = false;
    String speedLimit = "";
    String longitude = "";
    String latitude = "";
    int count = 0;

    LocationService locationService = new LocationService();
    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                getSpeedFromLambda(latitude, longitude);
                ha.postDelayed(this, 5000);
                //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);


                speedLimitTextView.setText(speedLimit);
//                if(!speedLimit.equals("\"Unknown\"")) {
//                    speedLimitTextView.setText(speedLimit.replaceAll("[^\\d.]", ""));
//                }
//                else{
//                    speedLimitTextView.setText(speedLimit);
//                }

            }
        }, 5000);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                count++;

                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                //makeHttpRequest( String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                // Called when a new location is found by the network location provider.
                //TextView textView = (TextView) findViewById(R.id.currentSpeed);
                //TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
                double kilomPerHour = Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0;
                currentSpeedTextView.setText(String.valueOf(kilomPerHour) + "km/h");
                //getSpeedFromLambda( latitude,longitude );

//                if (!speedLimit.contains("N")) {
//                    speedLimit = speedLimit.replaceAll("\\D+", "");
//                    speedLimit = speedLimit + " km/h";
//                }


//                if(kilomPerHour >SPEED_LIMIT){
//                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//                    // Vibrate for 400 milliseconds
//                    v.vibrate(5000);
//
////                    ImageView im = (ImageView) findViewById(R.id.stopSign);
////                    im.setVisibility(View.VISIBLE);
//                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_PERMISSIONS) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }

    }

    public void getSpeedFromLambda(String latitude, String longitude) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setSpeedLimit(response);
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

    public void setSpeedLimit(String response) {
        speedLimit = response;
    }

    public void showImage(View view) {
//        ImageView im = (ImageView) findViewById(R.id.stopSign);
//        im.setVisibility(View.INVISIBLE);
    }
}

