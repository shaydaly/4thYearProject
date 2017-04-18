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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




//        polyLineHandler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                if(lats.size()!=0){
//                    for(int i = 0; i< lats.size();i++) {
//                        if (i + 1 != lats.size()) {
//                            // mMap.addMarker(new MarkerOptions().snippet("marker"));
//                            mMap.addPolyline(new PolylineOptions()
//                                    .add(lats.get(i), lats.get(i + 1))
//                                    .width(10)
//                                    .geodesic(true)
//                                    .jointType(JointType.BEVEL)
//                                    .endCap(new RoundCap())
//                                    .startCap(new RoundCap())
//                                    .color(Color.RED));
//                        }
//                    }
//                }
//                lats.removeAll(lats);
//                lats.clear();
//            }
//        };
    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        isInFocus = hasFocus;
//    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.wtf("ONSTOP", "CALLED");
        if (!isInFocus) finish();
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.wtf("onPause", "CALLED");
    }
    @Override
    protected void onResume(){
        super.onResume();
        Log.wtf("onResume", "CALLED");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.wtf("onDestroy", "CALLED");
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




        //getSnapToRoadsPoints(getApplicationContext(), 53.416, -6.178, 53.4511,-6.1508, mMap);
        //mMap = googleMap;


//        System.out.println(TemporarySpeedCamera.temporarySpeedCameras.size()+" M size ");
//        for(TemporarySpeedCamera t : TemporarySpeedCamera.temporarySpeedCameras){
//            LatLng latLng = new LatLng(t.getLatitude(), t.getLongitude());
//            latlngs.add(latLng);
//            mMap.addMarker(new MarkerOptions().position(latLng).title(t.getTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.speedcamera)));
//        }
    }

//    private List<SnappedPoint> snapToRoads(GeoApiContext context) throws Exception {
//        List<SnappedPoint> snappedPoints = new ArrayList<>();
//
//        int offset = 0;
//        while (offset < mCapturedLocations.size()) {
//            // Calculate which points to include in this request. We can't exceed the API's
//            // maximum and we want to ensure some overlap so the API can infer a good location for
//            // the first few points in each request.
//            if (offset > 0) {
//                offset -= PAGINATION_OVERLAP;   // Rewind to include some previous points.
//            }
//            int lowerBound = offset;
//            int upperBound = Math.min(offset + PAGE_SIZE_LIMIT, mCapturedLocations.size());
//
//            // Get the data we need for this page.
//            LatLng[] page = mCapturedLocations
//                    .subList(lowerBound, upperBound)
//                    .toArray(new LatLng[upperBound - lowerBound]);
//
//            // Perform the request. Because we have interpolate=true, we will get extra data points
//            // between our originally requested path. To ensure we can concatenate these points, we
//            // only start adding once we've hit the first new point (that is, skip the overlap).
//            SnappedPoint[] points = RoadsApi.snapToRoads(context, true, page).await();
//            boolean passedOverlap = false;
//            for (SnappedPoint point : points) {
//                if (offset == 0 || point.originalIndex >= PAGINATION_OVERLAP - 1) {
//                    passedOverlap = true;
//                }
//                if (passedOverlap) {
//                    snappedPoints.add(point);
//                }
//            }
//
//            offset = upperBound;
//        }
//
//        return snappedPoints;
//    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyANu-d2RqCWLTyyZoh3s9lL0_PurPTNlIQ");
        return urlString.toString();
    }


    public void drawPath(SpeedCamera s, String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);



            FirebaseApp.initializeApp(mContext);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("speedVanDecodedLocation");

            myRef.push().setValue(new SpeedVanLocations(list));

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);

        }
//        mMap.addMarker(new MarkerOptions().position(poly.get(0)).title(String.valueOf(poly.get(0).latitude+","+poly.get(0).longitude)));
//        mMap.addMarker(new MarkerOptions().position(poly.get(poly.size()-1)).title(String.valueOf(poly.get(poly.size()-1).latitude+","+poly.get(poly.size()-1).longitude)));
        return poly;
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

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("bad traffic reported\t "+time).alpha(0.00f));

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
//                        mMap.addPolyline(new PolylineOptions()
//                                .add(new LatLng(Double.parseDouble(String.valueOf(snapshot.child("latitude").getValue())), Double.parseDouble(String.valueOf(snapshot.child("longitude").getValue()))))
//                                .width(10)
//                                .geodesic(true)
//                                .jointType(JointType.BEVEL)
//                                .endCap(new RoundCap())
//                                .startCap(new RoundCap())
//                                .color(Color.RED)).isClickable();
                            }
                            mMap.addPolyline(rectOptions);

                        }
                        catch(Exception e){
                            System.out.println(e.getMessage());
                        }
//                mMap.addPolyline(new PolylineOptions()
//                        .add(new LatLng(startLatitude, startLongitude), new LatLng(endLatitude, endLongitude))
//                        .width(10)
//                        .geodesic(true)
//                        .jointType(JointType.BEVEL)
//                        .endCap(new RoundCap())
//                        .startCap(new RoundCap())
//                        .color(Color.RED)).isClickable();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {


////                cameras.remove(dataSnapshot.getKey());
////                System.out.println(cameras.size());
//
//                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
//                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
//                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
//                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
//                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
//                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
//
//                System.out.println(SpeedCamera.cameras.size());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
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


    @Override
    public void onBackPressed() {
        //super.onStop();
       Log.wtf("onBackPressed", "CALLEd");
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), com.CARVISAPP.MainActivity.class);
        startActivity(intent);
    }

}
