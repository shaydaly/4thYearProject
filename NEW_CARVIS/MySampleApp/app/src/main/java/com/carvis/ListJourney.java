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


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.ProgressBar;
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


public class ListJourney extends ListActivity {

    Context context;
    CognitoUserPoolsSignInProvider provider;
    Journey journey;
    ArrayList<JourneyFragment> journeyFragments;
    ArrayList<String> journeyIDs;
    ArrayList<String> timestamps;
    ArrayList<String> durations;
    VolleyService volleyService;

    ProgressBar pBar;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                setListAdapter(new MobileArrayAdapter(context, journeyIDs, timestamps, durations));
            }
            catch(NullPointerException e){

            }
            catch(Exception ex){

            }

        }
    };

    Handler userJourneyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            fillJourneyList();
        }
    };
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
 //setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));

        journey = new Journey();
        context = getApplicationContext();
        provider = new CognitoUserPoolsSignInProvider(context);
        volleyService = new VolleyService(context);

        pBar = (ProgressBar) findViewById(R.id.listProgressBar);

        getUserJourneys();


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        final String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
        getFragments(selectedValue);


    }

    public void getFragments(final String selectedValue){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //journey.getJourneyFragments(context, provider.getUserName(), selectedValue);
                volleyService.getJourneyFragments(provider.getUserName(), selectedValue, journey);
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
    }

    public void getUserJourneys(){
//        journey.getUsersJourneys(context, provider.getUserName());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try{
                    volleyService.getUsersJourneys(provider.getUserName(),journey);
                userJourneyHandler.sendEmptyMessage(0);
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
                    while(js.size()==0) {
                       // pBar.setVisibility(View.VISIBLE);
                        js = journey.getListOfJourneys();
                    }

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
                handler.sendEmptyMessage(0);
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
