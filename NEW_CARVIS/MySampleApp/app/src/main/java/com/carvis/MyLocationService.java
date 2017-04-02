package com.carvis;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    public MyLocationService() {
    }

    public final String TAG = "shay";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "started");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run");
                    for(int i=0; i< 10; i++){
                        Log.i(TAG, "run:="+i);
                    }

            }
        };
        Thread thread = new Thread(r);
        thread.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(TAG, "created");


        //setContentView(mLocationView);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }



    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch(Exception e){

        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, String.valueOf(location.getLatitude()));
    }
}
