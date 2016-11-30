package com.example.seamus.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static java.security.AccessController.getContext;

//import com.google.android.gms.location.LocationClient;
import android.os.Vibrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class TrackSpeedActivity extends Activity {
    public static final double SPEED_LIMIT = 25.0;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    String maxSpeed="";
    static boolean locationChecking=false;

    LocationService locationService = new LocationService();
    Intent intent = getIntent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_speed);


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        TextView textView = (TextView) findViewById(R.id.currentSpeed);

        //textView.setText(String.valueOf(location.getSpeed()));

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                makeHttpRequest(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
                // Called when a new location is found by the network location provider.
                TextView textView = (TextView) findViewById(R.id.currentSpeed);
                double kilomPerHour = Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0;
                textView.setText(String.valueOf(kilomPerHour)+"km/h");
//                if(kilomPerHour >SPEED_LIMIT){
//                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//                    // Vibrate for 400 milliseconds
//                    v.vibrate(5000);
//
////                    ImageView im = (ImageView) findViewById(R.id.stopSign);
////                    im.setVisibility(View.VISIBLE);
//                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.GET_PERMISSIONS) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
        }

    }

    public void showImage(View view){
//        ImageView im = (ImageView) findViewById(R.id.stopSign);
//        im.setVisibility(View.INVISIBLE);
    }

    public void getLocation(View view){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                TextView textView = (TextView) findViewById(R.id.currentSpeed);

                textView.setText(String.valueOf(location.getSpeed())+"km/h");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };


    }


    public void stopGettingLocation(View view){
        locationChecking=false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
//    GoogleApiClient mGoogleApiClient;
//    final TextView editText = (TextView) findViewById(R.id.currentSpeed);
//
//    private Button btnGetLocation;
//    private TextView textGPS;
//    private LocationManager locationManager;
//    private LocationListener locationListener;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_track_speed);
////        if (mGoogleApiClient == null) {
////            mGoogleApiClient = new GoogleApiClient.Builder(this)
////                    .addConnectionCallbacks(this)
////                    .addOnConnectionFailedListener(this)
////                    .addApi(LocationServices.API)
////                    .addApi(AppIndex.API).build();
////        }
//
//
//
//        btnGetLocation = (Button) findViewById(R.id.trackSpeed);
//        textGPS = (TextView) findViewById(R.id.currentSpeed);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                textGPS.setText("lat: "+location.getLatitude()+" long: "+location.getLongitude());
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//            }
//        };
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{
//                        android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                        android.Manifest.permission.INTERNET
//                },10);
//                return;
//            }
//        }else{
//            //configureButton();
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        switch (requestCode){
//            case 10:
//                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    //configureButton();
//                return;
//        }
//    }

//    private void configureButton() {
//
//        btnGetLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//                }
//            }
//        });
//    }

    public void makeHttpRequest(String lon, String lat) {
        final TextView textView = (TextView) findViewById(R.id.speedLimit);

        RequestQueue queue = Volley.newRequestQueue(this);
        //String url ="http://www.openstreetmap.org/api/0.6/way/48290550";
        //String url ="http://overpass-api.de/api/interpreter?data=[out:json];way(4402297);out;";
        String url = "http://nominatim.openstreetmap.org/reverse?format=json&lat="+lat+"&lon="+lon;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText(ID(response)+"km/h");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
            }


        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "CARVIS");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public String ID(String json) {
        //return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
        String osmId = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            // JsonNode jsonNode = mapper.readValue(json,JsonNode.class);
            JSONObject jsonO = new JSONObject(json);
            //maxSpeed= String.valueOf(jsonNode.get("elements.tags"));
            osmId = jsonO.get("osm_id").toString();


            final TextView textView = (TextView) findViewById(R.id.speedLimit);

            RequestQueue queue = Volley.newRequestQueue(this);
            //String url ="http://www.openstreetmap.org/api/0.6/way/48290550";
            String url = "http://overpass-api.de/api/interpreter?data=[out:json];way(" + osmId + ");out;";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            maxSpeed = (getNewSpeed(response));

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText(error.toString());
                }


            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("User-agent", "CARVIS");
                    return headers;
                }
            };
// Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (Exception e) {

        }

        return maxSpeed;

    }


    public String getNewSpeed(String json) {
        String speed = "";
        String jas="";
        try {
            ObjectMapper mapper = new ObjectMapper();
            // JsonNode jsonNode = mapper.readValue(json,JsonNode.class);
            JSONObject jsonO = new JSONObject(json);
            //maxSpeed= String.valueOf(jsonNode.get("elements.tags"));
            jas = jsonO.get("elements").toString();

            JSONArray ja = new JSONArray(jas);
            //for(int i=0; i<ja.length(); i++){
            JSONObject j = (JSONObject)ja.get(0);
            JSONObject jo = (JSONObject)j.get("tags");


            speed= String.valueOf(jo.get("maxspeed"));

            // }



        } catch (JSONException e) {

        }
        return speed;
    }

}