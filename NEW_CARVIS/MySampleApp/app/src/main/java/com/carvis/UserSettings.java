package com.carvis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mysampleapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserSettings extends AppCompatActivity {

    Context context;
    Spinner spinner;

    String[] counties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        context = getApplicationContext();

        //getCityAddress(53.353964, -6.364627);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);

        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.settings);

        spinner  = (Spinner) findViewById(R.id.localeSpinner);
        spinner.setBackgroundColor(Color.WHITE);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.counties, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        Resources res = getResources();
        final String[] counties = res.getStringArray(R.array.counties);
        int position = getPosition(counties);

        spinner.setSelection(position);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                System.out.println(counties[position]);
                saveSharedPreferences(counties[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }
    public void saveSharedPreferences(String locale){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int position = 0;
        if(prefs.contains("locale")) {
            String locale = PreferenceManager.getDefaultSharedPreferences(context).getString("locale", null);
            System.out.println("locale  " + locale);
            for (int i = 0; i < counties.length; i++) {
                if (counties[i].equals(locale)) {
                    System.out.println("found " + i);
                    return i;
                }
            }
        }
        return position;
    }



}
