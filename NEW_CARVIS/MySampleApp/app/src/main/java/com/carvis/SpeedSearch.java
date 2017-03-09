package com.carvis;

import android.location.Location;

/**
 * Created by Seamus on 09/03/2017.
 */

public class SpeedSearch {
    private int osm_id;
    private Location location;

    public SpeedSearch(int osm_id) {
        this.osm_id = osm_id;
        location = new Location("");
    }

    public int getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(int osm_id) {
        this.osm_id = osm_id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
