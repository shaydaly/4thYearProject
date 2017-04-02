package com.carvis;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private boolean isRunning, intialMovement, isPlayingVoice;
    private int speed, limit;
    private  String locale;
    Date dNow;
    private Location test;


    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    FirebaseDatabase database;
    DatabaseReference vanRef;
    DatabaseReference cameraRef;
    DatabaseReference speedref;
    Context context;
    ExecutorService mThreadPool;
    private Journey journey;
    Intent currentSpeedIntent, speedLimitIntent ;
    VolleyService volleyService;
    CognitoUserPoolsSignInProvider provider;
    private SpeedSearch speedSearch;
    ScheduledExecutorService ses;


    private List<JourneyFragment> journeyList;
    ArrayList<OverSpeedLimit> overSpeedLimits;
    ArrayList<RoadRecord> record;

    HashMap<Integer, ArrayList<RoadRecord>> roadHashMap;

    public MyLocationService() {
    }

    public final String TAG = "shay";

    Handler overSpeedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (journey.getCurrentSpeed() > journey.getSpeedLimit()) {
                OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(),
                        (limit), dNow, provider.getUserName(), journey.getJourneyID(), speedSearch.getOsm_id());
                if (!overSpeedLimits.contains(o)) {
                    overSpeedLimits.add(o);
                }
            }
        }
    };




    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "started");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run");
                mThreadPool = Executors.newSingleThreadExecutor();

                Log.i(TAG, "startCommand");

            }
        };
        Thread thread = new Thread(r);
        thread.start();
        return Service.START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        Log.i(TAG, "created");

        FirebaseApp.initializeApp(context);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        volleyService = new VolleyService(context);
        provider = new CognitoUserPoolsSignInProvider(context);
        speedSearch = new SpeedSearch(-99);
        isPlayingVoice = false;
        final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        locale = PreferenceManager.getDefaultSharedPreferences(context).getString("locale", null);

        journey = new Journey();
        overSpeedLimits = new ArrayList<>();
        journeyList = new ArrayList<>();
        roadHashMap = new HashMap<>();
        intialMovement = true;
        test = new Location("location test");
        createSpeedVanReference();
        createSpeedCameraReference();
        createSpeedLimitReference(locale);

        //setContentView(mLocationView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        currentSpeedIntent = new Intent();
        speedLimitIntent = new Intent();

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
                try {
                    try {
                        dNow = new Date();
//                        if (journey.getSpeedLimit() != -99) {
//                            limit = (journey.getSpeedLimit());
//                        } else {
//                            limit = 0;
//                        }
                        limit = journey.getSpeedLimit();
                        if (limit != 0) {
                            journeyList.add(new JourneyFragment(journey.getLatitude(), journey.getLongitude(), journey.getCurrentSpeed(), limit, dNow, journey.getJourneyID(), provider.getUserName()));
                        }
                        if (journeyList.size() == 50) {
//                            JourneyFragment.AddJourneyFragments(queue, journeyList, journey.getJourneyID());
                            volleyService.addJourneyFragments(journeyList, journey.getJourneyID());
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
    public void onDestroy() {
        Log.i(TAG, "on destroyed called");
        super.onDestroy();
        endJourneys();
        ses.shutdown();
        mLocationRequest = null;
        mGoogleApiClient.disconnect();
        this.stopSelf();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch(Exception e){

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
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
                Log.i(TAG, String.valueOf(location.getLatitude()));
                //Data within intent to send in a broadcast.
                //  Intent intent = new Intent(HuhConnectionService.NEW_MESSAGE);

                try {
                    //trackSpeedLocation = location;
                    journey.setLatitude(String.valueOf(location.getLatitude()));
                    journey.setLongitude(String.valueOf(location.getLongitude()));



                    // Called when a new location is found by the network location provider.
                    if (location.hasSpeed()) {
                        speed = (int) (Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0);
                        journey.setCurrentSpeed((speed));

                        currentSpeedIntent = new Intent();
                        // sets keyword to listen out for for this broadcast
                        currentSpeedIntent.setAction("com.carvis");
                        currentSpeedIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        currentSpeedIntent.setPackage(context.getPackageName());
                        currentSpeedIntent.putExtra("speed", speed);

                        //Sends out broadcast
                        sendBroadcast(currentSpeedIntent);


//                if (speed > Integer.parseInt(journey.getSpeedLimit())) {
//                    currentSpeedTextView.setTextColor(Color.RED);
//                } else {
//                    currentSpeedTextView.setTextColor(Color.WHITE);
//                }
                        //journey.setSpeedLimit(String.valueOf(nearKnownSpeedLimit(location)));

                    }


                    if (intialMovement == true) {
                        //journey.addJourneyDB(queue, provider.getUserName(), "insert");
                        volleyService.addJourneyDB(journey, provider.getUserName(), "insert");
                        intialMovement = false;
                    }

                    speedSearch.setLocation(location);


                    try {
                        if (journey.getCurrentSpeed() > limit && limit != 0) {
                            if (!isPlayingVoice) {
                                Intent intent = new Intent();
                                // sets keyword to listen out for for this broadcast
                                intent.setAction("com.carvis");
                                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                                intent.setPackage(context.getPackageName());
                                intent.putExtra("playVoice", "");

                                //Sends out broadcast
                                sendBroadcast(intent);
                            }
                            //speeding = true;
                            if (!isRunning) {
                                setOverSpeedLimit();
                            }
                            //speedCheckService.wait10Seconds();

                            //currentSpeedTextView.setText(jID);
//                    OverSpeedLimit o = new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit));
//                    o.InsertOverLimitDB(queue, journey.getJourneyID(), provider.getUserName());
                            // overSpeedLimits.add(new OverSpeedLimit(journey.getLatitude(), journey.getLongitude(), String.valueOf(journey.getCurrentSpeed()), String.valueOf(limit), dNow, provider.getUserName(), journey.getJourneyID()));
                        } else {
//                            Intent intent = new Intent();
                            // sets keyword to listen out for for this broadcast
                            Intent intent = new Intent();
                            intent.setAction("com.carvis");
                            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            intent.setPackage(context.getPackageName());
                            intent.putExtra("stopVoice", "");

                            //Sends out broadcast
                            sendBroadcast(intent);
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
//                    Message message = new Message();
//                    Bundle b = new Bundle();
//                    b.putSerializable("SpeedCamera",s);
//                    message.setData(b);
//                    message.arg1 = 0;
//                    speedVanHandler.sendMessage(message);
                    Log.i(TAG,"near speed van");
                    //System.out.println(cameraLocations.get(i).getLatitude() + " _ _ _ " + cameraLocations.get(i).getLongitude());
                    return true;
                }
            }
        }
        for (TemporarySpeedCamera t : TemporarySpeedCamera.temporarySpeedCameras) {
            cameraLocation = new Location("Camera Location");
            cameraLocation.setLatitude(t.getLatitude());
            cameraLocation.setLongitude(t.getLongitude());
            if ((location.distanceTo(cameraLocation) / 1000) < 1.0) {
                //System.out.println("TEMPORARY SPEED CAMERA NEAR");
                //displaySpeedCameraInfo(t);
//                Message message = new Message();
//                Bundle b = new Bundle();
//                b.putSerializable("TemporarySpeedCamera",t);
//                message.setData(b);
//                message.arg1 = 0;
//                speedCameraHandler.sendMessage(message);
                Log.i(TAG,"near speed camera");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public void setOverSpeedLimit() {
        isRunning = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 5000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
//                            playVoice();
//                            stopVoice();
                            wait(futureTime - System.currentTimeMillis());

                        } catch (Exception e) {
                            Log.i("over speed", e.getMessage());
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


    public void nearKnownSpeedLimit(final SpeedSearch speedSearch) {
        try {
            if (roadHashMap.containsKey(speedSearch.getOsm_id())) {
                for (RoadRecord r : roadHashMap.get(speedSearch.getOsm_id())) {
                    test.setLatitude(Double.parseDouble(r.getLatitude()));
                    test.setLongitude(Double.parseDouble(r.getLongitude()));
                    if (speedSearch.getLocation().distanceTo(test) / 1000 <= 0.015) {
                        Log.i("speed Test log 1", "near" + r.getSpeedLimit());
                        // speedLimit =  roadRecords.get(i).getSpeedLimit();
                        journey.setSpeedLimit(r.getSpeedLimit());
                        //speedLimitTextView.setText(journey.getSpeedLimit() + " km/h");
//                        chooseSpeedImage(r.getSpeedLimit());
                        Message alertMessage = new Message();
                        alertMessage.what = r.getSpeedLimit();

                        speedLimitIntent = new Intent();
                        // sets keyword to listen out for for this broadcast
                        speedLimitIntent.setAction("com.carvis");
                        speedLimitIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        speedLimitIntent.setPackage(context.getPackageName());
                        speedLimitIntent.putExtra("speedLimit", r.getSpeedLimit());

                        sendBroadcast(speedLimitIntent);


                        //speedHandler.sendMessage(alertMessage);
                        return;
                    }
                }
            }

            for (Map.Entry<Integer, ArrayList<RoadRecord>> entry : roadHashMap.entrySet()) {
                for (RoadRecord r : entry.getValue()) {
                    test.setLatitude(Double.parseDouble(r.getLatitude()));
                    test.setLongitude(Double.parseDouble(r.getLongitude()));
                    if (speedSearch.getLocation().distanceTo(test) / 1000 <= 0.015) {
                        Log.i("speed Test log 2", "near" + r.getSpeedLimit());
                        // speedLimit =  roadRecords.get(i).getSpeedLimit();
                        speedSearch.setOsm_id(entry.getKey());
                        journey.setSpeedLimit(r.getSpeedLimit());
                        //speedLimitTextView.setText(journey.getSpeedLimit() + " km/h");
                        Message alertMessage = new Message();
                        alertMessage.what = r.getSpeedLimit();
                        //speedHandler.sendMessage(alertMessage);
                        //handler.sendEmptyMessage(0);
                        speedLimitIntent = new Intent();
                        // sets keyword to listen out for for this broadcast
                        speedLimitIntent.setAction("com.carvis");
                        speedLimitIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        speedLimitIntent.setPackage(context.getPackageName());
                        speedLimitIntent.putExtra("speedLimit", r.getSpeedLimit());
                        return;
                    }
                }
                // System.out.println(entry.getKey() + "/" + entry.getValue());
            }
            //handler.sendEmptyMessage(0);
            //journey.getSpeedFromLambda(database, queue, speedSearch);
            volleyService.getSpeedFromLambda(this, speedSearch, journey.getLatitude(), journey.getLongitude());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public void endJourneys() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //journey.addJourneyDB(queue, provider.getUserName(), "update");
                volleyService.addJourneyDB(journey, provider.getUserName(), "update");
//                JourneyFragment.AddJourneyFragments(queue, journeyList, journey.getJourneyID());
                volleyService.addJourneyFragments(journeyList, journey.getJourneyID());
                volleyService.addOverSpeedLimits(overSpeedLimits, journey.getJourneyID(), provider.getUserName());
                updateHandler.sendEmptyMessage(0);
            }
        };

        Thread endThread = new Thread(runnable);
        endThread.start();
    }

    private void createSpeedVanReference(){
        vanRef = database.getReference("speedVans");
        vanRef.keepSynced(true);
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
    }

    private void createSpeedCameraReference(){
        cameraRef = database.getReference("reportedSpeedCameras");
        cameraRef.keepSynced(true);
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

    }

    public  void createSpeedLimitReference(String locale){
        speedref =null;
        System.out.println("LOCALE : : "+(locale));
        speedref = database.getReference("speedLimits").child(locale);
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
                roadHashMap.put(Integer.parseInt(dataSnapshot.getKey()), record);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String getLocale(){
        return locale;
    }
}
