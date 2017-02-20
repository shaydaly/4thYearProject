package com.carvis;

import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mysampleapp.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    private PolylineOptions rectOptions = new PolylineOptions().width(20).geodesic(true);
    private ArrayList<LatLng> latlngs = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<JourneyFragment> listOfJourneys = (ArrayList<JourneyFragment>) extras.get("mylist");

            for (int i = 0; i < listOfJourneys.size(); i++) {
                LatLng latLng = new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude()));
                latlngs.add(latLng);
                //rectOptions.add(latLng);
                //rectOptions.color(Color.RED);
                options.position(latLng);
                //options.title(listOfJourneys.get(i).getLatitude()+"_"+listOfJourneys.get(i).getLongitude());
               options.title("Speed: "+listOfJourneys.get(i).getFragmentSpeed());
                options.snippet("Limit:"+listOfJourneys.get(i).getSpeedLimit());//


                if(i+1 < listOfJourneys.size()) {
                    if (Double.parseDouble(listOfJourneys.get(i).getFragmentSpeed()) > Double.parseDouble(listOfJourneys.get(i).getSpeedLimit())) {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        options.flat(true);
                        //rectOptions.add(latLng).color(Color.RED);
                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude())), new LatLng(Double.parseDouble(listOfJourneys.get(i + 1).getLatitude()), Double.parseDouble(listOfJourneys.get(i + 1).getLongitude())))
                                .width(20)
                                .geodesic(true)
                                .color(Color.RED));
                    }
                    else {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        options.flat(true);
                        //rectOptions.add(latLng).color(Color.GREEN);
                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude())), new LatLng(Double.parseDouble(listOfJourneys.get(i + 1).getLatitude()), Double.parseDouble(listOfJourneys.get(i + 1).getLongitude())))
                                .width(20)
                                .color(Color.GREEN)
                                .geodesic(true));
                    }
                }
                googleMap.addMarker(options).setAlpha(0.0f);
                //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Dublin"+latitudes.get(i)+"_"+longitudes.get(i)));
            }

            if(latlngs.size()!= 0) {
                //LatLngBounds boundingArea = new LatLngBounds(latlngs.get(latlngs.size() - 1),latlngs.get(0));

                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngs.get(1)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(0),12.0f));
            }
            else{
                LatLng home = new LatLng(53.3514105, -6.3803316);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,14.0f));
            }
        }
    }
}
