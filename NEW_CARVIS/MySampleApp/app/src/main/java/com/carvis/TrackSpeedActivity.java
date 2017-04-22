package com.carvis;

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
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.CARVISAPP.*;
import com.CARVISAPP.MainActivity;
import static android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED;

public class TrackSpeedActivity extends Activity {


    boolean  speedCameraWarningShowing,
             playVoice, playSpeedVoice, blockPhoneCalls, playTrafficVoice, receiveTrafficUpdates;


    private Context context;

    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ImageView imageView50, imageView60, imageView80, imageView100, imageView120;

    private TextView currentSpeedTextView, speedCameraTextView, badTraffic;
    private CognitoUserPoolsSignInProvider provider;

    private  MediaPlayer mediaPlayer;
    private  CarvisMediaPlayer carvisMediaPlayer;
    private  ArrayList<Integer> songs;
    private ArrayList<ImageView> imageViews;

    int speed, limit, kilomPerim;
    private Date dNow;
    double latitude, longitude;

    VolleyService volleyService;

    Intent serviceIntent;
    Intent firebaseIntent;

    ArrayList<String> missedCallNumbers;

    private String locale;


    SpeedSearch speedSearch;

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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        limit = 0;

        speedSearch = new SpeedSearch(-99);

        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);
        playVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playVoiceUpdate", true);
        playSpeedVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playSpeedLimit", true);
        blockPhoneCalls = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("blockIncomingCalls", false);
        playTrafficVoice = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playTrafficUpdates", false);
        receiveTrafficUpdates = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("receiveTrafficNotifications", false);


        String kilom = PreferenceManager.getDefaultSharedPreferences(context).getString("kilomPerim", "");
        if(kilom.equals("")){
            kilom = "5";
        }
        kilomPerim = Integer.parseInt(kilom);

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
                        limit = intent.getIntExtra("speedLimit", 0);
                        chooseSpeedImage(limit);
                        speedSearch.setOsm_id(intent.getIntExtra("osmID", 0));

                    } else if (intent.getAction().equals(MyLocationService.PLAY_SPEED_MESSAGE) && playSpeedVoice) {
                        carvisMediaPlayer.addSongToQueue(R.raw.speedlimitpolly);
                    }
                    else if (intent.getAction().equals(MyLocationService.PLAY_CAMERA_MESSAGE)) {
                        double cameraLatitude = intent.getDoubleExtra("latitude", 0);
                        double cameraLongitude = intent.getDoubleExtra("longitude", 0);
                        String time = intent.getStringExtra("time");
                        String address = SpeedCamera.getSpeedCameraAddress(context, cameraLatitude, cameraLongitude);

                        if (!speedCameraWarningShowing) {
                            displaySpeedCameraInfo(address, time);
                        }

                        if (playVoice) {
                            carvisMediaPlayer.addSongToQueue(R.raw.speedcamerapolly);
                        }

                    } else if (intent.getAction().equals(ACTION_PHONE_STATE_CHANGED) && blockPhoneCalls) {
                        blockIncomeCalls(intent);
                    } else if (intent.getAction().equals(CarvisFireBaseMessagingService.TAG)) {
                        if (receiveTrafficUpdates) {
                            if (distanceToTrafficAddress(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0))) {
                                showBadTrafficLocation(intent.getStringExtra("badTrafficLocation"));
                                if (playTrafficVoice) {
                                    carvisMediaPlayer.addSongToQueue(R.raw.badtraffic);
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
    }

    @Override
    protected void onStop() {
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
    }

    @Override
    public void onBackPressed() {
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

    private void blockIncomeCalls(Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");

            if(missedCallNumbers==null){
                missedCallNumbers = new ArrayList<>();
            }
            if (!missedCallNumbers.contains(phoneNumber)) {
                missedCallNumbers.add(phoneNumber);
                sendDrivingSMS(phoneNumber);
            }
            if ((phoneNumber != null)) {
                telephonyService.endCall();
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
                try {
                    if(!emergencyContact.equals("")) {
                        SmsManager smsManager = SmsManager.getDefault();
                        String messageBody = getString(R.string.emergencyText) + " " + latitude + "," + longitude + "\n\nhttp://www.google.com/maps/place/" + latitude + "," + longitude;
                        smsManager.sendTextMessage(emergencyContact, null, messageBody, null, null);
                        Toast.makeText(context, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context, "No emergency contact set", Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception e){

                }
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

                notification.setSmallIcon(R.mipmap.ic_launcher);
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
        try {
            Log.wtf("message sent", phoneNumber);
            SmsManager smsManager = SmsManager.getDefault();
            String messageBody = getString(R.string.currentlyDriving);
            smsManager.sendTextMessage(phoneNumber, null, messageBody, null, null);
            Toast.makeText(context, "MESSAGE SENT", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Log.wtf("SMS", e.getMessage());
        }
    }

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