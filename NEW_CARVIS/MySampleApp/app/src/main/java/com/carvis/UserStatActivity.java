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
        toolbar.setTitle(R.string.userStatistics);


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
            memberSince.setText(getString(R.string.memberSince)+"\n"+"\n"+"\n"+String.valueOf(userStat.getMemberSince().toLocalDate()));
            averageJourneyTime.setText(getString(R.string.averageJourneyTime)+"\n"+"\n"+String.valueOf(userStat.getAverageJourneyTime()));
            numberOfJourneys.setText(getString(R.string.numJourneys)+"\n"+"\n"+String.valueOf(userStat.getNumJourneys()));
            numberOfOverSpeeds.setText(getString(R.string.numOverSpeeds)+"\n"+"\n"+String.valueOf(userStat.getNumOverSpeed()));
            overSpeedPercentage.setText(getString(R.string.percentageOverSpeed)+"\n"+"\n"+String.valueOf(userStat.getOverSpeedPercentage()));
            overSpeedDay.setText(getString(R.string.dayWithMostOverSpeed)+"\n"+"\n"+String.valueOf(userStat.getOverSpeedDay()));
            overSpeedRoad.setText(getString(R.string.mostOverSpeedRoad)+"\n"+"\n"+String.valueOf(userStat.getRoadAddress()));
            kilomTravelled.setText(getString(R.string.kmTravelled)+"\n"+"\n"+String.valueOf(userStat.getKilomsTravelled()));
            avgJourneykilom.setText(getString(R.string.avgJourneyKM)+"\n"+"\n"+String.valueOf(userStat.getAverageJourneyKiloms() ));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
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
