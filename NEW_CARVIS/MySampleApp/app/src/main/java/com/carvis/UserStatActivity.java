package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.CARVISAPP.*;

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

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.userStatistics);

        Bundle b = getIntent().getExtras();
        userStat  = (UserStat)b.get("userStat");
        try {
            userStat.getMonthlyKilomTravelled();
            userStat.getMonthlyJourneys();
            final TextView averageJourneyTime = (TextView) findViewById(R.id.averageJourneyTime);
            final TextView avgKilomPerSpeed = (TextView) findViewById(R.id.avgKilomSpeed);
            final TextView numberOfJourneys = (TextView) findViewById(R.id.numberOfUserJourneys);
            final TextView numberOfOverSpeeds = (TextView) findViewById(R.id.numberOfOverSpeedJourneys);
            final TextView overSpeedPercentage = (TextView) findViewById(R.id.overSpeedPercentage);
            final TextView overSpeedDay = (TextView) findViewById(R.id.overSpeedDay);
            final TextView overSpeedRoad = (TextView) findViewById(R.id.overSpeedRoad);
            final TextView kilomTravelled = (TextView) findViewById(R.id.kilomTravelled);
            final TextView avgJourneykilom = (TextView) findViewById(R.id.avgJourneykilom);
            try {
                avgKilomPerSpeed.setText(getString(R.string.avgKilomSpeed) + "\n\n" + String.valueOf(userStat.overSpeedPerKilom()));
                averageJourneyTime.setText(getString(R.string.averageJourneyTime) + "\n\n" + String.valueOf(userStat.getAverageJourneyTime()));
                numberOfJourneys.setText(getString(R.string.numJourneys) + "\n\n\n" + String.valueOf(userStat.getNumJourneys()));
                numberOfOverSpeeds.setText(getString(R.string.numOverSpeeds) + "\n\n\n" + String.valueOf(userStat.getNumOverSpeed()));
                overSpeedPercentage.setText(getString(R.string.percentageOverSpeed) + "\n\n\n" + String.valueOf(userStat.getOverSpeedPercentage()));
                overSpeedDay.setText(getString(R.string.dayWithMostOverSpeed) + "\n\n" + String.valueOf(userStat.getMostOverSpedDay()));
                overSpeedRoad.setText(getString(R.string.mostOverSpeedRoad) + "\n\n" + String.valueOf(userStat.getRoadAddress()));
                kilomTravelled.setText(getString(R.string.kmTravelled) + "\n\n" + String.valueOf(userStat.getKilomsTravelled()));
                avgJourneykilom.setText(getString(R.string.avgJourneyKM) + "\n\n" + String.valueOf(userStat.getAverageJourneyKiloms()));
            } catch (Exception e) {
            }
        }
        catch (Exception e){
        }

    }
    @Override
    public void onBackPressed() {
        //super.onStop();
        System.out.println("back called");
        super.onBackPressed();
        Intent myIntent = new Intent(context, com.CARVISAPP.MainActivity.class);
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
        View layout = inflater.inflate(R.layout.user_stat_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView header = (TextView) layout.findViewById(R.id.toastHead);
        header.setText(getString(R.string.monthlyKm));
        try {
            HashMap<String, Double> monthlyKilom = userStat.getMonthlyKilom();
            TextView label = (TextView) layout.findViewById(R.id.label );
            TextView count = (TextView) layout.findViewById(R.id.count);
            Iterator it = monthlyKilom.entrySet().iterator();

            String countOutput="";
            String labelOutput="";
            String output = "";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + "\t" + pair.getValue());
                labelOutput += pair.getKey()+"\n";
                countOutput += UserStat.getRoundedValue(Double.parseDouble(String.valueOf(pair.getValue())),2)+"\n";
            }

            label.setText(labelOutput);
            count.setText(countOutput);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.CENTER , 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        catch (Exception e){
        }
    }

    public void showMonthlyJourneys(View view){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.user_stat_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView header = (TextView) layout.findViewById(R.id.toastHead);
        header.setText(getString(R.string.montlyJourney));
        try {
            HashMap<String, Integer> monthlyKilom = userStat.getMonthlyJourneysHashMap();
            TextView label = (TextView) layout.findViewById(R.id.label );
            TextView count = (TextView) layout.findViewById(R.id.count);
            Iterator it = monthlyKilom.entrySet().iterator();

            String countOutput="";
            String labelOutput="";
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + "\t" + pair.getValue());
                labelOutput += pair.getKey()+"\n";
                countOutput += pair.getValue()+"\n";
            }
            label.setText(labelOutput);
            count.setText(countOutput);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.LEFT , 0, 0);
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
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.user_stat_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));
                TextView header = (TextView) layout.findViewById(R.id.toastHead);
                header.setText(getString(R.string.overSpeedDays));


                TextView label = (TextView) layout.findViewById(R.id.label );
                TextView count = (TextView) layout.findViewById(R.id.count);

            if(days!=null) {
                Iterator it = days.entrySet().iterator();
                String countOutput = "";
                String labelOutput = "";
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    labelOutput += pair.getKey() + "\n";
                    countOutput += pair.getValue() + "\n";
                }
                label.setText(labelOutput);
                count.setText(countOutput);
            }
            else {
                label.setText("NA");
            }
                Toast toast = new Toast(context);
                toast.setGravity(Gravity.RIGHT, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
        }
        catch (Exception e){

        }
    }

}
