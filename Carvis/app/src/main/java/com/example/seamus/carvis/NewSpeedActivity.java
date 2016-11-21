package com.example.seamus.carvis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.example.geofenceapp.R;

public class NewSpeedActivity extends Activity {
    Context context;
    final TextView editText = (TextView) findViewById(R.id.theSpeed);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_speed);
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                Toast.makeText(context, "Current speed:" + location.getSpeed(),

                        Toast.LENGTH_SHORT).show();
                editText.setText("Current speed:"+String.valueOf(location.getSpeed()));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {

            }
        };
    }
}
                //; locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); }




