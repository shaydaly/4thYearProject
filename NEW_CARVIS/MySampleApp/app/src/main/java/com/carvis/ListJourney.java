//package com.carvis;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import com.mysampleapp.R;
//
//public class ListJourney extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_list_journey);
//    }
//}

package com.carvis;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.value;


public class ListJourney extends ListActivity {



    Context context;
    CognitoUserPoolsSignInProvider provider;
    Journey journey;
    ArrayList<JourneyFragment> journeyFragments;
    ArrayList<String> journeyIDs;
    ArrayList<String> timestamps;
    ArrayList<String> durations;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            setListAdapter(new MobileArrayAdapter(context, journeyIDs, timestamps, durations));
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            fillJourneyList();
        }
    };

    Handler fragmentHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            goToMap(journeyFragments);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));

        journey = new Journey();
        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);



        getUserJourneys();

//        //journey.getUsersJourneys(context, provider.getUserName());
//        for(int i= 0; i<=50; i++ ){
//            System.out.println(i);
//        }

//        ArrayList<String > j ;
//        j = journey.getListOfJourneys(context);
//        setListAdapter(new MobileArrayAdapter(this, j));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        final String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
        getFragments(selectedValue);


    }

    public void getFragments(String selectedValue){
        final String val = selectedValue;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                journey.getJourneyFragments(context, provider.getUserName(), val);
                long futureTme = System.currentTimeMillis()+3000;
                while (System.currentTimeMillis()< futureTme){
                    synchronized (this){
                        try{
                            wait(futureTme-System.currentTimeMillis());
                        }
                        catch(Exception e){

                        }
                    }
                }
                //call function
                journeyFragments = journey.getListOfJourneyFragments(context);
                fragmentHandler.sendEmptyMessage(0);
            }

        };
        Thread fragments = new Thread(r);
        fragments.start();
    }

    public void goToMap(ArrayList<JourneyFragment> fragments){
        Intent myIntent = new Intent(this, MapsActivity.class);
        ArrayList <JourneyFragment> addyExtras = new ArrayList <JourneyFragment>();

        for (int i = 0; i < fragments.size(); i++)
            addyExtras.add (fragments.get(i));

        System.out.println("(((("+addyExtras.size());
        myIntent.putExtra ("mylist", addyExtras);

        myIntent.putExtra("key", fragments); //Optional parameters

        this.startActivity(myIntent);


    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        super.onStop();
        journey.clearJourneyFragments();
    }

    public void getUserJourneys(){
        journey.getUsersJourneys(context, provider.getUserName());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTme = System.currentTimeMillis()+3000;
                while (System.currentTimeMillis()< futureTme){
                    synchronized (this){
                        try{
                            wait(futureTme-System.currentTimeMillis());
                        }
                        catch(Exception e){

                        }
                    }
                }
                handler2.sendEmptyMessage(0);
            }
        };
        Thread userJourneys = new Thread(r);
        userJourneys.start();
    }

    public void fillJourneyList() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<Journey> js = journey.getListOfJourneys(context);
                    journeyIDs = new ArrayList<String>();
                    timestamps = new ArrayList<String>();
                    durations = new ArrayList<String>();
                    for (int i = 0; i < js.size(); i++) {
                        journeyIDs.add(js.get(i).getJourneyID());
                        timestamps.add(js.get(i).getStart());
                        durations.add(js.get(i).getJourneyDuration());
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread journeyList = new Thread(runnable);
        journeyList.start();
    }

}
