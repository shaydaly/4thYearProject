package com.carvis;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.android.internal.telephony.ITelephony;
import com.android.volley.RequestQueue;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.CARVISAPP.*;
import com.CARVISAPP.MainActivity;

import static android.telephony.PhoneStateListener.LISTEN_CALL_STATE;
import static android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED;
import static android.telephony.TelephonyManager.ACTION_RESPOND_VIA_MESSAGE;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;
import static android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER;

public class TrackSpeedActivity extends Activity {


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS
    };

    //  SpeedCheckService speedCheckService;
    boolean ispeedCameraWarningShowing = false, snackBackShown, isRunning, isPlayingVoice, speedCameraWarningShowing,
            isPlayingCameraVoice, playVoice, playSpeedVoice, blockPhoneCalls, playTrafficVoice, receiveTrafficUpdates;


    private Context context;
    //Intent intent = getIntent();
    //JourneyFragment journeyFragment;


    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ImageView imageView50, imageView60, imageView80, imageView100, imageView120;

    TextView currentSpeedTextView, speedCameraTextView, badTraffic;
    CognitoUserPoolsSignInProvider provider;

    MediaPlayer mediaPlayer, speedLimitPlayer, speedCameraPlayer, trafficPlayer;
    CarvisMediaPlayer carvisMediaPlayer;
    ArrayList<Integer> songs;
    ArrayList<ImageView> imageViews;

    int speed, limit, kilomPerim;
    private Date dNow;
    double latitude, longitude;

    VolleyService volleyService;

    private Location test;
    Intent serviceIntent;
    Intent firebaseIntent;

    ArrayList<String> missedCallNumbers;

    private String locale;


    //Location trackSpeedLocation;

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
            if (time == null) {
                time = "";
            }
            speedCameraTextView.setVisibility(View.VISIBLE);
            speedCameraTextView.setText("Speed Camera \n" + address + "\n" + time);
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
            badTraffic.setText("Bad Traffic \n" + address);
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
        speedCameraPlayer = new MediaPlayer();
        trafficPlayer = new MediaPlayer();
        speedLimitPlayer = new MediaPlayer();


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

        playTrafficVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playTrafficUpdates", false);
        receiveTrafficUpdates = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("receiveTrafficNotifications", false);

        kilomPerim = PreferenceManager.getDefaultSharedPreferences(context).getInt("kilomPerim", 3);


        locale = PreferenceManager.getDefaultSharedPreferences(context).getString("locale", "");
        if (locale.equals("")) {
            locale = "Dublin";
        }

        songs = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        carvisMediaPlayer = new CarvisMediaPlayer(mediaPlayer, songs, context);


        serviceIntent = new Intent(context, MyLocationService.class);
        startService(serviceIntent);
        imageView50 = (ImageView) findViewById(R.id.speed50km);
        imageView60 = (ImageView) findViewById(R.id.speed60km);
        imageView80 = (ImageView) findViewById(R.id.speed80km);
        imageView100 = (ImageView) findViewById(R.id.speed100km);
        imageView120 = (ImageView) findViewById(R.id.speed120km);
        imageViews = new ArrayList<>();
        imageViews.add(imageView50);
        imageViews.add(imageView60);
        imageViews.add(imageView80);
        imageViews.add(imageView100);
        imageViews.add(imageView120);
        currentSpeedTextView = (TextView) findViewById(R.id.currentSpeed);
        speedCameraTextView = (TextView) findViewById(R.id.speedCamera);
        badTraffic = (TextView) findViewById(R.id.badTrafficReported);
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
                    } else if (intent.getAction().equals(MyLocationService.LIMIT_MESSAGE)) {
                        //Log.i("speeeeeeedd", String.valueOf(intent.getIntExtra("speedLimit", 0)));
                        limit = intent.getIntExtra("speedLimit", 0);
//                        if (limit != 0) {
//                            chooseSpeedImage(limit);
//                        }
                        chooseSpeedImage(limit);
                        speedSearch.setOsm_id(intent.getIntExtra("osmID", 0));

                    } else if (intent.getAction().equals(MyLocationService.PLAY_SPEED_MESSAGE) && playSpeedVoice) {
                        //Log.i("VOICEEEE", "PLAY VOICE RECEIVED");
                        //playSpeedPolly();
                        carvisMediaPlayer.addSongToQueue(R.raw.speedlimitpolly);
                        // Log.wtf("Speed limit polly", String.valueOf(R.raw.speedlimitpolly));
                    }
//                    else if (intent.getAction().equals(MyLocationService.STOP_SPEED_MESSAGE)) {
//                        // Log.i("VOICEEEE", "STOP VOICE RECEIVED");
//                        //stopSpeedVoice();
//                        carvisMediaPlayer.stopMediaPlayer();
//                    }

                    else if (intent.getAction().equals(MyLocationService.PLAY_CAMERA_MESSAGE)) {
//                        Log.i("shay", "received play speed camera broadcast");
                        double cameraLatitude = intent.getDoubleExtra("latitude", 0);
                        double cameraLongitude = intent.getDoubleExtra("longitude", 0);
                        String time = intent.getStringExtra("time");
                        String address = SpeedCamera.getSpeedCameraAddress(context, cameraLatitude, cameraLongitude);

                        if (!speedCameraWarningShowing) {
                            displaySpeedCameraInfo(address, time);
                        }

                        if (playVoice) {
                            carvisMediaPlayer.addSongToQueue(R.raw.speedcamerapolly);
                            //Log.wtf("Speed camera polly", String.valueOf(R.raw.speedcamerapolly));
                        }
//                        if (!isPlayingCameraVoice && playVoice) {
//
//                            playSpeedCameraPolly();
//                        }
                    } else if (intent.getAction().equals(ACTION_PHONE_STATE_CHANGED) && blockPhoneCalls) {
                        blockIncomeCalls(intent);
                    } else if (intent.getAction().equals(CarvisFireBaseMessagingService.TAG)) {
//                        Log.wtf("badTrafficLocation", intent.getStringExtra("badTrafficLocation"));
//                        Toast.makeText(context,intent.getStringExtra("badTrafficLocation"), Toast.LENGTH_LONG ).show();

                        if (receiveTrafficUpdates) {
                            if (distanceToTrafficAddress(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0))) {
                                showBadTrafficLocation(intent.getStringExtra("badTrafficLocation"));
                                if (playTrafficVoice) {
                                    //Log.wtf("play traffic", " paly");
                                    //Log.wtf("bad traffic", String.valueOf(R.raw.badtraffic));
                                    carvisMediaPlayer.addSongToQueue(R.raw.badtraffic);
                                    //playBadTrafficPolly();
                                }
                            }
                        }

                    }

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
        } catch (Exception e) {

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

    @Override
    public void onBackPressed() {
        //super.onStop();
        Log.wtf("onBackPressed", "CALLEd");
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.CARVISAPP.MainActivity.class);
        startActivity(intent);
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
                long futureTime = System.currentTimeMillis() + 10000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {

                        }
                    }
                }

                SpeedCameraHandler2.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    public void chooseSpeedImage(int limit) {
        if (limit == 0) {
            hideAllImages();
        } else if (limit == 50) {
            showImage(imageView50);
        } else if (limit == 60) {
            showImage(imageView60);
        } else if (limit == 80) {
            showImage(imageView80);
        } else if (limit == 100) {
            showImage(imageView100);
        } else if (limit == 120) {
            showImage(imageView120);
        }
    }

    public void showImage(ImageView view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public void hideAllImages() {
        for (ImageView imageView : imageViews) {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

//    public void playSpeedPolly() {
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                isPlayingVoice = true;
//                try {
//                    stopSpeedVoice();
//                    speedLimitPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedlimitpolly);
//                    speedLimitPlayer.start();
//
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//                long futureTime = System.currentTimeMillis() + 5000;
//                while (System.currentTimeMillis() < futureTime) {
//                    synchronized (this) {
//                        try {
//                            wait(futureTime - System.currentTimeMillis());
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//                isPlayingVoice = false;
//            }
//        };
//        Thread t = new Thread(r);
//        t.start();
//
////        player.reset();
////        player.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/2cp.3gp");
////        player.prepare();
////        player.start();
//
//    }


    private void showBadTrafficLocation(final String address) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("address", address);
                message.setData(b);
                message.arg1 = 0;
                badTrafficHandler.sendMessage(message);

                long futureTime = System.currentTimeMillis() + 15000;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {

                        }
                    }
                }
                badTrafficEndHandler.sendEmptyMessage(0);
            }
        };
        Thread t = new Thread(r);
        t.start();


    }


//    public void playSpeedVanPolly() {
//        Log.i("playSpeedVanPolly", "plzy");
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                isPlayingVoice = true;
//                try {
//                    stopVoice();
//                    mediaPlayer = MediaPlayer.create(TrackSpeedActivity.this, R.raw.speedvanpolly);
//                    mediaPlayer.start();
//
//                } catch (Exception e) {
//
//                }
//                long futureTime = System.currentTimeMillis() + 5000;
//                while (System.currentTimeMillis() < futureTime) {
//                    synchronized (this) {
//                        try {
//                            wait(futureTime - System.currentTimeMillis());
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }
//                isPlayingVoice = false;
//            }
//        };
//        Thread t = new Thread(r);
//        t.start();
//    }

    private void blockIncomeCalls(Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");

            if (!missedCallNumbers.contains(phoneNumber)) {
                missedCallNumbers.add(phoneNumber);
                sendDrivingSMS(phoneNumber);
            }
//                Log.wtf("INCOMING", phoneNumber);
            if ((phoneNumber != null)) {
                telephonyService.endCall();
//                    Log.wtf("HANG UP", phoneNumber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void registerButtons() {
        Button emergencySMSButton = (Button) findViewById(R.id.sendEmergencySMS);
        emergencySMSButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String emergencyContact = PreferenceManager.getDefaultSharedPreferences(context).getString("emergencyContact", "");
                if (emergencyContact.equals("")) {
                    emergencyContact = "0851329485";
                }
                SmsManager smsManager = SmsManager.getDefault();
                String messageBody = getString(R.string.emergencyText) + " " + latitude + "," + longitude + "\n\nhttp://www.google.com/maps/place/" + latitude + "," + longitude;
                smsManager.sendTextMessage(emergencyContact, null, messageBody, null, null);
                Toast.makeText(context, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
                return true;
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

                    volleyService.createTrafficNotification(Address, String.valueOf(latitude), String.valueOf(longitude), locale);
                    if (speedSearch.getOsm_id() != -99) {
                        volleyService.createTrafficIncident(speedSearch.getOsm_id(), time, provider);
                    }
                    Toast.makeText(context, "Bad traffic added", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {

                }
            }
        });

        Button endJourney = (Button) findViewById(R.id.endJourney);
        endJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(context, "Journey Ended", Toast.LENGTH_SHORT).show();
                    //finish();
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.wtf("endJounrye", e.getMessage());
                }
            }
        });
    }


    private void createNotification() {
        if (missedCallNumbers != null) {
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

    private void sendDrivingSMS(String phoneNumber) {
        Log.wtf("message sent", phoneNumber);
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
//

    private boolean distanceToTrafficAddress(double trafficLatitude, double trafficLongitude) {
        Location current = new Location("current");
        current.setLatitude(latitude);
        current.setLongitude(longitude);
        Location traffic = new Location("traffic");
        traffic.setLatitude(trafficLatitude);
        traffic.setLongitude(trafficLongitude);
        if ((current.distanceTo(traffic) / 1000) < kilomPerim) {
            return true;
        }
        return false;
    }
}