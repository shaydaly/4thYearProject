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
    private int numJourneys;
    private DateTime memberSince;
    private ArrayList<DateTime> overSpeedDates;
    private String roadAddress;
    int daysSinceOverSpeed;
    Map<String, Integer> mostCommon;


    HashMap<String, Double> monthlyKilom;

    public UserStat(String overSpeedRoad, ArrayList<JourneyInfo> journeyInfos, int journeysWithOverSpeed, int numJourneys, ArrayList<DateTime> overSpeedDates, DateTime memberSince, String roadAddress) {
        //this.username = username;
        this.overSpeedRoad = overSpeedRoad;
        this.journeyInfos = journeyInfos;
        this.journeysWithOverSpeed = journeysWithOverSpeed;
        this.numJourneys = numJourneys;
        this.overSpeedDates = overSpeedDates;
        this.memberSince = memberSince;
        this.roadAddress = roadAddress;
        monthlyKilom = new HashMap<>();
        mostCommon = new HashMap<>();
    }

    public HashMap<String, Double> getMonthlyKilom() {
        return monthlyKilom;
    }

    public UserStat() {
        overSpeedDates = new ArrayList<>();
    }

    public UserStat(String username){
        this.username = username;
        daysSinceOverSpeed = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDaysSinceOverSpeed(int daysSinceOverSpeed) {
        this.daysSinceOverSpeed = daysSinceOverSpeed;
    }

    public int getDaysSinceOverSpeed() {
        return daysSinceOverSpeed;
    }

    public DateTime getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(DateTime memberSince) {
        this.memberSince = memberSince;
    }

    //    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getOverSpeedRoad() {
        return overSpeedRoad;
    }

    public void setOverSpeedRoad(String overSpeedRoad) {
        this.overSpeedRoad = overSpeedRoad;
    }

    public ArrayList<JourneyInfo> getJourneyDates() {
        return journeyInfos;
    }

    public void setJourneyDates(ArrayList<JourneyInfo> journeyInfos) {
        this.journeyInfos = journeyInfos;
    }

    public int getJourneysWithOverSpeed() {
        return journeysWithOverSpeed;
    }

    public void setJourneysWithOverSpeed(int journeysWithOverSpeed) {
        this.journeysWithOverSpeed = journeysWithOverSpeed;
    }

    public int getNumJourneys() {
        return numJourneys;
    }

    public void setNumJourneys(int numJourneys) {
        this.numJourneys = numJourneys;
    }

    public ArrayList<DateTime> getOverSpeedDates() {
        return overSpeedDates;
    }

    public void setOverSpeedDates(ArrayList<DateTime> overSpeedDates) {
        this.overSpeedDates = overSpeedDates;
    }

    public void addOverSpeedDate(DateTime date){
        overSpeedDates.add(date);
    }

    public int getNumOverSpeed(){
        return journeysWithOverSpeed;
    }

    public void addJourneyDate(JourneyInfo journeyDate){
        journeyInfos.add(journeyDate);
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

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getDayOfWeek(DateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern( "EEEE" ).withLocale( java.util.Locale.ENGLISH );
        String dayOfWeekName = formatter.print( dateTime );
        return dayOfWeekName;
    }


    public String getOverSpeedDay(){
        if(overSpeedDates.size()!= 0) {
            ArrayList<String> days = new ArrayList<>();
            for (DateTime dateTime : overSpeedDates) {
                days.add(getDayOfWeek(dateTime));
            }
            String maxWord = null;
            Integer maxCount = -1;
            for (String day : days) {
                if (!mostCommon.containsKey(day)) {
                    mostCommon.put(day, 1);
                }
                int count = mostCommon.get(day) + 1;
                if (count > maxCount) {
                    maxWord = day;
                    maxCount = count;
                }
                mostCommon.put(day, count);
            }
            return maxWord;
        }
        else{
            return "NA";
        }
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
                System.out.println(total+" total");
                double value  = getRoundedValue((total / journeyInfos.size()), 5);
                System.out.println(value+" value");
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
                total += getDistance(start, end);
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
        Log.wtf("map size", String.valueOf(monthlyKilom.size()));
        Iterator it = monthlyKilom.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
        //return getRoundedValue(total, 2);

        if(total!= 0){
            return getRoundedValue(total,2);
        }



        return total;
    }

    public void getMonthlyKilomTravelled() {
        double total = 0;

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
                String month = journeyInfo.getStartTime().toString("MMM");

                if (!monthlyKilom.containsKey(month)) {
                    monthlyKilom.put(month, monthlyTotal);
                } else {
                    monthlyKilom.put(month, getRoundedValue(monthlyKilom.get(month),2) + monthlyTotal);
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

    public double getDistance(Location start, Location end){
        return (start.distanceTo(end)) / 1000;
    }



    public double  getJourneyDuration(DateTime start, DateTime end){

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

    public  double getRoundedValue(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}


 class JourneyInfo implements  Serializable{
     DateTime startTime;
     DateTime endTime;
     double startLatitude, startLongitude, endLatitude, endLongitude;


     JourneyInfo(DateTime startTime, DateTime endTime, double startLatitude, double startLongitude, double endLatitude, double endLongitude){
         this.startTime = startTime;
         this.endTime = endTime;
         this.startLatitude = startLatitude;
         this.startLongitude = startLongitude;
         this.endLatitude  = endLatitude;
         this.endLongitude = endLongitude;
     }

     JourneyInfo(DateTime startTime, DateTime endTime){
         this.startTime = startTime;
         this.endTime = endTime;
     }

     public DateTime getStartTime() {
         return startTime;
     }

     public void setStartTime(DateTime startTime) {
         this.startTime = startTime;
     }

     public DateTime getEndTime() {
         return endTime;
     }

     public void setEndTime(DateTime endTime) {
         this.endTime = endTime;
     }

     public double getStartLatitude() {
         return startLatitude;
     }

     public void setStartLatitude(double startLatitude) {
         this.startLatitude = startLatitude;
     }

     public double getStartLongitude() {
         return startLongitude;
     }

     public void setStartLongitude(double startLongitude) {
         this.startLongitude = startLongitude;
     }

     public double getEndLatitude() {
         return endLatitude;
     }

     public void setEndLatitude(double endLatitude) {
         this.endLatitude = endLatitude;
     }

     public double getEndLongitude() {
         return endLongitude;
     }

     public void setEndLongitude(double endLongitude) {
         this.endLongitude = endLongitude;
     }

//     public int describeContents() {
//         return 0;
//     }
//
//     public void writeToParcel(Parcel out, int flags) {
//         out.wr;
//         out.writeValue(startLocation);
//         out.writeValue(endLocation);
//     }
//
//     public static final Parcelable.Creator<JourneyInfo> CREATOR
//             = new Parcelable.Creator<JourneyInfo>() {
//         public JourneyInfo createFromParcel(Parcel in) {
//             return new JourneyInfo(in);
//         }
//
//         public JourneyInfo[] newArray(int size) {
//             return new JourneyInfo[size];
//         }
//     };
//
//     private JourneyInfo(Parcel in) {
////         startLocation = in.readBundle();
////     }
//     }
 }




