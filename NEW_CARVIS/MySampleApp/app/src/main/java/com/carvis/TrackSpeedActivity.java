package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.NotificationCompat;
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
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
//import com.carvis.SpeedCheckService.SpeedLocalBinder;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class TrackSpeedActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //  SpeedCheckService speedCheckService;
    boolean isBound = false;

    private Context context;
    //Intent intent = getIntent();
    Journey journey;
    //JourneyFragment journeyFragment;
    CognitoUserPoolsSignInProvider provider;
    boolean intialMovement;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private List<JourneyFragment> journeyList;

//    ImageView imageView50;
//    ImageView imageView60;
//    ImageView imageView80;
//    ImageView imageView100;

    TextView currentSpeedTextView;
    TextView speedLimitTextView;


    int limit;
    private Date dNow;
    int speed;
    private RequestQueue queue;

    VolleyService volleyService;

    private Location test;
    //private Timer timer , timer2;

    //    HashSet<SpeedCamera> cameras;
    FirebaseDatabase database;
    DatabaseReference vanRef;
    DatabaseReference cameraRef;
    DatabaseReference speedref;
    //ArrayList<Road> roads;
    ArrayList<OverSpeedLimit> overSpeedLimits;
    ArrayList<RoadRecord> record;

    HashMap<Integer, ArrayList<RoadRecord>> roadHashMap;
    //boolean speeding;

    //Location trackSpeedLocation;
    boolean isRunning;
    SpeedSearch speedSearch;
    ExecutorService mThreadPool;
    ScheduledExecutorService ses;

    Handler speedLimitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            currentSpeedTextView.setText(String.valueOf(speed + "km-h"));
        }
    };


    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    Handler speedVanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle summaryBundle = msg.getData();
            SpeedCamera s = (SpeedCamera)summaryBundle.get("SpeedCamera");

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.speed_camera_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));

            TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
            text.setText(SpeedCamera.getSpeedCameraAddress(context, s.getStartLatitude(), s.getEndLongitude()));

            TextView lastLocated = (TextView) layout.findViewById(R.id.lastSpotted);
            if (s.getReportedTimes().size() != 0) {
                lastLocated.setText(s.getReportedTimes().get(s.getReportedTimes().size() - 1));
            }
            TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
            header.setText("Speed Van Nearby");

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    };


    Handler speedCameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle summaryBundle = msg.getData();
            TemporarySpeedCamera t = (TemporarySpeedCamera)summaryBundle.get("TemporarySpeedCamera");

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.speed_camera_toast,
                    (ViewGroup) findViewById(R.id.custom_toast_container));
            TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
            header.setText("Speed Camera Nearby");


            TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
            text.setText(SpeedCamera.getSpeedCameraAddress(context, t.getLatitude(), t.getLongitude()));


            TextView lastLocated = (TextView) layout.findViewById(R.id.lastSpotted);

            lastLocated.setText(t.getTime());


            Toast toast = new Toast(context);
            toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    };

    Handler firebaseSpeedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    Handler overSpeedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Integer.parseInt(journey.getCurrentSpeed()) > Integer.parseInt(journey.getSpeedLimit())) {
                overSpeedLimits.add(new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()),
                        String.valueOf(limit), dNow, provider.getUserName(), journey.getJourneyID(),speedSearch.getOsm_id()));
            }
        }
    };


//    private Handler handler;
//    private Runnable runnable;
//
//    private Handler journeyFragmentHandler;
//    private Runnable journeyFragmentRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);
        journey = new Journey();
        journeyList = new ArrayList<>();
        overSpeedLimits = new ArrayList<>();
        //speeding = false;

        roadHashMap = new HashMap<>();
        isRunning = false;
        intialMovement = true;
        test = new Location("location test");

        mThreadPool = Executors.newSingleThreadExecutor();



//        timer = new Timer();
//        timer2 = new Timer();

        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        queue = Volley.newRequestQueue(context);

        volleyService = new VolleyService(context);

        //roads = new ArrayList<>();
        speedSearch = new SpeedSearch(-99);
        final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // cameras = new HashSet<>();
        Button clickButton = (Button) findViewById(R.id.addSpeedCamera);
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = ft.format(dNow);
                TemporarySpeedCamera.addTemporaryCamera(journey.getLatitude(), journey.getLongitude(), time, context);
            }
        });

//        Button speedVan = (Button) findViewById(R.id.speedvan);
//        speedVan.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                SpeedCamera.addSpeedVanLocatedRecord(database);
//            }
//        });

//        imageView50 = (ImageView) findViewById(R.id.speed50km);
//        imageView60 = (ImageView) findViewById(R.id.speed60km);
//        imageView80 = (ImageView) findViewById(R.id.speed80km);
//        imageView100 = (ImageView) findViewById(R.id.speed100km);
//FirebaseDatabase
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(context);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        vanRef = database.getReference("speedVans");
        vanRef.keepSynced(true);
        cameraRef = database.getReference("reportedSpeedCameras");
        cameraRef.keepSynced(true);
        speedref = database.getReference("speedLimits");
        speedref.keepSynced(true);

        speedref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                record = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    RoadRecord r = new RoadRecord(
                            String.valueOf(postSnapshot.child("latitude").getValue()),
                            String.valueOf(postSnapshot.child("longitude").getValue()),
                            Integer.parseInt(String.valueOf(postSnapshot.child("speedLimit").getValue())));
                    record.add(r);
                }
                //System.out.println(dataSnapshot.getKey()+" KEY");
                //Road road = new Road(Integer.parseInt(dataSnapshot.getKey()), record);
                //roads.add(new Road(Integer.parseInt(dataSnapshot.getKey()), record));
                roadHashMap.put(Integer.parseInt(dataSnapshot.getKey()), record);
                // System.out.println(r.getLatitude());
//                HashMap<String, String> reports = (HashMap<String, String>) dataSnapshot.getChildren();
//                if(reports!=null) {
//                    Iterator it = reports.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry) it.next();
//                        System.out.println(pair.getKey()+" _ "+pair.getValue());
//                        it.remove(); // avoids a ConcurrentModificationException
//                    }
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


////                cameras.remove(dataSnapshot.getKey());
////                System.out.println(cameras.size());
//
//                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
//                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
//                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
//                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
//                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
//                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//
//                System.out.println(SpeedCamera.cameras.size());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                //cameras.remove(dataSnapshot.getKey());
//                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
//                System.out.println("camera size : "+SpeedCamera.cameras.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        vanRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                //System.out.println(dataSnapshot.child("startLatitude").getValue());
                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
                //System.out.println(String.valueOf(dataSnapshot.child("reportedTimes").getValue()));
                HashMap<String, String> reports = (HashMap<String, String>) dataSnapshot.child("Reported Times").getValue();
                ArrayList<String> dates = new ArrayList<String>();
                if (reports != null) {
                    Iterator it = reports.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        dates.add(String.valueOf(pair.getValue()));
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
                SpeedCamera.addSpeedCamera(new SpeedCamera(id, startLat, startLong, endLat, endLong, dates));
                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


//                cameras.remove(dataSnapshot.getKey());
//                System.out.println(cameras.size());

                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));

                //System.out.println(SpeedCamera.getCameras().size());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //cameras.remove(dataSnapshot.getKey());
                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
                // System.out.println("camera size : "+SpeedCamera.getCameras().size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        cameraRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                String time = String.valueOf(dataSnapshot.child("time").getValue());

                TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(latitude, longitude, time));
                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
                //System.out.println(TemporarySpeedCamera.temporarySpeedCameras.size()+" is the temp size");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


////                cameras.remove(dataSnapshot.getKey());
////                System.out.println(cameras.size());
//
//                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
//                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
//                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
//                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
//                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
//                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//
//                System.out.println(SpeedCamera.cameras.size());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                //cameras.remove(dataSnapshot.getKey());
//                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
//                System.out.println("camera size : "+SpeedCamera.cameras.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        ses = Executors.newScheduledThreadPool(10);
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // do some work

                try {
                    nearKnownSpeedLimit(speedSearch);
                } catch (Exception e) {
                    Log.i("Get Limit Exception", e.getMessage());
                }
            }
        }, 0, 5, TimeUnit.SECONDS);  // execute every x seconds

        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // do some work

                try {
                    try {
                        dNow = new Date();
                        if (!journey.getSpeedLimit().equals("NA")) {
                            limit = Integer.parseInt(journey.getSpeedLimit());
                        } else {
                            limit = 0;
                        }

                        journeyList.add(new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), String.valueOf(limit), dNow, journey.getJourneyID(), provider.getUserName()));
                        if (journeyList.size() == 50) {
//                            JourneyFragment.AddJourneyFragments(queue, journeyList, journey.getJourneyID());
                            volleyService.addJourneyFragments(journeyList,journey.getJourneyID());
                            journeyList.clear();
                        }
                        //System.out.println(cameras.size());
                    } catch (Exception e) {
                        Log.i("Get Limit Exception", e.getMessage());
                    }
                } catch (Exception e) {
                    Log.i("Get Limit Exception", e.getMessage());
                }
            }
        }, 0, 15, TimeUnit.SECONDS);  // execute every x seconds

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
        endJourneys();
        ses.shutdown();

        //handler.removeCallbacks(runnable);
        //journeyFragmentHandler.removeCallbacks(journeyFragmentRunnable);
//        timer.cancel();
//        timer.purge();
//        if (queue != null) {
//            System.out.println(queue + " is not empty so cancelling");
//            queue.cancelAll(this);
//        }
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
    public void onLocationChanged(final Location location) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //trackSpeedLocation = location;
                    journey.setLatitude(String.valueOf(location.getLatitude()));
                    journey.setLongitude(String.valueOf(location.getLongitude()));
                    // Called when a new location is found by the network location provider.
                    if (location.hasSpeed() == true) {
                        speed = (int) (Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0);
                        journey.setCurrentSpeed(String.valueOf(speed));
                        speedLimitHandler.sendEmptyMessage(0);
//                if (speed > Integer.parseInt(journey.getSpeedLimit())) {
//                    currentSpeedTextView.setTextColor(Color.RED);
//                } else {
//                    currentSpeedTextView.setTextColor(Color.WHITE);
//                }
                        //journey.setSpeedLimit(String.valueOf(nearKnownSpeedLimit(location)));

                    }

//                    if (nearSpeedCamera(location)){
//
//                    }

                    if (intialMovement == true) {
                        //journey.addJourneyDB(queue, provider.getUserName(), "insert");
                        volleyService.addJourneyDB(journey, provider.getUserName(),"insert");
                        intialMovement = false;
                    }

                    speedSearch.setLocation(location);


                    try {
                        if (Integer.parseInt(journey.getCurrentSpeed()) > limit && limit != 0) {
                            //speeding = true;
                            if(!isRunning)
                            {
                                setOverSpeedLimit();
                            }
                            //speedCheckService.wait10Seconds();

                            //currentSpeedTextView.setText(jID);
//                    OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit));
//                    o.InsertOverLimitDB(queue, journey.getJourneyID(), provider.getUserName());
                            // overSpeedLimits.add(new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit), dNow, provider.getUserName(), journey.getJourneyID()));
                        }

                    } catch (Exception e) {
                        Log.i("Except", e.getMessage());
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

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
//    @Override
//    protected void onDestroy() {
//        this.unbindService(SpeedCheckService);
//        super.onDestroy();
//    }


    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public void endJourneys() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //journey.addJourneyDB(queue, provider.getUserName(), "update");
                volleyService.addJourneyDB(journey, provider.getUserName(),"update");
//                JourneyFragment.AddJourneyFragments(queue, journeyList, journey.getJourneyID());
                volleyService.addJourneyFragments(journeyList, journey.getJourneyID());
                OverSpeedLimit.addOverSpeedLimits(queue, context, overSpeedLimits, journey.getJourneyID(), provider.getUserName());
                updateHandler.sendEmptyMessage(0);
            }
        };

        Thread endThread = new Thread(runnable);
        endThread.start();
    }

    public boolean nearSpeedCamera(Location location) {

//        Location cameraStart;
//        Location cameraEnd;
//        Location cameraMiddle;
        Location cameraLocation;


//        Iterator<SpeedCamera> iterator = SpeedCamera.getCameras().iterator();
//        SpeedCamera min = (SpeedCamera) iterator.next();
//        SpeedCamera test;
//        while(iterator.hasNext()){
//            Iterator<Location> locations = iterator.next().getCameraLocations().iterator();
//            while(locations.hasNext()){
//                if(locations.next().distanceTo(location))
//            }
//        }


        for (SpeedCamera s : SpeedCamera.getCameras()) {
            ArrayList<Location> cameraLocations = s.getCameraLocations();
            for (int i = 0; i < cameraLocations.size(); i++) {
                if (location.distanceTo(cameraLocations.get(i)) / 1000 < 0.1) {
                    //displaySpeedVanInfo(s);
                    Message message = new Message();
                    Bundle b = new Bundle();
                    b.putSerializable("SpeedCamera",s);
                    message.setData(b);
                    message.arg1 = 0;
                    speedVanHandler.sendMessage(message);
                    System.out.println(cameraLocations.get(i).getLatitude() + " _ _ _ " + cameraLocations.get(i).getLongitude());
                    return true;
                }
            }
        }
        for (TemporarySpeedCamera t : TemporarySpeedCamera.temporarySpeedCameras) {
            cameraLocation = new Location("Camera Location");
            cameraLocation.setLatitude(t.getLatitude());
            cameraLocation.setLongitude(t.getLongitude());
            if ((location.distanceTo(cameraLocation) / 1000) < 1.0) {
                System.out.println("TEMPORARY SPEED CAMERA NEAR");
                //displaySpeedCameraInfo(t);
                Message message = new Message();
                Bundle b = new Bundle();
                b.putSerializable("TemporarySpeedCamera",t);
                message.setData(b);
                message.arg1 = 0;
                speedCameraHandler.sendMessage(message);
                return true;
            }
        }
        return false;
    }

    public void displaySpeedVanInfo(final SpeedCamera s) {

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.speed_camera_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
                text.setText(SpeedCamera.getSpeedCameraAddress(context, s.getStartLatitude(), s.getEndLongitude()));

                TextView lastLocated = (TextView) layout.findViewById(R.id.lastSpotted);
                if (s.getReportedTimes().size() != 0) {
                    lastLocated.setText(s.getReportedTimes().get(s.getReportedTimes().size() - 1));
                }
                TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
                header.setText("Speed Van Nearby");

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();


    }

//    public void displaySpeedCameraInfo(final TemporarySpeedCamera t) {
//
//                LayoutInflater inflater = getLayoutInflater();
//                View layout = inflater.inflate(R.layout.speed_camera_toast,
//                        (ViewGroup) findViewById(R.id.custom_toast_container));
//                TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
//                header.setText("Speed Camera Nearby");
//
//
//                TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
//                text.setText(SpeedCamera.getSpeedCameraAddress(context, t.getLatitude(), t.getLongitude()));
//
//
//                TextView lastLocated = (TextView) layout.findViewById(R.id.lastSpotted);
//
//                lastLocated.setText(t.getTime());
//
//
//                Toast toast = new Toast(context);
//                toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL, 0, 0);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.setView(layout);
//                toast.show();
//
////        LayoutInflater inflater = getLayoutInflater();
////        View layout = inflater.inflate(R.layout.speed_camera_toast,
////                (ViewGroup) findViewById(R.id.custom_toast_container));
////        TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
////        header.setText("Speed Camera Nearby");
////
////
////        TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
////        text.setText(SpeedCamera.getSpeedCameraAddress(context, t.getLatitude(), t.getLongitude()));
////
////
////        TextView lastLocated = (TextView) layout.findViewById(R.id.lastSpotted);
////
////        lastLocated.setText(t.getTime());
////
////
////        Toast toast = new Toast(context);
////        toast.setGravity(Gravity.DISPLAY_CLIP_VERTICAL, 0, 0);
////        toast.setDuration(Toast.LENGTH_SHORT);
////        toast.setView(layout);
////        toast.show();
//    }

//    public void createNotification() {
//        // Prepare intent which is triggered if the
//        // notification is selected
//        Intent intent = new Intent(this, NotificationActivityReceiver.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
//
//        // Build notification
//        // Actions are just fake
//        Notification noti = new Notification.Builder(this)
//                .setContentTitle("New mail from " + "test@gmail.com")
//                .setSmallIcon(R.drawable.com_facebook_button_icon)
//                .setContentIntent(pIntent).build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, noti);
//    }



    AlertDialog alertDialog;

    public void showAlertDialog(final String id) {

//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
//                    .setTitle("Speed Van Location")
//                    .setMessage("Is there a speed Van in location?")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            SpeedCamera.addSpeedVanLocatedRecord(database, id);
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert);
//
//            alertDialog = dialogBuilder.create();
//            alertDialog.show();

//        if(!alertDialog.isShowing()){
//            alertDialog.show();
//        }
    }

    public void nearKnownSpeedLimit(final SpeedSearch speedSearch) {
        try {
            if (roadHashMap.containsKey(speedSearch.getOsm_id())) {
                for (RoadRecord r : roadHashMap.get(speedSearch.getOsm_id())) {
                    test.setLatitude(Double.parseDouble(r.getLatitude()));
                    test.setLongitude(Double.parseDouble(r.getLongitude()));
                    if (speedSearch.getLocation().distanceTo(test) / 1000 <= 0.015) {
                        Log.i("speed Test log 1", "near");
                        // speedLimit =  roadRecords.get(i).getSpeedLimit();
                        journey.setSpeedLimit(String.valueOf(r.getSpeedLimit()));
                        speedLimitTextView.setText(journey.getSpeedLimit() + " km/h");
                        //handler.sendEmptyMessage(0);
                        return;
                    }
                }
            }

            for (Map.Entry<Integer, ArrayList<RoadRecord>> entry : roadHashMap.entrySet()) {
                for (RoadRecord r : entry.getValue()) {
                    test.setLatitude(Double.parseDouble(r.getLatitude()));
                    test.setLongitude(Double.parseDouble(r.getLongitude()));
                    if (speedSearch.getLocation().distanceTo(test) / 1000 <= 0.015) {
                        Log.i("speed Test log 2", "near");
                        // speedLimit =  roadRecords.get(i).getSpeedLimit();
                        speedSearch.setOsm_id(entry.getKey());
                        journey.setSpeedLimit(String.valueOf(r.getSpeedLimit()));
                        speedLimitTextView.setText(journey.getSpeedLimit() + " km/h");
                        //handler.sendEmptyMessage(0);
                        return;
                    }
                }
                // System.out.println(entry.getKey() + "/" + entry.getValue());
            }
            //handler.sendEmptyMessage(0);
            //journey.getSpeedFromLambda(database, queue, speedSearch);
            volleyService.getSpeedFromLambda(database,speedSearch,journey.getLatitude(),journey.getLongitude());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


//    private ServiceConnection speedCheckConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder service) {
//            SpeedCheckService.SpeedLocalBinder binder = (SpeedCheckService.SpeedLocalBinder) service;
//            speedCheckService = binder.getService();
//            isBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            isBound = false;
//        }
//    };

    public void setOverSpeedLimit() {
        isRunning = true;
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    long futureTime = System.currentTimeMillis() + 5000;
                    while (System.currentTimeMillis() < futureTime) {
                        synchronized (this) {
                            try {
                                wait(futureTime - System.currentTimeMillis());
                            }
                            catch (Exception e) {

                            }
                        }
                    }
                    overSpeedHandler.sendEmptyMessage(0);
                    isRunning = false;
                }

            };
            Thread t = new Thread(r);
            t.start();

    }
}






