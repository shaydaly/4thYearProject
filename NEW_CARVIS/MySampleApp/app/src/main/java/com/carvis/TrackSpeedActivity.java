package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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


    private  Context context;
    //Intent intent = getIntent();
    Journey journey;
    //JourneyFragment journeyFragment;
    CognitoUserPoolsSignInProvider provider;
    int count = 0;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");



    List<JourneyFragment> journeyList;

    ImageView imageView50;
    ImageView imageView60 ;
    ImageView imageView80;
    ImageView imageView100 ;

    TextView currentSpeedTextView;
    TextView speedLimitTextView;
    int limit;
    Date dNow;
    String newSpeed;
    int speed;
    RequestQueue queue;
    private Timer timer ;

    Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);
        journey = new Journey();
        journeyList  = new ArrayList<>();

        timer = new Timer();

        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);

//        imageView50 = (ImageView) findViewById(R.id.speed50km);
//        imageView60 = (ImageView) findViewById(R.id.speed60km);
//        imageView80 = (ImageView) findViewById(R.id.speed80km);
//        imageView100 = (ImageView) findViewById(R.id.speed100km);
        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                try {
                    getSpeedFromLambda(journey.getLatitude(), journey.getLongitude());
                    newSpeed = journey.getSpeedLimit().replaceAll("[^\\d.]", "");

                    //speedLimitTextView.setText(newSpeed+"km/h");
                    limit = Integer.parseInt(newSpeed);

                    //speedLimitTextView.setText(String.valueOf(limit)+"km/h");
                    dNow  = new Date();

                    //journeyFragment = new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName());

                    journeyList.add(new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName()));

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
                }
                catch(Exception e){
                    Log.i("Get Limit"," Exception");
                }
            }
        }, 0, 5000);//5 seconds


//        final Handler ha = new Handler();
//        ha.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                System.out.println("RAN");
//                //call function
//                getSpeedFromLambda(journey.getLatitude(), journey.getLongitude());
//                //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
//                ha.postDelayed(this, 3000);
//                try {
//                    newSpeed = journey.getSpeedLimit().replaceAll("[^\\d.]", "");
//
//                    //speedLimitTextView.setText(newSpeed+"km/h");
//                    limit = Integer.parseInt(newSpeed);
//
//                    dNow  = new Date();
//
//                    journeyFragment = new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName());
//
//                    journeyList.add(journeyFragment);
//
//                    if (limit == 50) {
//                        showImage(imageView50);
//                    }
//                    if (limit == 60) {
//                        showImage(imageView60);
//                    }
//                    if (limit == 80) {
//                        showImage(imageView80);
//                    }
//                    if (limit == 100) {
//                        showImage(imageView100);
//                    }
//                }
//                catch(Exception e){
//                    Log.i("Get Limit","");
//                }
//            }
//
//        }, 3000);
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
        super.onStop();
        timer.cancel();
        timer.purge();
        if (queue != null) {
            System.out.println(queue+" is not empty so cancelling");
            queue.cancelAll(this);
        }
        endJourneys();
        mGoogleApiClient.disconnect();
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

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000); // Update location every second

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            System.out.println(e.getMessage());
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

        journey.setLatitude(String.valueOf(location.getLatitude()));
        journey.setLongitude(String.valueOf(location.getLongitude()));
        // Called when a new location is found by the network location provider.
        if (location.hasSpeed() == true) {
            speed = (int)(Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0);
            journey.setCurrentSpeed(String.valueOf(speed));
            currentSpeedTextView.setText(String.valueOf(journey.getCurrentSpeed()) + "km-h");
        }
        if (count == 0) {
            journey.addJourneyDB(context, provider.getUserName(),"insert");
            count++;
        }
        try {
//            if (Double.parseDouble(journey.getCurrentSpeed()) > limit) {
//                if (!journey.getJourneyID().equals("")) {
//                    //currentSpeedTextView.setText(jID);
//                    //OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit));
//                    //o.InsertOverLimitDB(context, journey.getJourneyID(), provider.getUserName());
//                }
//            }

        } catch (Exception e) {
            //speedLimitTextView.setText(journey.getSpeedLimit());
        }
    }


    public void getSpeedFromLambda(String latitude, String longitude) {
        System.out.println("GET SPEED CALLED");
        queue  = Volley.newRequestQueue(context);
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/QuerySpeed/callqueryspeed?latitude=" + latitude + "&longitude=" + longitude;
        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println("response from getspeed\n"+response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            journey.setSpeedLimit((obj.get("speed").toString()));
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
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

    public void endJourneys() {
        System.out.println("end journeys called");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                journey.addJourneyDB(context, provider.getUserName(), "update");
                JourneyFragment.AddJourneyFragments(context, journeyList);
                updateHandler.sendEmptyMessage(0);
                //updateHandler.sendEmptyMessage(0);
            }
        };

        Thread endThread = new Thread(runnable);
        endThread.start();
    }

}

