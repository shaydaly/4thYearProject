package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.CARVISAPP.R;

import java.util.ArrayList;

public class MissedCall extends AppCompatActivity {
    ArrayList<String> numbers;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.missedCallToolBar);

        mContext = getApplicationContext();

        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Missed Calls");

        // Get the reference of movies
        ListView moviesList = (ListView) findViewById(R.id.missedCallList);


       numbers = (ArrayList<String>) getIntent().getStringArrayListExtra("numbers");

        // Create The Adapter with passing ArrayList as 3rd parameter
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, numbers);
        // Set The Adapter
        moviesList.setAdapter(arrayAdapter);

        // register onClickListener to handle click events on each item
        moviesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // argument position gives the index of item which is clicked
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                String selectedNumber = numbers.get(position);
                Toast.makeText(getApplicationContext(), "Movie Selected : " + selectedNumber, Toast.LENGTH_LONG).show();


                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+selectedNumber));
                if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(phoneIntent);
            }
        });
    }
}
