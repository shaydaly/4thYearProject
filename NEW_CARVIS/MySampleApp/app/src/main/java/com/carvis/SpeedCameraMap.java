package com.carvis;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
//import com.google.maps.*;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.CARVISAPP.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpeedCameraMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context mContext;
    RequestQueue queue;

    FirebaseDatabase database;
    DatabaseReference vanRef,cameraRef, trafficIncidentsRef;

    private boolean isInFocus = false;
    Handler polyLineHandler;
    ArrayList<LatLng> lats = new ArrayList<>();
    ArrayList<SpeedCamera> cameras = new ArrayList<>();
    ArrayList<Circle> circles;


    Handler trafficUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            for(TrafficUpdate trafficUpdate : TrafficUpdate.trafficUpdates){
//
//            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();


        circles = new ArrayList<>();

        setContentView(R.layout.activity_speed_camera_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }




    @Override
    protected void onStop(){
        super.onStop();
        if (!isInFocus) finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        FirebaseApp.initializeApp(mContext);
        database = FirebaseDatabase.getInstance();


        cameraRef = database.getReference("reportedSpeedCameras");
        vanRef = database.getReference("speedVanDecodedLocation");
        trafficIncidentsRef = database.getReference("reportedTrafficIncident");
        mMap = googleMap;
        LatLng dublin = new LatLng(53.348778, -6.270933);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin,11.0f));


        createCameraReference();
        createTrafficReference();
        createVanReference();
    }



    private void createTrafficReference(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                trafficIncidentsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                        double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                        String time = String.valueOf(dataSnapshot.child("time").getValue());
                        Log.i("shay", "child added");
                        Circle circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(latitude, longitude))
                                .radius(250)
                                .strokeColor(ContextCompat.getColor(mContext, R.color.redTransparent))
                                .fillColor(ContextCompat.getColor(mContext, R.color.redTransparent)));

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Bad Traffic Reported\t "+time).alpha(0.00f));

                        Log.i("shay", latitude+"\t"+longitude+"\t"+time);
                        //TrafficUpdate.addTrafficUpdateToList(new TrafficUpdate(latitude,longitude, time));
                    }


                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                trafficUpdateHandler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    private void createCameraReference(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                cameraRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        System.out.println("child added");
                        double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                        double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                        String time = String.valueOf(dataSnapshot.child("time").getValue());

                        TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(latitude, longitude,time));
                        //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
                        LatLng latLng = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(time).icon(BitmapDescriptorFactory.fromResource(R.drawable.new_speed_amera)));



                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                        double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                        String time = String.valueOf(dataSnapshot.child("time").getValue());
                        TemporarySpeedCamera.deleteTemporaryCamera(new TemporarySpeedCamera(latitude, longitude, time));
//                //cameras.remove(dataSnapshot.getKey());
//                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
//                System.out.println("camera size : "+SpeedCamera.cameras.size());
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
                trafficUpdateHandler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }

    private void createVanReference(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                vanRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        try {

//                    for (DataSnapshot snapshot : dataSnapshot.child("latLngs").getChildren()) {
//                        System.out.println(snapshot.child("latitude").getValue());
//
//
//                    }
                            PolylineOptions rectOptions = new PolylineOptions()
                                    .width(10)
                                    .geodesic(true)
                                    .jointType(JointType.BEVEL)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(getResources().getColor(R.color.vanMapColor));
                            for (DataSnapshot snapshot : dataSnapshot.child("latLngs").getChildren()) {
                                rectOptions.add(new LatLng(Double.parseDouble(String.valueOf(snapshot.child("latitude").getValue())), Double.parseDouble(String.valueOf(snapshot.child("longitude").getValue()))));
                            }
                            mMap.addPolyline(rectOptions);

                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                trafficUpdateHandler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.CARVISAPP.MainActivity.class);
        startActivity(intent);
    }

}