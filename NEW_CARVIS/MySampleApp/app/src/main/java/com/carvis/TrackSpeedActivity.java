package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    HashSet<SpeedCamera> cameras;
    FirebaseDatabase database;
    DatabaseReference myRef;

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

        cameras = new HashSet<>();

        Button clickButton = (Button) findViewById(R.id.addSpeedCamera);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TemporarySpeedCamera.addTemporaryCamera(journey.getLatitude(),journey.getLongitude(),dNow,context);
            }
        });

//        imageView50 = (ImageView) findViewById(R.id.speed50km);
//        imageView60 = (ImageView) findViewById(R.id.speed60km);
//        imageView80 = (ImageView) findViewById(R.id.speed80km);
//        imageView100 = (ImageView) findViewById(R.id.speed100km);
//
        FirebaseApp.initializeApp(context);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        // Attach a listener to read the data at our posts reference
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot s : dataSnapshot.getChildren()){
//                    System.out.println("datasnapshot");
//                    int id = Integer.parseInt(s.getKey());
//                    double startLat = Double.parseDouble(String.valueOf(s.child("startLatitude").getValue()));
//                    double startLong = Double.parseDouble(String.valueOf(s.child("startLongitude").getValue()));
//                    double endLat = Double.parseDouble(String.valueOf(s.child("endLatitude").getValue()));
//                    double endLong = Double.parseDouble(String.valueOf(s.child("endLongitude").getValue()));
////                    System.out.println(String.valueOf(s.getKey())+" __");
////                    System.out.println(s.child("startLatitude").getValue());
////                    System.out.println(s.child("startLongitude").getValue());
////                    System.out.println(s.child("endLatitude").getValue());
////                    System.out.println(s.child("endLongitude").getValue());
//                    cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
                    double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
                    double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
                    double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
                    cameras.add(new SpeedCamera(startLat, startLong, endLat, endLong));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
//
//        //myRef.child("NEW CAMERA").setValue("Located There");
//        System.out.println("FireBase:::::"+myRef.child("NEW CAMERA").getRef());


        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                try {
                    if(!journey.getLongitude().equals("")) {
                        getSpeedFromLambda(journey.getLatitude(), journey.getLongitude());
                    }
                    dNow  = new Date();

                    //journeyFragment = new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName());
                    if(!journey.getJourneyID().equals("")){
                        journeyList.add(new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName()));
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
                     }
                    System.out.println(cameras.size());
                }
                catch(Exception e){
                    Log.i("Get Limit Exception",e.getMessage());
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
//        Location locationCamera = new Location("pointB");
//        locationCamera.setLatitude(53.351616);
//        locationCamera.setLongitude(-6.385280);
//        float distance = (location.distanceTo(locationCamera))/1000;
        if(nearSpeedCamera(location)){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                 v.vibrate(1000);

            System.out.println(location.toString());
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
                            JSONObject obj = new JSONObject(response.toString());
                            journey.setSpeedLimit((obj.get("speed").toString()));
                            //speedLimitTextView.setText(newSpeed+"km/h");
                            System.out.println(journey.getSpeedLimit());
                            if(!journey.getSpeedLimit().equals("NA")) {
                                limit = Integer.parseInt(journey.getSpeedLimit());
                            }

                            speedLimitTextView.setText(journey.getSpeedLimit());


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

    public boolean nearSpeedCamera(Location location){
        for(SpeedCamera s : cameras){
            Location cameraStart = new Location("startLatitude");
            Location cameraEnd = new Location("endLatitude");
            cameraStart.setLatitude(s.getStartLatitude());
            cameraStart.setLongitude(s.getStartLongitude());
            cameraEnd.setLatitude(s.getEndLatitude());
            cameraEnd.setLongitude(s.getEndLongitude());
            if((location.distanceTo(cameraStart)/1000) < .5 || (location.distanceTo(cameraEnd)/1000) < .5){
                System.out.println("WARNING NEAR CAMERA :"+s.getStartLatitude()+"_"+ s.getStartLongitude());
                displaySpeedCameraInfo(s);
                return true;
            }
        }
        return false;
    }

    public void displaySpeedCameraInfo(SpeedCamera s){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.speed_camera_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
        text.setText(s.getSpeedCameraAddress(context));

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}

