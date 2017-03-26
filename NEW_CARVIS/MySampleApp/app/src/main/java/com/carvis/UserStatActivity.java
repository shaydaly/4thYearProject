package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mysampleapp.*;

public class UserStatActivity extends AppCompatActivity {

    UserStat userStat ;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stat);
        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Stats");


        Bundle b = getIntent().getExtras();
        userStat  = (UserStat)b.get("userStat");
        final TextView averageJourneyTime = (TextView) findViewById(R.id.averageJourneyTime);
        final TextView memberSince= (TextView) findViewById(R.id.memberSince);
        final TextView numberOfJourneys= (TextView) findViewById(R.id.numberOfUserJourneys);
        final TextView numberOfOverSpeeds= (TextView) findViewById(R.id.numberOfOverSpeedJourneys);
        final TextView overSpeedPercentage= (TextView) findViewById(R.id.overSpeedPercentage);
        final TextView overSpeedDay= (TextView) findViewById(R.id.overSpeedDay);
        final TextView overSpeedRoad= (TextView) findViewById(R.id.overSpeedRoad);
        final TextView kilomTravelled= (TextView) findViewById(R.id.kilomTravelled);
        final TextView avgJourneykilom= (TextView) findViewById(R.id.avgJourneykilom);
        try {

            memberSince.setText(String.valueOf(userStat.getMemberSince().toLocalDate()));
            averageJourneyTime.setText(String.valueOf(userStat.getAverageJourneyTime()));
            numberOfJourneys.setText(String.valueOf(userStat.getNumJourneys()));
            numberOfOverSpeeds.setText(String.valueOf(userStat.getNumOverSpeed()));
            overSpeedPercentage.setText(String.valueOf(userStat.getOverSpeedPercentage()));
            overSpeedDay.setText(String.valueOf(userStat.getOverSpeedDay()));
            overSpeedRoad.setText(String.valueOf(userStat.getOverSpeedRoad()));
            kilomTravelled.setText(String.valueOf(userStat.getKilomsTravelled()));
            avgJourneykilom.setText(String.valueOf(userStat.getAverageJourneyKiloms() ));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
    @Override
    public void onBackPressed() {
        //super.onStop();
        System.out.println("back called");
        super.onBackPressed();
        Intent myIntent = new Intent(context, com.mysampleapp.MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
        super.onStop();
    }

    @Override
    protected void onStop(){
        System.out.println("stop called");
        super.onStop();
    }

}
