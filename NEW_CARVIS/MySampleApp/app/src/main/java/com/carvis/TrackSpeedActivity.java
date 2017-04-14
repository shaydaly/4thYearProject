package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.android.internal.telephony.ITelephony;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.lang.reflect.Method;
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
import com.mysampleapp.*;
import com.mysampleapp.MainActivity;
import com.mysampleapp.demo.HomeDemoFragment;

import static android.content.Intent.ACTION_ANSWER;
import static android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class TrackSpeedActivity extends Activity {


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS
    };

    //  SpeedCheckService speedCheckService;
    boolean isBound = false, speedCameraWarningShowing = false;
    boolean snackBackShown;

    private Context context;
    //Intent intent = getIntent();
    //JourneyFragment journeyFragment;


    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;


    ImageView imageView50;
    ImageView imageView60;
    ImageView imageView80;
    ImageView imageView100;

    TextView currentSpeedTextView, speedCameraTextView, badTraffic;
    CognitoUserPoolsSignInProvider provider;

    MediaPlayer mediaPlayer;

    int limit;
    private Date dNow;
    int speed;
    double latitude, longitude;
    private RequestQueue queue;

    VolleyService volleyService;

    private Location test;
    Intent serviceIntent;
    Intent firebaseIntent;

    ArrayList<String> missedCallNumbers;



    //Location trackSpeedLocation;
    boolean isRunning;
    boolean isPlayingVoice;
    boolean isPlayingCameraVoice, playVoice , playSpeedVoice, blockPhoneCalls;
    SpeedSearch speedSearch;



//    View trackSpeedView;

//    Handler speedHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            chooseSpeedImage(msg.what);
//        }
//    };


    Handler SpeedCameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle summaryBundle = msg.getData();
            String address = summaryBundle.getString("address");
            String time = summaryBundle.getString("time");
            speedCameraTextView.setVisibility(View.VISIBLE);
            speedCameraTextView.setText(address+"\n"+time);
        }
    };

    Handler SpeedCameraHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            speedCameraTextView.setVisibility(View.INVISIBLE);
            speedCameraWarningShowing = false;
        }
    };

    Handler badTrafficHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle summaryBundle = msg.getData();
            String address = summaryBundle.getString("address");
            badTraffic.setVisibility(View.VISIBLE);
            badTraffic.setText(address);
        }
    };

    Handler badTrafficEndHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            badTraffic.setVisibility(View.INVISIBLE);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mediaPlayer = new MediaPlayer();



        //speeding = false;

        isRunning = false;
        isPlayingVoice = false;
        isPlayingCameraVoice = false;
        limit = 0;
        snackBackShown = false;

        speedSearch = new SpeedSearch(-99);

        missedCallNumbers = new ArrayList<>();



        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);
        //showPermissionDialog();
        playVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playVoiceUpdate", true);

        playSpeedVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playSpeedLimit", true);

        blockPhoneCalls = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("blockIncomingCalls", false);

        serviceIntent = new Intent(context, MyLocationService.class);
        startService(serviceIntent);

        //Uri myUri = Uri.fromFile(new File("raw/speedlimitpolly.mp3"));

        imageView50 = (ImageView) findViewById(R.id.speed50km);
        imageView60 = (ImageView) findViewById(R.id.speed60km);
        imageView80 = (ImageView) findViewById(R.id.speed80km);
        imageView100 = (ImageView) findViewById(R.id.speed100km);


        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        speedCameraTextView = (TextView)findViewById(R.id.speedCamera);
        badTraffic = (TextView)findViewById(R.id.badTrafficReported);
        //speedLimitTextView = (TextView) findViewById(R.id.speedLimit);

//        trackSpeedView = findViewById(R.id.activity_track_speed);



    }




    @Override
    protected void onResume() {
        super.onResume();
        latitude = 0.0;
        longitude = 0.0;




        firebaseIntent = new Intent(context, CarvisFireBaseMessagingService.class);
        startService(firebaseIntent);





        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(MyLocationService.SPEED_MESSAGE)) {
                        speed = intent.getIntExtra("speed", 0);
                        currentSpeedTextView.setText(String.valueOf(speed) + " km/h");
                        latitude = intent.getDoubleExtra("latitude", 0);
                        longitude = intent.getDoubleExtra("longitude", 0);
                    }
                    else if (intent.getAction().equals(MyLocationService.LIMIT_MESSAGE)) {
                        //Log.i("speeeeeeedd", String.valueOf(intent.getIntExtra("speedLimit", 0)));
                        limit = intent.getIntExtra("speedLimit", 0);
                        if (limit != 0) {
                            chooseSpeedImage(limit);
                        }

                        speedSearch.setOsm_id(intent.getIntExtra("osmID",0));

                    }
                    else if (intent.getAction().equals(MyLocationService.PLAY_SPEED_MESSAGE) && !isPlayingVoice && playSpeedVoice) {
                        //Log.i("VOICEEEE", "PLAY VOICE RECEIVED");
                        playSpeedPolly();
                    }
                    else if (intent.getAction().equals(MyLocationService.STOP_SPEED_MESSAGE) && isPlayingVoice) {
                       // Log.i("VOICEEEE", "STOP VOICE RECEIVED");
                        //stopVoice();
                    }

                    else if (intent.getAction().equals(MyLocationService.PLAY_CAMERA_MESSAGE)) {
                        Log.i("shay", "received play speed camera broadcast");
                        double cameraLatitude = intent.getDoubleExtra("latitude",0);
                        double cameraLongitude = intent.getDoubleExtra("longitude",0);
                        String time = intent.getStringExtra("time");
                                String address = SpeedCamera.getSpeedCameraAddress(context,cameraLatitude, cameraLongitude);

//                        if(!speedCameraWarningShowing) {
//                            displaySpeedCameraInfo(address, time);
//                        }
//                        if (!isPlayingCameraVoice && playVoice) {
//                            playSpeedCameraPolly();
//                        }
                    }

                    else if (intent.getAction().equals(ACTION_PHONE_STATE_CHANGED) && blockPhoneCalls) {
                        Log.wtf("PHONE CALL RECEIVED", "phone call");
                        blockIncomeCalls(intent);
                    }
                    else if (intent.getAction().equals(CarvisFireBaseMessagingService.TAG)) {
//                        Log.wtf("badTrafficLocation", intent.getStringExtra("badTrafficLocation"));
//                        Toast.makeText(context,intent.getStringExtra("badTrafficLocation"), Toast.LENGTH_LONG ).show();
                        playBadTrafficPolly(intent.getStringExtra("badTrafficLocation"));
                    }

//                    else if (intent.getAction().equals(ACTION_ANSWER)) {
//                        Log.wtf("PHONE CALL RECEIVED", "phone call");
//                        blockIncomeCalls(intent);
//                    }

//                    if((speed>limit && limit!= 0)){
//                        trackSpeedView.setBackgroundColor(Color.RED);
//                    }
//                    else{
//                        trackSpeedView.setBackgroundColor(Color.WHITE);
//                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        };

        IntentFilter filter = new IntentFilter(MyLocationService.SPEED_MESSAGE);
        filter.addAction(MyLocationService.LIMIT_MESSAGE);
        filter.addAction(MyLocationService.PLAY_SPEED_MESSAGE);
        filter.addAction(MyLocationService.STOP_SPEED_MESSAGE);
        filter.addAction(MyLocationService.PLAY_CAMERA_MESSAGE);
        filter.addAction(ACTION_PHONE_STATE_CHANGED);
        filter.addAction(TELEPHONY_SERVICE);
        filter.addAction(CarvisFireBaseMessagingService.TAG);
        registerReceiver(mBroadcastReceiver, filter);


        registerButtons();


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

        try {
            unregisterReceiver(mBroadcastReceiver);
        }
        catch(Exception e){

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        context.stopService(serviceIntent);
        context.stopService(firebaseIntent);
        createNotification();
        //startActivity(new Intent(context, MainActivity.class));

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

    public void displaySpeedCameraInfo(final String address, final String time) {
        speedCameraWarningShowing = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("address", address);
                b.putString("time", time);
                message.setData(b);
                message.arg1 = 0;
                SpeedCameraHandler.sendMessage(message);
                long futureTime = System.currentTimeMillis() + 5000;
                while(System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try{
                            wait(futureTime - System.currentTimeMillis());
                        }
                        catch(Exception e){

                        }
                    }
                }

                SpeedCameraHandler2.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
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
//        snackBackShown = true;
////        Snackbar snackbar = Snackbar
////                .make(findViewById(R.id.activity_track_speed), "Speed Van Nearby", Snackbar.LENGTH_LONG)
////                .setAction(SpeedCamera.getSpeedCameraAddress(context, t.getLatitude(), t.getLongitude()), new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////
////                    }
////                });
////
////// Changing message text color
////        snackbar.setActionTextColor(Color.RED);
////
////// Changing action button text color
////        View sbView = snackbar.getView();
////        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
////        textView.setTextColor(Color.RED);
////        if(!snackbar.isShown()) {
////            snackbar.show();
////        }
//        snackBackShown = false;
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

    public void playSpeedPolly() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                isPlayingVoice = true;
                try {
                    stopVoice();
                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedlimitpolly);
                    mediaPlayer.start();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        };
        Thread t = new Thread(r);
        t.start();

//        player.reset();
//        player.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/2cp.3gp");
//        player.prepare();
//        player.start();

    }

    public void playBadTrafficPolly(final String address) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("address", address);
                message.setData(b);
                message.arg1 = 0;
                badTrafficHandler.sendMessage(message);
                isPlayingVoice = true;
                try {
                    stopVoice();
                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.badtraffic);
                    mediaPlayer.start();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                long futureTime = System.currentTimeMillis() + 15000;
                Log.wtf("before while","");
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {

                        }
                    }

                }
                Log.wtf("after while","");
                badTrafficEndHandler.sendEmptyMessage(0);
                isPlayingVoice = false;

            }
        };
        Thread t = new Thread(r);
        t.start();

//        player.reset();
//        player.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/2cp.3gp");
//        player.prepare();
//        player.start();

    }


    public void playSpeedCameraPolly() {
        Log.i("playSpeedCameraPolly", "plzy");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                isPlayingCameraVoice = true;
                try {
                    stopVoice();
                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedcamerapolly);
                    mediaPlayer.start();

                } catch (Exception e) {

                }
                long futureTime = System.currentTimeMillis() + 30000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {

                        }
                    }
                }
                isPlayingCameraVoice = false;
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public void playSpeedVanPolly() {
        Log.i("playSpeedVanPolly", "plzy");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                isPlayingVoice = true;
                try {
                    stopVoice();
                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedvanpolly);
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
        };
        Thread t = new Thread(r);
        t.start();
    }

    public void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void blockIncomeCalls(Intent intent){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");
            sendDrivingSMS(phoneNumber);

            if(!missedCallNumbers.contains(phoneNumber)){
                missedCallNumbers.add(phoneNumber);
            }
            Log.d("INCOMING", phoneNumber);
            if ((phoneNumber != null)) {
                telephonyService.endCall();
                Log.d("HANG UP", phoneNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerButtons(){
        Button emergencySMSButton = (Button) findViewById(R.id.sendEmergencySMS);
        emergencySMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emergencyContact = PreferenceManager.getDefaultSharedPreferences(context).getString("emergencyContact", "");
                if (emergencyContact.equals("")) {
                    System.out.println("emergeency contact emtpy");
                    emergencyContact = "0851329485";
                }
                SmsManager smsManager = SmsManager.getDefault();
                String messageBody = getString(R.string.emergencyText) + " " + latitude + "," + longitude + "\n\nhttp://www.google.com/maps/place/" + latitude + "," + longitude;
                smsManager.sendTextMessage(emergencyContact, null, messageBody, null, null);
                Toast.makeText(context, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
            }
        });

        Button addSpeedCameraButton = (Button) findViewById(R.id.addSpeedCamera);
        addSpeedCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dNow = new Date();
                String time = ft.format(dNow);
                TemporarySpeedCamera.addTemporaryCamera(latitude, longitude, time, context);
                Toast.makeText(context, "Speed Camera Added", Toast.LENGTH_SHORT).show();
            }
        });

        Button addTrafficIndicator = (Button) findViewById(R.id.addTrafficIndicator);
        addTrafficIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dNow = new Date();
                    String time = ft.format(dNow);
                    TrafficUpdate.AddTrafficUpdate(latitude, longitude, time, context);
                    String Address = SpeedCamera.getSpeedCameraAddress(context, latitude, longitude);

                    volleyService.createTrafficNotification(Address);
                    if(speedSearch.getOsm_id()!= -99) {
                        volleyService.createTrafficIncident(speedSearch.getOsm_id(), time, provider.getUserName());
                    }
                    Toast.makeText(context, "Bad traffic added", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){

                }
            }
        });

        Button endJourney = (Button) findViewById(R.id.endJourney);
        endJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(context, "Journey Ended", Toast.LENGTH_SHORT).show();
                    //onDestroy();
                }
                catch(Exception e){
                    Log.wtf("endJounrye",e.getMessage());
                }
            }
        });
    }


    private void createNotification(){
        if(missedCallNumbers!=null) {
            if (missedCallNumbers.size() != 0) {
                NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
                notification.setAutoCancel(true);

                notification.setSmallIcon(R.mipmap.splash_icon);
                notification.setWhen(System.currentTimeMillis());
                notification.setContentText(getString(R.string.missedCall));
                Intent intent = new Intent(this, MissedCall.class);


                intent.putStringArrayListExtra("numbers", missedCallNumbers);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pendingIntent);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(1231, notification.build());
            }
        }
    }

    private void sendDrivingSMS(String phoneNumber){
        SmsManager smsManager = SmsManager.getDefault();
        String messageBody = getString(R.string.currentlyDriving);
        smsManager.sendTextMessage(phoneNumber, null, messageBody, null, null);
        Toast.makeText(context, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
    }


//    private void showPermissionDialog() {
//        if (!MyLocationService.checkPermission(this)) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    1);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        return;
//                    }
//
//                    Intent intent = new Intent();
//                    // sets keyword to listen out for for this broadcast
//                    intent.setAction("SEAMUS");
//                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                    intent.setPackage(context.getPackageName());
//                    //Sends out broadcast
//                    sendBroadcast(intent);
//
//                return;
//            }
//        }
}









