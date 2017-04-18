package com.carvis;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LogWriter;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.CARVISAPP.R;

import org.json.JSONObject;

/**
 * Created by Seamus on 14/04/2017.
 */

public class CarvisFireBaseMessagingService extends FirebaseMessagingService{

    public static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.wtf(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Log.wtf(TAG, "Message data payload: " + remoteMessage.getData());

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
            handleNow();
        }

        // Check if message contains a notification payload.
        if(remoteMessage.getData()!= null){
            try {
//                Log.wtf("data message", String.valueOf(remoteMessage.getData()));
                JSONObject jsonObject = new JSONObject(remoteMessage.getData());
                double latitude = jsonObject.getDouble("latitude");
                double longitude = jsonObject.getDouble("longitude");
                String address = jsonObject.getString("address");

                Intent intent = new Intent();
                intent.setAction(TAG);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("badTrafficLocation", address);
                getApplicationContext().sendBroadcast(intent);

            }

            catch(Exception e){
                Log.wtf("message ex", e.getMessage());
            }
        }
        if (remoteMessage.getNotification() != null) {
//            try {
//                Log.wtf("remote message", String.valueOf(remoteMessage.getNotification()));
//                JSONObject jsonObject = new JSONObject(remoteMessage.getNotification().getBody());
//                double latitude = Double.parseDouble(jsonObject.getString("latitude"));
//                double longitude = Double.parseDouble(jsonObject.getString("longitude"));
//                String address = jsonObject.getString("address");
//
//                Intent intent = new Intent();
//                intent.setAction(TAG);
//                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//                intent.putExtra("latitude", latitude);
//                intent.putExtra("longitude", longitude);
//                intent.putExtra("badTrafficLocation", address);
//                getApplicationContext().sendBroadcast(intent);
//
//                Log.wtf(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            }
//
//            catch(Exception e){
//                Log.wtf("message ex", e.getMessage());
//            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]




    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
//        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}