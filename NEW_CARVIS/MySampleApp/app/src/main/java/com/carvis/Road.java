package com.carvis;

import android.location.Location;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Seamus on 06/03/2017.
 */

public class Road {

    int osm_id;
    int speedLimit;
    private ArrayList<RoadRecord> roadRecords;

    public Road(int osm_id, ArrayList<RoadRecord> roadRecords) {
        this.osm_id = osm_id;
        this.roadRecords= roadRecords;
    }

    public Road(int osm_id){
        this.osm_id = osm_id;
    }
    public int getOsm_id() {
        return osm_id;
    }

    public void setOsm_id(int osm_id) {
        this.osm_id = osm_id;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public ArrayList<RoadRecord> getRoadcoOrdinates() {
        return roadRecords;
    }

    public void setRoadcoOrdinates(ArrayList<RoadRecord> roadRecords) {
        this.roadRecords = roadRecords;
    }

    public void addToCoOrdinates(RoadRecord r){
        roadRecords.add(r);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Road))
            return false;
        if (obj == this)
            return true;

        Road rhs = (Road) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
//                        append(startLatitude, rhs.startLatitude).
//                        append(startLongitude, rhs.startLongitude).
//                        append(endLatitude, rhs.endLatitude).
//                        append(endLongitude, rhs.endLongitude).
//                        isEquals();
                            append(osm_id, rhs.getOsm_id()).isEquals();
    }
}
