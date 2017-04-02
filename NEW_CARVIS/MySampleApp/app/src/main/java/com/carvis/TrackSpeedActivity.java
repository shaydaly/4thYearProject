package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mysampleapp.R;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class TrackSpeedActivity extends Activity  {


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //  SpeedCheckService speedCheckService;
    boolean isBound = false;
    boolean snackBackShown;

    private Context context;
    //Intent intent = getIntent();
    //JourneyFragment journeyFragment;


    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");





    ImageView imageView50;
    ImageView imageView60;
    ImageView imageView80;
    ImageView imageView100;

    TextView currentSpeedTextView;
    TextView speedLimitTextView;

    MediaPlayer mediaPlayer;

    int limit;
    private Date dNow;
    int speed;
    private RequestQueue queue;

    VolleyService volleyService;

    private Location test;
    Intent serviceIntent;
    //private Timer timer , timer2;

    //    HashSet<SpeedCamera> cameras;
    FirebaseDatabase database;
//    DatabaseReference vanRef;
//    DatabaseReference cameraRef;

    //ArrayList<Road> roads;

    //boolean speeding;

    //Location trackSpeedLocation;
    boolean isRunning;
    boolean isPlayingVoice;
    SpeedSearch speedSearch;



    Handler speedHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chooseSpeedImage(msg.what);
        }
    };



    BroadcastReceiver mBroadcastReceiver;


//    private Handler handler;
//    private Runnable runnable;
//
//    private Handler journeyFragmentHandler;
//    private Runnable journeyFragmentRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);

        mediaPlayer = new MediaPlayer();


        //speeding = false;

        isRunning = false;
        isPlayingVoice = false;
        limit =0;
        snackBackShown = false;



//        timer = new Timer();
//        timer2 = new Timer();

        context = getApplicationContext();

        serviceIntent = new Intent(context, MyLocationService.class);
        startService(serviceIntent);



        Uri myUri = Uri.fromFile(new File("raw/speedlimitpolly.mp3"));

//        mediaPlayer = MediaPlayer.create(this, R.raw.speedlimitpolly);
//            mediaPlayer = MediaPlayer.create(context, myUri);
//        mediaPlayer.setLooping(false);
//            mediaPlayer.setDataSource(context, myUri);



        //roads = new ArrayList<>();

        // cameras = new HashSet<>();

//        Button addSpeedCameraButton = (Button) findViewById(R.id.addSpeedCamera);
//        addSpeedCameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String time = ft.format(dNow);
//                TemporarySpeedCamera.addTemporaryCamera(journey.getLatitude(), journey.getLongitude(), time, context);
//            }
//        });




//        Button emergencySMSButton = (Button) findViewById(R.id.sendEmergencySMS);
//        emergencySMSButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String emergencyContact = PreferenceManager.getDefaultSharedPreferences(context).getString("emergencyContact", null);
//                if(emergencyContact.equals("")){
//                    emergencyContact = "0851329485";
//                }
//                SmsManager smsManager = SmsManager.getDefault();
//                String messageBody = getResources().getString(R.string.emergencyText)+" "+journey.getLatitude()+","+journey.getLongitude()+"\n\nhttp://www.google.com/maps/place/"+journey.getLatitude()+","+journey.getLongitude();
//                smsManager.sendTextMessage(emergencyContact, null, messageBody, null, null);
//            }
//        });

//        Button speedVan = (Button) findViewById(R.id.speedvan);
//        speedVan.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                SpeedCamera.addSpeedVanLocatedRecord(database);
//            }
//        });

        imageView50 = (ImageView) findViewById(R.id.speed50km);
        imageView60 = (ImageView) findViewById(R.id.speed60km);
        imageView80 = (ImageView) findViewById(R.id.speed80km);
        imageView100 = (ImageView) findViewById(R.id.speed100km);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseApp.initializeApp(context);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();

        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        //speedLimitTextView = (TextView) findViewById(R.id.speedLimit);




    }

    @Override
    protected  void onResume(){
        super.onResume();
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {


                    speed = intent.getIntExtra("speed",0);
                    //limit = Integer.parseInt(intent.getStringExtra("speedLimit"));

                    //chooseSpeedImage(limit);
                    if(speed!=0) {
                        currentSpeedTextView.setText(String.valueOf(speed) + " km/h");
                    }

                    limit = intent.getIntExtra("speedLimit",0);
                    if(limit!= 0) {
                        chooseSpeedImage(limit);
                    }
                    if((intent.getExtras().containsKey("playVoice")) && !isPlayingVoice ){
                        playVoice();
                    }
                    if(intent.getExtras().containsKey("stopVoice")){
                        stopVoice();
                    }
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }

            }
        };

        IntentFilter filter = new IntentFilter("com.carvis");
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        super.onStop();
        //unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.stopService(serviceIntent);

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
                if (!snackBackShown) {
                    showSnackBarSpeedCamera(t);
                }
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

    public void displaySpeedCameraInfo(final TemporarySpeedCamera t) {

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

//    public void setOverSpeedLimit() {
//        isRunning = true;
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                long futureTime = System.currentTimeMillis() + 5000;
//                while (System.currentTimeMillis() < futureTime) {
//                    synchronized (this) {
//                        try {
////                            playVoice();
////                            stopVoice();
//                            wait(futureTime - System.currentTimeMillis());
//
//                        } catch (Exception e) {
//                            Log.i("over speed", e.getMessage());
//                        }
//                    }
//                }
//                overSpeedHandler.sendEmptyMessage(0);
//                isRunning = false;
//            }
//
//        };
//        Thread t = new Thread(r);
//        t.start();
//    }

    public void chooseSpeedImage(int limit) {
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

    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public void showSnackBarSpeedCamera(TemporarySpeedCamera t) {
        snackBackShown = true;
//        Snackbar snackbar = Snackbar
//                .make(findViewById(R.id.activity_track_speed), "Speed Van Nearby", Snackbar.LENGTH_LONG).setDuration(5000)
//                .setAction(SpeedCamera.getSpeedCameraAddress(context, t.getLatitude(), t.getLongitude()), new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                });
//
//// Changing message text color
//        snackbar.setActionTextColor(Color.RED);
//
//// Changing action button text color
//        View sbView = snackbar.getView();
//        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.RED);
//        if(!snackbar.isShown()) {
//            snackbar.show();
//        }
        snackBackShown = false;
    }

    public void showSnackBarSpeedVan(SpeedCamera s) {
        snackBackShown = true;
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.activity_track_speed), "Speed Van Nearby", Snackbar.LENGTH_LONG).setDuration(5000)
                .setAction(SpeedCamera.getSpeedCameraAddress(context, s.getStartLatitude(), s.getEndLongitude()), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

// Changing message text color
        snackbar.setActionTextColor(Color.RED);

// Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        if (!snackbar.isShown()) {
            snackbar.show();
        }
        snackBackShown = false;
    }

    public void playVoice() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                isPlayingVoice = true;
                try {
                    stopVoice();
                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedlimitpolly);
                    mediaPlayer.start();

                } catch (Exception e) {

                }
                long futureTime = System.currentTimeMillis() + 5000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {

                        }
                    }
                }
                isPlayingVoice = false;
            }
        }

                ;
        Thread t = new Thread(r);
        t.start();

//        player.reset();
//        player.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/2cp.3gp");
//        player.prepare();
//        player.start();


    }

    public void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

//    public  void createSpeedLimitReference(String locale){
//        speedref =null;
//        System.out.println("LOCALE : : "+(locale));
//        speedref = database.getReference("speedLimits").child(locale);
//        speedref.keepSynced(true);
//
//        speedref.addChildEventListener(new ChildEventListener() {
//
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//
//                record = new ArrayList<>();
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    RoadRecord r = new RoadRecord(
//                            String.valueOf(postSnapshot.child("latitude").getValue()),
//                            String.valueOf(postSnapshot.child("longitude").getValue()),
//                            Integer.parseInt(String.valueOf(postSnapshot.child("speedLimit").getValue())));
//                    record.add(r);
//                }
//                roadHashMap.put(Integer.parseInt(dataSnapshot.getKey()), record);
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

//    private void createSpeedVanReference(){
//        vanRef = database.getReference("speedVans");
//        vanRef.keepSynced(true);
//        vanRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                //System.out.println(dataSnapshot.child("startLatitude").getValue());
//                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
//                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
//                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
//                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
//                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
//                //System.out.println(String.valueOf(dataSnapshot.child("reportedTimes").getValue()));
//                HashMap<String, String> reports = (HashMap<String, String>) dataSnapshot.child("Reported Times").getValue();
//                ArrayList<String> dates = new ArrayList<String>();
//                if (reports != null) {
//                    Iterator it = reports.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry) it.next();
//                        dates.add(String.valueOf(pair.getValue()));
//                        it.remove(); // avoids a ConcurrentModificationException
//                    }
//                }
//                SpeedCamera.addSpeedCamera(new SpeedCamera(id, startLat, startLong, endLat, endLong, dates));
//                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//
//
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
//                //System.out.println(SpeedCamera.getCameras().size());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                //cameras.remove(dataSnapshot.getKey());
//                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
//                // System.out.println("camera size : "+SpeedCamera.getCameras().size());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

//    private void createSpeedCameraReference(){
//        cameraRef = database.getReference("reportedSpeedCameras");
//        cameraRef.keepSynced(true);
//        cameraRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
//                double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
//                String time = String.valueOf(dataSnapshot.child("time").getValue());
//
//                TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(latitude, longitude, time));
//                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//                //System.out.println(TemporarySpeedCamera.temporarySpeedCameras.size()+" is the temp size");
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//
//
//////                cameras.remove(dataSnapshot.getKey());
//////                System.out.println(cameras.size());
////
////                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
////                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
////                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
////                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
////                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
////                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
////
////                System.out.println(SpeedCamera.cameras.size());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                //cameras.remove(dataSnapshot.getKey());
////                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
////                System.out.println("camera size : "+SpeedCamera.cameras.size());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//    }





}






