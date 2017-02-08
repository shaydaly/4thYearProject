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
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mysampleapp.R;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class TrackSpeedActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static Context context;
    Intent intent = getIntent();
    Journey journey;
    CognitoUserPoolsSignInProvider provider;
    int count = 0;


    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);

        journey = new Journey();


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);

        //Intent i = getIntent();
        //Customer myParcelableObject = (Customer) i.getParcelableExtra("CustomerBundle");


        final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        final ImageView imageView50 = (ImageView) findViewById(R.id.speed50km);
        final ImageView imageView60 = (ImageView) findViewById(R.id.speed60km);
        final ImageView imageView80 = (ImageView) findViewById(R.id.speed80km);
        final ImageView imageView100 = (ImageView) findViewById(R.id.speed100km);

        //Testing new location


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

// end new location
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                getSpeedFromLambda(journey.getLatitude(), journey.getLongitude());
                ha.postDelayed(this, 5000);
                //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
                try {
                    String newSpeed = journey.getSpeedLimit().replaceAll("[^\\d.]", "");

                    //speedLimitTextView.setText(newSpeed+"km/h");
                    int limit = Integer.parseInt(newSpeed);

                    if (journey.getCurrentSpeed() > limit) {
                        if (!journey.getJourneyID().equals("")) {
                            //currentSpeedTextView.setText(jID);
                            OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit));
                            o.InsertOverLimitDB(context, journey.getJourneyID(), provider.getUserName());
                        }
                    }
//                    while(journey.getCurrentSpeed() > limit) {
////                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//////                    // Vibrate for 400 milliseconds
////                        v.vibrate(1000);
//                    }

                    if (limit == 50) {
                        showImage(imageView50);
                    }
                    if (limit == 60) {
                        showImage(imageView60);
                    }
                    if (limit == 80) {
                        showImage(imageView80);
                    }
                    if (limit == 100) {
                        showImage(imageView100);
                    }
                } catch (Exception e) {
                    speedLimitTextView.setText(journey.getSpeedLimit());
                }
            }
        }, 5000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onConnected(Bundle bundle) {

        try {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        1);
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }

        } catch (Exception e) {
            System.out.println("lalala");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        final TextView currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        journey.setLatitude(String.valueOf(location.getLatitude()));
        journey.setLongitude(String.valueOf(location.getLongitude()));
        // Called when a new location is found by the network location provider.
        if (location.hasSpeed() == true) {
            journey.setCurrentSpeed(Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0);
            currentSpeedTextView.setText(String.valueOf(journey.getCurrentSpeed()) + "km-h");
        }
        if (count == 0) {
            journey.addJourneyDB(context, provider.getUserName());
            count++;
        }
    }

    public void getSpeedFromLambda(String latitude, String longitude) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
        final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            journey.setSpeedLimit((obj.get("speed").toString()));
                        } catch (JSONException e) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        mLocationRequest = LocationRequest.create();
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        mLocationRequest.setInterval(1000); // Update location every second

                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, this);
                    } catch (Exception e) {

                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

}

