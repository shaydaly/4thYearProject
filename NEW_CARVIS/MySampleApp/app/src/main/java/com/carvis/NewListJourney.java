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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.mysampleapp.R;


import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.value;
import static android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED;


public class NewListJourney extends Activity {

    Context context;
    CognitoUserPoolsSignInProvider provider;
    Journey journey;
    ArrayList<JourneyFragment> journeyFragments;
    ArrayList<String> journeyIDs;
    ArrayList<String> timestamps;
    ArrayList<String> durations;
    VolleyService volleyService;

    ProgressBar pBar;
    TextView gettingJourneyTV;
    ListView listView;
    ProgressBar progressBar;


    BroadcastReceiver mBroadCastReceiver;

    Handler adapterHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                pBar.setVisibility(View.GONE);
                gettingJourneyTV.setVisibility(View.GONE);
                MobileArrayAdapter adapter = new  MobileArrayAdapter(context, journeyIDs, timestamps, durations);
                listView.setAdapter(adapter);
            }
            catch(Exception ex){
                Log.e("shay",ex.getMessage());
            }

        }
    };

//    Handler userJourneyHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            fillJourneyList();
//        }
//    };
    //    Handler snapPointHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            goToMap(journeyFragments);
//        }
//    };
    Handler fragmentHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //getSnapToRoadsPoints(context,journeyFragments);
            goToMap(journeyFragments);
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list_journey);
        //setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));


        journey = new Journey();
        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);

        listView = (ListView)findViewById(R.id.list) ;
        gettingJourneyTV = (TextView)findViewById(R.id.gettingJourneys);
        pBar = (ProgressBar)findViewById(R.id.gettingJourneysProgress);


        Toolbar toolbar = (Toolbar) findViewById(R.id.prevJourneyToolbar);

        // Set up the activity to use this toolbar. As a side effect this sets the Toolbar's title
        // to the activity's title.
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getString(R.string.journeys));


        getUserJourneys();








        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(context, i, Toast.LENGTH_LONG).show();
                final String selectedValue = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(context, selectedValue, Toast.LENGTH_SHORT).show();
                getFragments(selectedValue);
            }
//
//            @Override
//            public void onItemClick(AdapterView<?>adapter,View v, int position){
////                ItemClicked item = adapter.getItemAtPosition(position);
////
////                Intent intent = new Intent(Activity.this,destinationActivity.class);
////                //based on item add info to intent
////                startActivity(intent);
//                getFragments(selectedValue);
//            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        mBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().equals(VolleyService.JOURNEYRESPFULL)) {
                        fillJourneyList();
                    }

                    else if(intent.getAction().equals(VolleyService.JOURNEYRESPEMPTY)){
                        gettingJourneyTV.setText("No Journeys Found");
                    }

//                    else if (intent.getAction().equals(ACTION_ANSWER)) {
//                        Log.wtf("PHONE CALL RECEIVED", "phone call");
//                        blockIncomeCalls(intent);
//                    }

//                    if((speed>limit && limit!= 0)){
//                        trackSpeedView.setBackgroundColor(Color.RED);
//                    }
//                    else{
//                        trackSpeedView.setBackgroundColor(Color.WHITE);
//                    }


                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        };

        IntentFilter filter = new IntentFilter(VolleyService.JOURNEYRESPFULL);
        filter.addAction(VolleyService.JOURNEYRESPEMPTY);
        registerReceiver(mBroadCastReceiver, filter);
    }

//    @Override
//    protected void onItemClick(ListView l, View v, int position, long id) {
//
//        //get selected items
//        final String selectedValue = (String) getListAdapter().getItem(position);
//        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
//        getFragments(selectedValue);
//
//
//    }

    public void getFragments(final String selectedValue){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //journey.getJourneyFragments(context, provider.getUserName(), selectedValue);
                volleyService.getJourneyFragments(provider, selectedValue, journey);
//                long futureTme = System.currentTimeMillis()+3000;
//                while (System.currentTimeMillis()< futureTme){
//                    synchronized (this){
//                        try{
//                            wait(futureTme-System.currentTimeMillis());
//                        }
//                        catch(Exception e){
//
//                        }
//                    }
//                }
                //call function
                journeyFragments = journey.getListOfJourneyFragments();
                while(journeyFragments.size()==0){
                    journeyFragments = journey.getListOfJourneyFragments();
                }
                fragmentHandler.sendEmptyMessage(0);
            }

        };
        Thread fragments = new Thread(r);
        fragments.start();
    }

    public void goToMap(ArrayList<JourneyFragment> fragments){
        Intent myIntent = new Intent(this, MapsActivity.class);
//        ArrayList <JourneyFragment> addyExtras = new ArrayList <JourneyFragment>();
//
//        for (int i = 0; i < fragments.size(); i++)
//            addyExtras.add (fragments.get(i));
//
//        //myIntent.putExtra ("mylist", addyExtras);
        myIntent.putExtra("journeyFragments", fragments); //Optional parameters

        this.startActivity(myIntent);


    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        super.onStop();
        journey.clearJourneyFragments();
        unregisterReceiver(mBroadCastReceiver);
    }

    public void getUserJourneys(){
//        journey.getUsersJourneys(context, provider.getUserName());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    volleyService.getUsersJourneys(provider,journey);
                    //userJourneyHandler.sendEmptyMessage(0);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
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
                    ArrayList<Journey> js = journey.getListOfJourneys();

                        // pBar.setVisibility(View.VISIBLE);


                    journeyIDs = new ArrayList<>();
                    timestamps = new ArrayList<>();
                    durations = new ArrayList<>();
                    for (int i = 0; i < js.size(); i++) {
                        journeyIDs.add(js.get(i).getJourneyID());
                        timestamps.add(js.get(i).getStart());
                        durations.add(js.get(i).getJourneyDuration());
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                adapterHandler.sendEmptyMessage(0);
            }
        };
        Thread journeyList = new Thread(runnable);
        journeyList.start();
    }



//    public void getSnapToRoadsPoints(Context c, final ArrayList<JourneyFragment> fragments){
//        final Context context = c;
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                String path="";
//                for(int i = 0; i< fragments.size(); i=i+5){
//                    path += fragments.get(i).getLatitude() + "," + fragments.get(i).getLongitude() + "|";
//                }
//                path = path.substring(0, path.length()-1);
//
//                final ArrayList<LatLng> latsLongs = new ArrayList<>();
//                System.out.println("Called!!");
//                RequestQueue queue = Volley.newRequestQueue(context);
//                String url = "https://roads.googleapis.com/v1/snapToRoads?path="+path+"&interpolate=true&key=AIzaSyANu-d2RqCWLTyyZoh3s9lL0_PurPTNlIQ";
//                final JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                try {
//                                    JSONObject obj = new JSONObject(response.toString());
////                            //journey.setSpeedLimit((obj.get("speed").toString()));
//                                    JSONArray jarray = obj.getJSONArray("snappedPoints");
//                                    //System.out.println("11\t"+jarray.toString());
//
//                                    for(int i =0; i< jarray.length(); i++) {
//                                            JSONObject o = (jarray.getJSONObject(i).getJSONObject("location"));
//                                            //LatLng l = new LatLng(o.getDouble("latitude"),o.getDouble("longitude"));
//
//                                            fragments.get(i).setLatitude(o.get("latitude").toString());
//                                            fragments.get(i).setLongitude(o.get("longitude").toString());
//                                        }
//
//                                } catch (JSONException e) {
//                                    System.out.println(e.getMessage());
//                                }
//                                snapPointHandler.sendEmptyMessage(0);
//                            }
//                        }, new Response.ErrorListener() {
//
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                //journey.setSpeedLimit("");
//                                System.out.println(error.toString());
//                            }
//                        });
//
//
//                queue.add(jsObjRequest);
//                snapPointHandler.sendEmptyMessage(0);
//            }
//
//
//        };
//        Thread snapThread = new Thread(r);
//        snapThread.start();
//    }

}
