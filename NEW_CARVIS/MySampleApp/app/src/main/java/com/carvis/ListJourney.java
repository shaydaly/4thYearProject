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
import com.carvis.MobileArrayAdapter;
import com.mysampleapp.navigation.NavigationDrawer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));


        journey = new Journey();



        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);



        journey.getUsersJourneys(context, provider.getUserName());
//        //journey.getUsersJourneys(context, provider.getUserName());
//        for(int i= 0; i<=50; i++ ){
//            System.out.println(i);
//        }
        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

                try {
                    ArrayList<Journey> js = journey.getListOfJourneys(context);
                    ArrayList<String> journeyIDs = new ArrayList<String>();
                    ArrayList<String> timestamps = new ArrayList<String>();
                    ArrayList<String> durations = new ArrayList<String>();
                    HashMap<String, String> map2 = new HashMap<String, String>();
                    for(int i = 0; i< js.size(); i++){
                        journeyIDs.add(js.get(i).getJourneyID());
                        timestamps.add(js.get(i).getStart());
                        durations.add(js.get(i).getJourneyDuration());

                    }
                    setListAdapter(new MobileArrayAdapter(context, journeyIDs, timestamps,durations));
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }, 3500);



//        ArrayList<String > j ;
//        j = journey.getListOfJourneys(context);
//        setListAdapter(new MobileArrayAdapter(this, j));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
        journey.getJourneyFragments(context, provider.getUserName(), selectedValue);



        Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                journeyFragments = journey.getListOfJourneyFragments(context);
                goToMap(journeyFragments);

            }
        }, 5000);

        Handler ha2 = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function

            }
        }, 2000);

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




        // for some reason, I remember a posting saying it's best to create a new
        // object to pass.  I have no idea why..

//        startActivity(intent);

    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        super.onStop();
        journey.clearJourneyFragments();
    }

}
