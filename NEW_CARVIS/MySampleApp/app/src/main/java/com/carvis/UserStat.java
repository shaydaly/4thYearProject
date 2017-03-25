package com.carvis;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Seamus on 24/03/2017.
 */

public class UserStat implements Serializable {
   // private String username;
    private String overSpeedRoad;
    private ArrayList<JourneyDate> journeyDates;
    int journeysWithOverSpeed;
    int numJourneys;
    private ArrayList<DateTime> overSpeedDates;


    public UserStat(String overSpeedRoad, ArrayList<JourneyDate> journeyDates, int journeysWithOverSpeed, int numJourneys, ArrayList<DateTime> overSpeedDates) {
        //this.username = username;
        this.overSpeedRoad = overSpeedRoad;
        this.journeyDates = journeyDates;
        this.journeysWithOverSpeed = journeysWithOverSpeed;
        this.numJourneys = numJourneys;
        this.overSpeedDates = overSpeedDates;
    }

    public UserStat() {
        overSpeedDates = new ArrayList<>();
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

    public ArrayList<JourneyDate> getJourneyDates() {
        return journeyDates;
    }

    public void setJourneyDates(ArrayList<JourneyDate> journeyDates) {
        this.journeyDates = journeyDates;
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

    public void addJourneyDate(JourneyDate journeyDate){
        journeyDates.add(journeyDate);
    }

    public String getDayOfWeek(DateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern( "EEEE" ).withLocale( java.util.Locale.ENGLISH );
        String dayOfWeekName = formatter.print( dateTime );
        return dayOfWeekName;
    }


    public String getOverSpeedDay(){
        ArrayList<String> days = new ArrayList<>();
        for(DateTime dateTime : overSpeedDates){
            days.add(getDayOfWeek(dateTime));
        }
        Map<String, Integer> mostCommon = new HashMap<>();
        String maxWord = null;
        Integer maxCount = -1;
        for (String day : days) {
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
    }

    public String getOverSpeedPercentage(){
        return String.valueOf((overSpeedDates.size() / journeyDates.size()) * 100)+"%";
    }

    public double getAverageJourneyTime(){
        double total = 0;
        for(JourneyDate journeyDate : journeyDates){
            total += getJourneyDuration(journeyDate.getStartTime(), journeyDate.getEndTime());
        }

        return total / journeyDates.size();
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
}


 class JourneyDate implements Serializable{
     DateTime startTime;
     DateTime endTime;

     JourneyDate(DateTime startTime, DateTime endTime){
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
 }




