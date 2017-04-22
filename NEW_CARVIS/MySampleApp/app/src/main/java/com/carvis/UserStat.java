package com.carvis;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.apache.commons.lang3.time.DateUtils.round;

/**
 * Created by Seamus on 24/03/2017.
 */

public class UserStat implements Serializable {
    private String username;
    private String overSpeedRoad;
    private ArrayList<JourneyInfo> journeyInfos;
    private int journeysWithOverSpeed;
    private ArrayList<DateTime> overSpeedDates;
    private String roadAddress;
    private  int numOverSpeeds;
    private String mostOverSpedDay;
    private Map<String, Integer> mostCommon;
    private HashMap<String , Integer>monthlyJourneys;

    HashMap<String, Double> monthlyKilom;

    public UserStat(String overSpeedRoad, ArrayList<JourneyInfo> journeyInfos, int journeysWithOverSpeed, ArrayList<DateTime> overSpeedDates,  String roadAddress, int numOverSpeeds) {
        this.overSpeedRoad = overSpeedRoad;
        this.journeyInfos = journeyInfos;
        this.journeysWithOverSpeed = journeysWithOverSpeed;
        this.overSpeedDates = overSpeedDates;
        this.roadAddress = roadAddress;
        this.numOverSpeeds =  numOverSpeeds;
        monthlyKilom = new HashMap<>();
        mostOverSpedDay = getOverSpeedDay();
        monthlyJourneys = new HashMap<>();
    }

    public String getMostOverSpedDay() {
        return mostOverSpedDay;
    }

    public HashMap<String, Double> getMonthlyKilom() {
        return monthlyKilom;
    }

    public HashMap<String, Integer> getMonthlyJourneysHashMap() {
        return monthlyJourneys;
    }

    public UserStat() {
        overSpeedDates = new ArrayList<>();
        mostCommon = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double  overSpeedPerKilom(){
        double total = 0;
        try {
            total  =  getKilomsTravelled() / numOverSpeeds;
        }
        catch(Exception e){

        }
        return getRoundedValue(total, 2);
    }

    public void setJourneysWithOverSpeed(int journeysWithOverSpeed) {
        this.journeysWithOverSpeed = journeysWithOverSpeed;
    }

    public int getNumJourneys() {
        return journeyInfos.size();
    }


    public void addOverSpeedDate(DateTime date){
        overSpeedDates.add(date);
    }

    public int getNumOverSpeed(){
        return journeysWithOverSpeed;
    }

    public ArrayList<JourneyInfo> getJourneyInfos() {
        return journeyInfos;
    }

    public void setJourneyInfos(ArrayList<JourneyInfo> journeyInfos) {
        this.journeyInfos = journeyInfos;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public String getDayOfWeek(DateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern( "EEEE" ).withLocale( java.util.Locale.ENGLISH );
        String dayOfWeekName = formatter.print( dateTime );
        return dayOfWeekName;
    }


    public String getOverSpeedDay(){
        if(overSpeedDates!=null) {
            if (overSpeedDates.size() != 0) {
                ArrayList<String> days = new ArrayList<>();
                for (DateTime dateTime : overSpeedDates) {
                    days.add(getDayOfWeek(dateTime));
                }
                String maxWord = null;
                Integer maxCount = -1;
                for (String day : days) {
                    if(mostCommon ==null){
                        mostCommon = new HashMap<>();
                    }
                    if (!mostCommon.containsKey(day)) {
                        mostCommon.put(day, 0);
                    }

                    int count = mostCommon.get(day) + 1;
                    if (count > maxCount) {
                        maxWord = day;
                        maxCount = count;
                    }
                    mostCommon.put(day, count);
                }
                return maxWord;
            } else {
                return "NA";
            }
        }
        return "NA";
    }

    public Map<String, Integer> getMostCommon() {
        return mostCommon;
    }

    public String getOverSpeedPercentage(){
        if(journeyInfos.size()!=0) {
            double percentage = ((double) journeysWithOverSpeed / journeyInfos.size()) * 100;
            return String.valueOf(getRoundedValue(percentage,2))+"%";
        }
        return String.valueOf(0.0)+"%";

    }
    public double getAverageJourneyTime() {
            double total =0.0;
            if (journeyInfos.size() != 0) {
                for (JourneyInfo journeyDate : journeyInfos) {
                    total += getJourneyDuration(journeyDate.getStartTime(), journeyDate.getEndTime());
                }
                double value  = getRoundedValue((total / journeyInfos.size()), 5);
                total = value;
            }
            else {
                total =  0.0;
            }

        if(total!= 0){
            return getRoundedValue(total,2);
        }
        return total;
    }

    public double getKilomsTravelled(){
        double total = 0;
        Location start = new Location("start");
        Location end =new Location("end");
        if(journeyInfos.size()!= 0) {
            for (JourneyInfo journeyInfo : journeyInfos) {
                double monthlyTotal = 0;
                start.setLatitude(journeyInfo.getStartLatitude());
                start.setLongitude(journeyInfo.getStartLongitude());
                end.setLatitude(journeyInfo.getEndLatitude());
                end.setLongitude(journeyInfo.getEndLongitude());
                total += getRoundedValue(getDistance(start, end),2);
//                monthlyTotal = getDistance(start, end);
//                String month = journeyInfo.getStartTime().toString("MMM");
//
//                if(!monthlyKilom.containsKey(month)){
//                    monthlyKilom.put(month, monthlyTotal);
//                }
//                else{
//                    monthlyKilom.put(month, monthlyKilom.get(month) +monthlyTotal);
//                }

            }
        }
        else{
            total = 0;
        }
//        Log.wtf("map size", String.valueOf(monthlyKilom.size()));
//        Iterator it = monthlyKilom.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
//            //it.remove(); // avoids a ConcurrentModificationException
//        }
        //return getRoundedValue(total, 2);

        if(total!= 0){
            return getRoundedValue(total,2);
        }



        return total;
    }

    public void getMonthlyKilomTravelled() {
        Location start = new Location("start");
        Location end = new Location("end");
        if (journeyInfos.size() != 0) {
            for (JourneyInfo journeyInfo : journeyInfos) {
                double monthlyTotal = 0;
                start.setLatitude(journeyInfo.getStartLatitude());
                start.setLongitude(journeyInfo.getStartLongitude());
                end.setLatitude(journeyInfo.getEndLatitude());
                end.setLongitude(journeyInfo.getEndLongitude());
                monthlyTotal = getDistance(start, end);
                monthlyTotal = getRoundedValue(monthlyTotal, 2);
                String month = journeyInfo.getStartTime().toString("MMMM");

                Log.wtf("monthly total", String.valueOf(monthlyTotal));
                if (!monthlyKilom.containsKey(month)) {
                    monthlyKilom.put(month, monthlyTotal);
                } else {
                    monthlyKilom.put(month, getRoundedValue(monthlyKilom.get(month),2) + monthlyTotal);
                }

            }
        }
    }

    public void getMonthlyJourneys() {
        if (journeyInfos.size() != 0) {
            for (JourneyInfo journeyInfo : journeyInfos) {
                double monthlyTotal = 0;
                String month = journeyInfo.getStartTime().toString("MMMM");

                if (!monthlyJourneys.containsKey(month)) {
                    monthlyJourneys.put(month, 1);
                } else {
                    monthlyJourneys.put(month, monthlyJourneys.get(month) + 1);
                }

            }
        }
    }


    public double getAverageJourneyKiloms(){
        if(journeyInfos.size()!= 0) {
            return getRoundedValue(getKilomsTravelled() / journeyInfos.size(), 2);
        }
        return 0.0;
    }

    public static double getDistance(Location start, Location end){
        return (start.distanceTo(end)) / 1000;
    }

    public static double  getJourneyDuration(DateTime start, DateTime end){

        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        if(start!= null&& end!=null) {
            System.out.println(); // Sat Jan 02 00:00:00 GMT 2010
            org.joda.time.Duration duration = new org.joda.time.Duration(start, end);
            return duration.getStandardMinutes();
        }
        else{
            return 0;
        }
    }

    public  static double getRoundedValue(double value, int places) {
        double returnValue = 0.0;
        try {
            if (places < 0) throw new IllegalArgumentException();

            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            returnValue =  bd.doubleValue();
        }
        catch (Exception e){

        }
        return returnValue;
    }
}





