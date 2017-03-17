package com.carvis;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Seamus on 16/03/2017.
 */

public class SpeedVanLocations {
    List<LatLng> latLngs;

    public SpeedVanLocations(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    SpeedVanLocations(){

    }


    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }
}
