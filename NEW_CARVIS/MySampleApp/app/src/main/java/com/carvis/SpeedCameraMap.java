package com.carvis;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.gson.JsonArray;
import com.mysampleapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpeedCameraMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Context mContext;
    RequestQueue queue;


    Handler polyLineHandler;
    ArrayList<LatLng> lats = new ArrayList<>();
    ArrayList<SpeedCamera> cameras = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();


        queue = Volley.newRequestQueue(mContext);
        setContentView(R.layout.activity_speed_camera_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        polyLineHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(lats.size()!=0){
                    for(int i = 0; i< lats.size();i++) {
                        if (i + 1 != lats.size()) {
                            // mMap.addMarker(new MarkerOptions().snippet("marker"));
                            mMap.addPolyline(new PolylineOptions()
                                    .add(lats.get(i), lats.get(i + 1))
                                    .width(10)
                                    .geodesic(true)
                                    .jointType(JointType.BEVEL)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(Color.RED));
                        }
                    }
                }
                lats.removeAll(lats);
                lats.clear();
            }
        };


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
        final FirebaseDatabase database;
        DatabaseReference vanRef;
        DatabaseReference cameraRef;
        FirebaseApp.initializeApp(getApplicationContext());
        database = FirebaseDatabase.getInstance();
        cameraRef = database.getReference("reportedSpeedCameras");
        vanRef = database.getReference("speedVanDecodedLocation");
        mMap = googleMap;
        LatLng dublin = new LatLng(53.348778, -6.270933);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin,11.0f));
        cameraRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                System.out.println("child added");
                double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                String time = String.valueOf(dataSnapshot.child("time").getValue());

                TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(latitude, longitude,time));
                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
                System.out.println(TemporarySpeedCamera.temporarySpeedCameras.size()+" is the temp size");
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(time).icon(BitmapDescriptorFactory.fromResource(R.drawable.speedcamera)).flat(true));

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

        int i = 0;
//        vanRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                int id = Integer.parseInt(dataSnapshot.getKey());
//                double startLatitude = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
//                double startLongitude = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
//                double endLatitude = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
//                double endLongitude = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
//
//                try {
//                    SpeedCamera s = new SpeedCamera(id, startLatitude, startLongitude, endLatitude, endLongitude);
//
//                //getSnapToRoadsPoints(getApplicationContext(), s);
//                }
//                catch(Exception e){
//                    System.out.println(e.getMessage());
//                }
//
//                mMap.addPolyline(new PolylineOptions()
//                        .add(new LatLng(startLatitude, startLongitude), new LatLng(endLatitude, endLongitude))
//                        .width(10)
//                        .geodesic(true)
//                        .jointType(JointType.BEVEL)
//                        .endCap(new RoundCap())
//                        .startCap(new RoundCap())
//                        .color(Color.RED)).isClickable();
//            }
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
//
//
//////                cameras.remove(dataSnapshot.getKey());
//////                System.out.println(cameras.size());
////
////                int id = Integer.parseInt(String.valueOf(dataSnapshot.getKey()));
////                double startLat = Double.parseDouble(String.valueOf(dataSnapshot.child("startLatitude").getValue()));
////                double startLong = Double.parseDouble(String.valueOf(dataSnapshot.child("startLongitude").getValue()));
////                double endLat = Double.parseDouble(String.valueOf(dataSnapshot.child("endLatitude").getValue()));
////                double endLong = Double.parseDouble(String.valueOf(dataSnapshot.child("endLongitude").getValue()));
////                //cameras.add(new SpeedCamera(id, startLat, startLong, endLat, endLong));
////
////                System.out.println(SpeedCamera.cameras.size());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
////                //cameras.remove(dataSnapshot.getKey());
////                SpeedCamera.removeSpeedCamera(Integer.parseInt(dataSnapshot.getKey()));
////                System.out.println("camera size : "+SpeedCamera.cameras.size());
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });


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
                                        .color(Color.RED);
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

        //getSnapToRoadsPoints(getApplicationContext(), 53.416, -6.178, 53.4511,-6.1508, mMap);
        //mMap = googleMap;


//        System.out.println(TemporarySpeedCamera.temporarySpeedCameras.size()+" M size ");
//        for(TemporarySpeedCamera t : TemporarySpeedCamera.temporarySpeedCameras){
//            LatLng latLng = new LatLng(t.getLatitude(), t.getLongitude());
//            latlngs.add(latLng);
//            mMap.addMarker(new MarkerOptions().position(latLng).title(t.getTime()).icon(BitmapDescriptorFactory.fromResource(R.drawable.speedcamera)));
//        }

       // mMap = googleMap;
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    public void getSnapToRoadsPoints(Context c, final SpeedCamera speedCamera){
        //String url = "https://roads.googleapis.com/v1/snapToRoads?path="+startLatitude+","+ startLongitude+"|"+endLatitude+","+endLongitude+"&interpolate=true&key=AIzaSyANu-d2RqCWLTyyZoh3s9lL0_PurPTNlIQ";
        //String url = "https://roads.googleapis.com/v1/snapToRoads?path=-35.27801,149.12958|-35.28032,149.12907&interpolate=true&key=AIzaSyANu-d2RqCWLTyyZoh3s9lL0_PurPTNlIQ";
//        String params = "";
//        for(Location l : locations){
//            params+= l.getLatitude()+","+l.getLongitude()+"|";
//        }
//        params = params.substring(0, params.length()-1);
//        String url = "https://roads.googleapis.com/v1/snapToRoads?path="+startLat+","+startLong+"|"+endLat+","+endLong+"&interpolate=true&key=AIzaSyANu-d2RqCWLTyyZoh3s9lL0_PurPTNlIQ";
//        System.out.println(url);
        //String url = "http://router.project-osrm.org/trip/v1/driving/"+params+"?overview=false";
        //System.out.println(url);
        String url = makeURL(speedCamera.getStartLatitude(), speedCamera.getStartLongitude(), speedCamera.getEndLatitude(), speedCamera.getEndLongitude());
        System.out.println(url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            drawPath(speedCamera, response.toString());
                            JSONObject obj = new JSONObject(response.toString());
//                            //journey.setSpeedLimit((obj.get("speed").toString()));
                            JSONArray jarray = obj.getJSONArray("snappedPoints");
                            //System.out.println("11\t"+jarray.toString());
                            //System.out.println(jarray.toString() + " _ _ _ _ _ _ ");
                            for (int i = 0; i < jarray.length(); i++) {
                                //System.out.println(jarray.length());
                                    JSONObject jsonObject = jarray.getJSONObject(i);
                                    //System.out.println(jsonObject.toString());
                                double lat = Double.parseDouble(String.valueOf(jsonObject.getJSONObject("location").get("latitude")));
                                double longitude = Double.parseDouble(String.valueOf(jsonObject.getJSONObject("location").get("longitude")));


                                    //System.out.println(Double.parseDouble(String.valueOf(jsonObject.get("latitude"))) + Double.parseDouble(String.valueOf(jsonObject.get("longitude"))));
                                    //LatLng l = new LatLng(Double.parseDouble(String.valueOf(jsonObject.get("latitude"))), Double.parseDouble(String.valueOf(jsonObject.get("longitude"))));
                                    //System.out.println(l.toString());

                                    lats.add(new LatLng(lat,longitude));

                                }
//                            for(int i = 0; i< lats.size(); i++){
//                                mMap.addPolyline(new PolylineOptions()
//                                        .add(lats.get(i), lats.get(i+1))
//                                        .width(10)
//                                        .geodesic(true)
//                                        .jointType(JointType.BEVEL)
//                                        .endCap(new RoundCap())
//                                        .startCap(new RoundCap())
//                                        .color(Color.RED));
//                            }

//                            lats.add(new LatLng(startLatitude,startLongitude));
//                            lats.add(new LatLng(endLatitude,endLongitude));
                            //polyLineHandler.sendEmptyMessage(0);
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //journey.setSpeedLimit("");
                        System.out.println(error.toString());
                    }
                });
        queue.add(jsObjRequest);
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

}
