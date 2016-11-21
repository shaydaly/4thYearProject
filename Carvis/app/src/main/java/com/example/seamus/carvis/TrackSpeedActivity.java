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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static java.security.AccessController.getContext;

//import com.google.android.gms.location.LocationClient;
import android.os.Vibrator;


public class TrackSpeedActivity extends Activity {
    public static final double SPEED_LIMIT = 25.0;
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
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
                // Called when a new location is found by the network location provider.
                TextView textView = (TextView) findViewById(R.id.currentSpeed);
                textView.setText("location");
                double kilomPerHour = Math.round((location.getSpeed() * 3.6) * 100.0) / 100.0;
                textView.setText(String.valueOf(kilomPerHour)+"km/h");
                if(kilomPerHour >SPEED_LIMIT){
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    // Vibrate for 400 milliseconds
                    v.vibrate(5000);

                    ImageView im = (ImageView) findViewById(R.id.stopSign);
                    im.setVisibility(View.VISIBLE);
                }
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
        ImageView im = (ImageView) findViewById(R.id.stopSign);
        im.setVisibility(View.INVISIBLE);
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

// Register the listener with the Location Manager to receive location updates
       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


//        locationChecking=true;
//        if(checkCallingOrSelfPermission(INITIAL_PERMS[0]) == PackageManager.PERMISSION_DENIED || checkCallingOrSelfPermission(INITIAL_PERMS[1]) == PackageManager.PERMISSION_DENIED ) {
//            ActivityCompat.requestPermissions(this, INITIAL_PERMS, 0);
//        }
//        while(locationChecking==true) {
//            String currentLocation = locationService.getLocation(this);
//            TextView locationResult = (TextView) findViewById(R.id.currentSpeed);
//            locationResult.setText(currentLocation);
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

}