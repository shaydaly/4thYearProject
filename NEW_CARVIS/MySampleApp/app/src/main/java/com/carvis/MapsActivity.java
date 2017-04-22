package com.carvis;

import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

//import com.google.android.gms.identity.intents.Address;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.JointType;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.CARVISAPP.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MarkerOptions options = new MarkerOptions();
    private PolylineOptions rectOptions = new PolylineOptions().width(20).geodesic(true);
    private ArrayList<LatLng> latlngs = new ArrayList<>();

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = getApplicationContext();

//

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
        LatLng dublin = new LatLng(53.348778, -6.270933);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin,11.0f));
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                ArrayList<JourneyFragment> listOfJourneys = (ArrayList<JourneyFragment>) extras.get("journeyFragments");


                for (int i = 0; i < listOfJourneys.size(); i++) {
                    LatLng latLng = new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude()));
                    latlngs.add(latLng);
                    options.title("Speed: " + listOfJourneys.get(i).getFragmentSpeed());
                    if(listOfJourneys.get(i).getSpeedLimit()!=0) {
                        options.snippet("Limit:" + listOfJourneys.get(i).getSpeedLimit());//
                    }
                    else{
                        options.snippet("Limit: NA");
                    }
                    if (i + 1 < listOfJourneys.size()) {
                        if ((listOfJourneys.get(i).getSpeedLimit()) == 0) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            options.flat(true);
                            options.position(latLng);
                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude())), new LatLng(Double.parseDouble(listOfJourneys.get(i + 1).getLatitude()), Double.parseDouble(listOfJourneys.get(i + 1).getLongitude())))
                                    .width(20)
                                    .geodesic(true)
                                    .jointType(JointType.BEVEL)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(Color.BLUE));
                        } else if (listOfJourneys.get(i).getFragmentSpeed() > listOfJourneys.get(i).getSpeedLimit()) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            options.flat(true);
                            options.position(latLng);
                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude())), new LatLng(Double.parseDouble(listOfJourneys.get(i + 1).getLatitude()), Double.parseDouble(listOfJourneys.get(i + 1).getLongitude())))
                                    .width(20)
                                    .geodesic(true)
                                    .jointType(JointType.BEVEL)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(Color.RED));
                        } else {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            options.flat(true);
                            options.position(latLng);
                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(Double.parseDouble(listOfJourneys.get(i).getLatitude()), Double.parseDouble(listOfJourneys.get(i).getLongitude())), new LatLng(Double.parseDouble(listOfJourneys.get(i + 1).getLatitude()), Double.parseDouble(listOfJourneys.get(i + 1).getLongitude())))
                                    .width(20)
                                    .color(Color.GREEN)
                                    .jointType(JointType.BEVEL)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .geodesic(true));
                        }
                    }
                    else {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        options.flat(true);
                        options.position(latLng);
                    }

                    if (options != null) {
                        googleMap.addMarker(options).setAlpha(0.0f);
                    }
                    //options.rotation(90f);
                }

                if (latlngs.size() != 0) {
                    Marker origin = mMap.addMarker(new MarkerOptions().position(latlngs.get(0)).title(getAddressFromLocation(0)).visible(true));
                    Marker destination = mMap.addMarker(new MarkerOptions().position(latlngs.get(latlngs.size() - 1)).title(getAddressFromLocation(latlngs.size() - 1)).visible(true));

                    origin.showInfoWindow();
                    destination.showInfoWindow();
                    origin.setZIndex(1);
                    destination.setZIndex(1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngs.get(latlngs.size() - 1), 13.0f));
                }
            } else {
                LatLng home = new LatLng(53.3514105, -6.3803316);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 13.0f));
            }
        }catch (Exception e){
            Log.e("mapException", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public String getLocation(){
        return "";
    }

    public String getAddressFromLocation(int pos){
        String address="";
        Geocoder geocoder= new Geocoder(this, Locale.ENGLISH);
        StringBuilder strAddress = new StringBuilder();
        try {
            List<Address> addresses = geocoder.getFromLocation(latlngs.get(pos).latitude,latlngs.get(pos).longitude, 1);
            if(addresses != null) {
                Address fetchedAddress = addresses.get(0);
                for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i));
                    if (i != fetchedAddress.getMaxAddressLineIndex() - 1) {
                        strAddress.append(",\t\t");
                    } else {
                        strAddress.append(".\t\t");
                    }
                }

                address = strAddress.toString();
            }
            else{
                address ="Unknown";
            }
        }
        catch (IOException e) {

        }
        return address;
    }
}
