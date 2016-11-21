package com.example.seamus.carvis;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;


public class LocationService {

    public String getLocation(Activity activity) {


        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(android.location.Location location) {
                System.out.println(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permissions location");
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        double longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
        double latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
        return longitude + " | " + latitude;

    }


}
