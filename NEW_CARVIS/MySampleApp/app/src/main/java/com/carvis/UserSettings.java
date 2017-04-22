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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.CARVISAPP.*;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserSettings extends AppCompatActivity {

    private Context context;
    private  Spinner spinner ;

    private  String[] counties;
    private  String emergencyContact;
    private  EditText editText, kilomPerim;
    private  SharedPreferences prefs;
    private TextView kilomPerm, receiveVoiceTraffic;

    private  Switch blockCallsSwitch, speedCameraSwitch, overLimitSwitch, receiveTrafficUpdates, receiveVoiceTrafficUpdates;

    private   String locale;
    private   FirebaseMessaging firebaseMessaging;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseMessaging = FirebaseMessaging.getInstance();
//        memberSince = (TextView)findViewById(R.id.memberSinceDisplay);
//        memberSince.setText(prefs.getString("memberSince", ""));

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
        kilomPerim = (EditText)findViewById(R.id.kilomPermInput);
        editText.setText(emergencyContact);

        kilomPerm = (TextView)findViewById(R.id.kilomPerm);
        receiveVoiceTraffic = (TextView)findViewById(R.id.receiveVoiceTraffic);
        blockCallsSwitch = (Switch)findViewById(R.id.blockIncomingCallsSwitch);
        overLimitSwitch = (Switch)findViewById(R.id.overSpeedLimitSwitch);
        speedCameraSwitch = (Switch)findViewById(R.id.speedCameraNotificationSwitch);
        receiveTrafficUpdates  = (Switch)findViewById(R.id.recieveTrafficUpdatesSwitch);
        receiveVoiceTrafficUpdates = (Switch)findViewById(R.id.receiveVoiceTrafficUpdatesSwitch);
        Resources res = getResources();
        counties = res.getStringArray(R.array.counties);
        int position = getPosition(counties);

        kiomPerimError();


        kilomPerim.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                kiomPerimError();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                Log.wtf("text changed", " after");

            }
        });



        spinner.setSelection(position);

        if(prefs.contains("playVoiceUpdate")){
            if(prefs.getBoolean("playVoiceUpdate", false)){
//                Log.wtf("playVoiceUpdate", "0");
                //voiceSpinner.setSelection(0);
                speedCameraSwitch.setChecked(true);
            }
            else{
//                Log.wtf("playVoiceUpdate", "1");
                //voiceSpinner.setSelection(1);
                speedCameraSwitch.setChecked(false);
            }
        }
        if(prefs.contains("receiveTrafficNotifications")){
            if(prefs.getBoolean("receiveTrafficNotifications", false)){
//                Log.wtf("playVoiceUpdate", "0");
                //voiceSpinner.setSelection(0);
                receiveTrafficUpdates.setChecked(true);
            }
            else{
//                Log.wtf("playVoiceUpdate", "1");
                //voiceSpinner.setSelection(1);
                receiveTrafficUpdates.setChecked(false);
            }
        }




        if(prefs.contains("playSpeedLimit")){
            if(prefs.getBoolean("playSpeedLimit", true)){
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
//                Log.wtf("blockIncomingCalls", "1");
                //blockIncomingCallSpinner.setSelection(1);
                blockCallsSwitch.setChecked(true);
            }
            else{
//                Log.wtf("blockIncomingCalls", "1");
                //blockIncomingCallSpinner.setSelection(0);
                blockCallsSwitch.setChecked(false);
            }
        }

        if(prefs.contains("playTrafficUpdates")){
            if(prefs.getBoolean("playTrafficUpdates", false)){
//                Log.wtf("playTrafficUpdates", "1");
                //blockIncomingCallSpinner.setSelection(1);
                receiveVoiceTrafficUpdates.setChecked(true);
            }
            else{
//                Log.wtf("playTrafficUpdates", "1");
                //blockIncomingCallSpinner.setSelection(0);
                receiveVoiceTrafficUpdates.setChecked(false);
            }
        }

        if(prefs.contains("kilomPerim")){
            kilomPerim.setText(prefs.getString("kilomPerim", ""));
        }

        showTrafficLabel();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                locale = parentView.getSelectedItem().toString();
                saveLocaleSharedPreferences(locale);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        speedCameraSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                try {
                    if (speedCameraSwitch.isChecked()) {
                        prefs.edit()
                                .putBoolean("playVoiceUpdate", true)
                                .commit();
                    } else {
                        prefs.edit()
                                .putBoolean("playVoiceUpdate", false)
                                .commit();
                    }
                }
                catch(Exception e){

                }
            }
        });

        receiveTrafficUpdates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                unsubcribeFromAll();

                if(receiveTrafficUpdates.isChecked()){
                    kiomPerimError();
                    kilomPerim.setEnabled(true);
                    firebaseMessaging.subscribeToTopic(locale);
                    receiveVoiceTrafficUpdates.setEnabled(true);
                    prefs.edit()
                            .putBoolean("receiveTrafficNotifications", true)
                            .commit();
                }
                else{
                    kilomPerim.setEnabled(false);
                    kilomPerim.setError(null);
                    receiveVoiceTrafficUpdates.setEnabled(false);

                    firebaseMessaging.unsubscribeFromTopic(locale);
                    prefs.edit()
                            .putBoolean("receiveTrafficNotifications", false)
                            .commit();
                }
                showTrafficLabel();
            }
        });

        receiveVoiceTrafficUpdates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                try {
                    if (receiveVoiceTrafficUpdates.isChecked()) {
                        prefs.edit()
                                .putBoolean("playTrafficUpdates", true)
                                .commit();
                    } else {
                        prefs.edit()
                                .putBoolean("playTrafficUpdates", false)
                                .commit();
                    }
                }catch (Exception e){

                }
            }
        });



        blockCallsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                try {
                    if (blockCallsSwitch.isChecked()) {
                        prefs.edit()
                                .putBoolean("blockIncomingCalls", true)
                                .commit();
                    } else {
                        prefs.edit()
                                .putBoolean("blockIncomingCalls", false)
                                .commit();
                    }
                }
                catch (Exception e){

                }
            }
        });

        overLimitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                try {
                    if (overLimitSwitch.isChecked()) {
                        prefs.edit()
                                .putBoolean("playSpeedLimit", true)
                                .commit();
                    } else {
                        prefs.edit()
                                .putBoolean("playSpeedLimit", false)
                                .commit();
                    }
                }
                catch(Exception e){

                }
            }
        });

    }







    public void saveLocaleSharedPreferences(String locale){
        try {
            prefs.edit()
                    .putString("locale", locale)
                    .commit();
        }
        catch(Exception e){

        }
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

        String kilom = kilomPerim.getText().toString();
//        if(!kilom.equals("")){
//            int kilomPerim = Integer.parseInt(kilom);
//            prefs.edit()
//                    .putInt("kilomPerim", kilomPerim)
//                    .commit();
//        }

        prefs.edit()
                .putString("emergencyContact", emergencyContact)
                .putString("kilomPerim", kilom)
                .commit();

        if(receiveTrafficUpdates.isChecked()){
            unsubcribeFromAll();
            firebaseMessaging.subscribeToTopic(locale);
        }


        Intent intent = new Intent(context, com.CARVISAPP.MainActivity.class);
        context.startActivity(intent);
    }


    private void unsubcribeFromAll(){
        for(String s: counties){
            firebaseMessaging.unsubscribeFromTopic(s);
        }
    }

    private void kiomPerimError(){
        if(receiveTrafficUpdates.isChecked() && kilomPerim.getText().toString().equals("")){
            Log.wtf("number 1","");
            kilomPerim.setError("Kilom perimeter must be indicated to receive traffic updates");
        }
        if(receiveTrafficUpdates.isChecked() && kilomPerim.getText().toString().equals("0")){
            Log.wtf("number 2","");
            kilomPerim.setError("0 should not be entered as a perimeter distance");
        }
    }

    private void showTrafficLabel(){
        if(!receiveTrafficUpdates.isChecked()) {
            receiveVoiceTrafficUpdates.setEnabled(false);
            kilomPerim.setEnabled(false);
            kilomPerm.setTextColor(getResources().getColor(R.color.silver));
            receiveVoiceTraffic.setTextColor(getResources().getColor(R.color.silver));
        }
        else{
            receiveVoiceTrafficUpdates.setEnabled(true);
            kilomPerim.setEnabled(true);
            kilomPerm.setTextColor(getResources().getColor(R.color.white));
            receiveVoiceTraffic.setTextColor(getResources().getColor(R.color.white));
        }
    }


}