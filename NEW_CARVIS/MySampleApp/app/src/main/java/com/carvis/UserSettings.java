package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.mysampleapp.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserSettings extends AppCompatActivity {

    Context context;
    Spinner spinner , voiceSpinner, speedLimitSpinner, blockIncomingCallSpinner;

    TextView memberSince;
    String[] counties;
    String emergencyContact;
    EditText editText ;
    SharedPreferences prefs;

    Switch blockCallsSwitch, speedCameraSwitch, overLimitSwitch, receiveTrafficUpdates;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        memberSince = (TextView)findViewById(R.id.memberSinceDisplay);
        memberSince.setText(prefs.getString("memberSince", ""));


        //Log.wtf("TOKEM",FirebaseInstanceId.getInstance().getToken());
        //FirebaseMessaging.getInstance().send(RemoteMessage);


        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);

        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.settings);

        spinner  = (Spinner) findViewById(R.id.localeSpinner);
        //voiceSpinner = (Spinner)findViewById(R.id.speedCameraVoiceUpdateSpinner);
        //speedLimitSpinner = (Spinner) findViewById(R.id.speedOverLimitVoiceSpinner);
        //blockIncomingCallSpinner = (Spinner) findViewById(R.id.blockIncomingSpinner);

        spinner.setBackgroundColor(Color.WHITE);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.counties, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        emergencyContact = PreferenceManager.getDefaultSharedPreferences(context).getString("emergencyContact", null);
        editText = (EditText)findViewById(R.id.emergencyContact);

        editText.setText(emergencyContact);

        blockCallsSwitch = (Switch)findViewById(R.id.blockIncomingCallsSwitch);
        overLimitSwitch = (Switch)findViewById(R.id.overSpeedLimitSwitch);
        speedCameraSwitch = (Switch)findViewById(R.id.speedCameraNotificationSwitch);
        receiveTrafficUpdates  = (Switch)findViewById(R.id.recieveTrafficUpdatesSwitch);

        Resources res = getResources();
        final String[] counties = res.getStringArray(R.array.counties);
        final String[] playVoiceArray = res.getStringArray(R.array.voiceUpdateChoices);
        int position = getPosition(counties);
//        int postionOfPlayVoicePosition = getPositionOfPlayVoice(playVoiceArray);
//        int speedPos = getSpeedPosition(playVoiceArray);

        spinner.setSelection(position);
        //voiceSpinner.setSelection(postionOfPlayVoicePosition);

        //speedLimitSpinner.setSelection(speedPos);

        if(prefs.contains("playVoiceUpdate")){
            if(prefs.getBoolean("playVoiceUpdate", false)){
                Log.wtf("playVoiceUpdate", "0");
                //voiceSpinner.setSelection(0);
                speedCameraSwitch.setChecked(true);
            }
            else{
                Log.wtf("playVoiceUpdate", "1");
                //voiceSpinner.setSelection(1);
                speedCameraSwitch.setChecked(false);
            }
        }
        if(prefs.contains("receiveTrafficNotifications")){
            if(prefs.getBoolean("receiveTrafficNotifications", false)){
                Log.wtf("playVoiceUpdate", "0");
                //voiceSpinner.setSelection(0);
                receiveTrafficUpdates.setChecked(true);
            }
            else{
                Log.wtf("playVoiceUpdate", "1");
                //voiceSpinner.setSelection(1);
                receiveTrafficUpdates.setChecked(false);
            }
        }




        if(prefs.contains("playSpeedLimit")){
            if(prefs.getBoolean("playSpeedLimit", false)){
                Log.wtf("playSpeedLimit", "0");
                overLimitSwitch.setChecked(true);
            }
            else{
                Log.wtf("playSpeedLimit", "1");
//                speedLimitSpinner.setSelection(1);
                overLimitSwitch.setChecked(false);
            }
        }

        if(prefs.contains("blockIncomingCalls")){
            if(prefs.getBoolean("blockIncomingCalls", false)){
                Log.wtf("blockIncomingCalls", "1");
                //blockIncomingCallSpinner.setSelection(1);
                blockCallsSwitch.setChecked(true);
            }
            else{
                Log.wtf("blockIncomingCalls", "1");
                //blockIncomingCallSpinner.setSelection(0);
                blockCallsSwitch.setChecked(false);
            }
        }

        speedCameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(speedCameraSwitch.isChecked()){
                    prefs.edit()
                            .putBoolean("playVoiceUpdate", true)
                            .commit();
                }
                else{
                    prefs.edit()
                            .putBoolean("playVoiceUpdate", false)
                            .commit();
                }
            }
        });


        receiveTrafficUpdates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(receiveTrafficUpdates.isChecked()){
                    FirebaseMessaging.getInstance().subscribeToTopic("trafficUpdates");
                    prefs.edit()
                            .putBoolean("receiveTrafficNotifications", true)
                            .commit();
                }
                else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("trafficUpdates");
                    prefs.edit()
                            .putBoolean("receiveTrafficNotifications", false)
                            .commit();
                }
            }
        });



        blockCallsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(blockCallsSwitch.isChecked()){
                    prefs.edit()
                            .putBoolean("blockIncomingCalls", true)
                            .commit();
                }
                else{
                    prefs.edit()
                            .putBoolean("blockIncomingCalls", false)
                            .commit();
                }
            }
        });

        overLimitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(overLimitSwitch.isChecked()){
                    prefs.edit()
                            .putBoolean("playSpeedLimit", true)
                            .commit();
                }
                else{
                    prefs.edit()
                            .putBoolean("playSpeedLimit", false)
                            .commit();
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                System.out.println(counties[position]);
                saveLocaleSharedPreferences(counties[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
//
//        voiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//
//                Log.wtf("spnner   ",String.valueOf(voiceSpinner.getSelectedItem()));
//                if(String.valueOf(voiceSpinner.getSelectedItem()).equals("YES")) {
//                    prefs.edit()
//                            .putBoolean("playVoiceUpdate", true)
//                            .commit();
//                }
//                else{
//                    prefs.edit()
//                            .putBoolean("playVoiceUpdate", false)
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
//
//        speedLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//
//                if(String.valueOf(speedLimitSpinner.getSelectedItem()).equals("YES")){
//                    prefs.edit()
//                            .putBoolean("playSpeedLimit", true)
//                            .commit();
//                }
//                else{
//                    prefs.edit()
//                            .putBoolean("playSpeedLimit", false)
//                            .commit();
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });


//        blockIncomingCallSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//
//                if(String.valueOf(blockIncomingCallSpinner.getSelectedItem()).equals("YES")) {
//                    prefs.edit()
//                            .putBoolean("blockIncomingCalls", true)
//                            .commit();
//                }
//                else{
//                    prefs.edit()
//                            .putBoolean("blockIncomingCalls", false)
//                            .commit();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });

    }







    public void saveLocaleSharedPreferences(String locale){
        prefs.edit()
                .putString("locale", locale)
                .commit();
    }

    public void getCityAddress(double lat, double lon) {
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                System.out.println("LOCAL  "+addresses.get(0).getLocality());
            } else {
                // do your staff
            }
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }



    public int getPosition(String[]counties) {
        int position = 0;
        if(prefs.contains("locale")) {
            String locale = PreferenceManager.getDefaultSharedPreferences(context).getString("locale", null);
            for (int i = 0; i < counties.length; i++) {
                if (counties[i].equals(locale)) {
                    return i;
                }
            }
        }
        return position;
    }

    public int getPositionOfPlayVoice(String[]playVoiceChoices) {
        int position = 0;
        if(prefs.contains("playVoiceUpdate")) {
            boolean locale = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playVoiceUpdate", false);
            for (int i = 0; i < playVoiceChoices.length; i++) {
                if (playVoiceChoices[i].equals(locale)) {
                    return i;
                }
            }
        }
        return position;
    }

    public int getSpeedPosition(String[]playVoiceChoices) {
        int position = 0;
        if(prefs.contains("playSpeedLimit")) {
            boolean locale = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("playSpeedLimit", false);
            for (int i = 0; i < playVoiceChoices.length; i++) {
                if (playVoiceChoices[i].equals(locale)) {
                    return i;
                }
            }
        }
        return position;
    }


    @Override
    public void onBackPressed() {
        //super.onStop();
        System.out.println("back called");
        super.onBackPressed();

        String emergencyContact = String.valueOf(editText.getText());

        prefs.edit()
                .putString("emergencyContact", emergencyContact)
                .commit();
        super.onStop();
    }



}
