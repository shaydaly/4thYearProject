package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mysampleapp.*;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        try {
            userStat.getMonthlyKilomTravelled();
            final TextView averageJourneyTime = (TextView) findViewById(R.id.averageJourneyTime);
            final TextView memberSince = (TextView) findViewById(R.id.memberSince);
            final TextView numberOfJourneys = (TextView) findViewById(R.id.numberOfUserJourneys);
            final TextView numberOfOverSpeeds = (TextView) findViewById(R.id.numberOfOverSpeedJourneys);
            final TextView overSpeedPercentage = (TextView) findViewById(R.id.overSpeedPercentage);
            final TextView overSpeedDay = (TextView) findViewById(R.id.overSpeedDay);
            final TextView overSpeedRoad = (TextView) findViewById(R.id.overSpeedRoad);
            final TextView kilomTravelled = (TextView) findViewById(R.id.kilomTravelled);
            final TextView avgJourneykilom = (TextView) findViewById(R.id.avgJourneykilom);
            try {
                memberSince.setText(getString(R.string.averageSpeed) + "\n" + "\n" + "\n" + String.valueOf(userStat.getAverageSpeed()));
                averageJourneyTime.setText(getString(R.string.averageJourneyTime) + "\n" + "\n" + String.valueOf(userStat.getAverageJourneyTime()));
                numberOfJourneys.setText(getString(R.string.numJourneys) + "\n" + "\n" + String.valueOf(userStat.getNumJourneys()));
                numberOfOverSpeeds.setText(getString(R.string.numOverSpeeds) + "\n" + "\n" + String.valueOf(userStat.getNumOverSpeed()));
                overSpeedPercentage.setText(getString(R.string.percentageOverSpeed) + "\n" + "\n" + String.valueOf(userStat.getOverSpeedPercentage()));
                overSpeedDay.setText(getString(R.string.dayWithMostOverSpeed) + "\n" + "\n" + String.valueOf(userStat.getMostOverSpedDay()));
                overSpeedRoad.setText(getString(R.string.mostOverSpeedRoad) + "\n" + "\n" + String.valueOf(userStat.getRoadAddress()));
                kilomTravelled.setText(getString(R.string.kmTravelled) + "\n" + "\n" + String.valueOf(userStat.getKilomsTravelled()));
                avgJourneykilom.setText(getString(R.string.avgJourneyKM) + "\n" + "\n" + String.valueOf(userStat.getAverageJourneyKiloms()));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        catch (Exception e){
            Log.wtf("errorr ", e.getMessage());
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


    public void showToast(View view){
                LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.speed_camera_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
        header.setText("Monthly Kilom");


        try {
            HashMap<String, Double> monthlyKilom = userStat.getMonthlyKilom();
            TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
            //Log.wtf("map size", String.valueOf(monthlyKilom.size()));
            Iterator it = monthlyKilom.entrySet().iterator();

            String thisMonth = DateTime.now().toString("MMM");

            String output = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + "\t" + pair.getValue());
                output = output + pair.getKey() + ":\t\t" + pair.getValue() + "\n";
                //it.remove(); // avoids a ConcurrentModificationException
            }


            text.setText(output);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER , 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        catch (Exception e){

        }
    }

    public void showOverSpeedDays(View view){
        try {
            Map<String, Integer> days = userStat.getMostCommon();
            if (days != null) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.speed_camera_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));
                TextView header = (TextView) layout.findViewById(R.id.speedToastHeader);
                header.setText(getString(R.string.overSpeedDayTitle));


                TextView text = (TextView) layout.findViewById(R.id.speedVanLocation);
                //Log.wtf("map size", String.valueOf(monthlyKilom.size()));
                Iterator it = days.entrySet().iterator();

                String thisMonth = DateTime.now().toString("MMM");

                String output = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    output = output + pair.getKey() + ":\t\t" + pair.getValue() + "\n";
                    //it.remove(); // avoids a ConcurrentModificationException
                }


                text.setText(output);

                Toast toast = new Toast(context);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        }
        catch (Exception e){

        }
    }

}
