package com.carvis;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import com.google.gson.Gson;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


/**
 * Created by Seamus on 08/02/2017.
 */

public class JourneyFragment{


    private Date time;
    private String latitude, longitude, speedLimit;
    double speed;

    JourneyFragment(String lat, String lon,  double sp, String sl,Date d){
 //       super(lat,lon,speed,speedLimit);
        latitude = lat;
        longitude = lon;
        speed = sp;
        speedLimit = sl;
        time = d;
    }

    public double getCurrentSpeed(){
        return speed;
    }
    public void setTime(Date time) {
        this.time = time;
    }
//

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public void setSpeedLimit(String speedLimit) {
        this.speedLimit = speedLimit;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Date getTime() {
        return time;
    }
//

    public String getLatitude() {
        return latitude;
    }


    public String getSpeedLimit() {
        return speedLimit;
    }


    public String getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    JourneyFragment(){

    }

//    public void setTime(Date time) {
//        this.time = time;
//    }
//
//    public Date getTime() {
//        return time;
//    }



    public static void AddJourneyFragments(Context c, String username, List<JourneyFragment> journies){
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        Date dNow = new Date( );



        try {
//
//
//            List<JourneyFragment> jo = new ArrayList<>();
////
//            JourneyFragment j = new JourneyFragment("55.55", "55", 55, "70", "hel");
//            JourneyFragment j2 = new JourneyFragment("55.55", "55", 55,"880", "hel");
//
//
//            //Journey jour = new Journey("55.55","88",50.0,"56",null,dNow);
//
//
//            jo.add((j));
//            jo.add(j2);
//
//            List<TestObject> strings = new ArrayList<>();
//            strings.add(new TestObject("Seamus","Daly",25));
//            strings.add(new TestObject("Dean","Flood",25));
//            strings.add(new TestObject("Aar","Fogo",25));


            Gson gson = new Gson();
            String json = gson.toJson(journies);
            System.out.println(json);


//
//
//
//
//            final StringWriter sw =new StringWriter();
//            final ObjectMapper mapper = new ObjectMapper();
//




        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }


    }


}
