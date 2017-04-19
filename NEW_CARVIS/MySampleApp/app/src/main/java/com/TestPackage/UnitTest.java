package com.TestPackage;//package com.TestPackage;
//
//import android.content.Context;
//import android.content.Intent;
//import android.location.Location;
//import android.os.IBinder;
//import android.test.mock.MockContext;
//import android.util.Log;
//
//import com.TestPackage.MockRequest;
//import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
//import com.carvis.Journey;
//import com.carvis.MyLocationService;
//import com.carvis.TemporarySpeedCamera;
//import com.carvis.TrackSpeedActivity;
//import com.carvis.UserStat;
//import com.carvis.VolleyService;
//
//import org.joda.time.DateTime;
//import org.junit.Test;
//
////import static com.facebook.FacebookSdk.getApplicationContext;
//import java.util.concurrent.TimeoutException;
//
//import static com.carvis.TemporarySpeedCamera.temporarySpeedCameras;
//import static junit.framework.Assert.assertEquals;
//import static org.hamcrest.CoreMatchers.any;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
import android.content.Context;
import android.location.Location;
import android.test.mock.MockContext;

import com.carvis.JourneyInfo;
import com.carvis.TemporarySpeedCamera;
import com.carvis.UserStat;
import com.carvis.VolleyService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

//
///**
// * Created by Seamus on 18/02/2017.
// */
//
//
public class UnitTest {
//
    Context mContext;
//    CognitoUserPoolsSignInProvider provider;
////    @Test
////    public void testQuerySpeed(){
//////        com.carvis.TrackSpeedActivity track = new TrackSpeedActivity();
//////        Journey j = new Journey();
//////        track.getSpeedFromLambda("53.3509124","-6.47792");
//////        System.out.println(j.getCurrentSpeed());
//////        assertThat(j.getCurrentSpeed(), is("50"));
////
////    }
//
//    @Test
//    public void testDayOfWeekMethod(){
//        UserStat userStat = new UserStat();
//        DateTime dateTime = new DateTime();
//
//       //assertThat(userStat.getDayOfWeek(dateTime).equals("Friday"),is(true));
//    }
//


    @Test
    public void testOverSpeedDay() {
        UserStat userStat = new UserStat();
        DateTime dateTime = new DateTime();
        //today = Wednesday
        userStat.addOverSpeedDate(dateTime);
        //+ + 5 days = Monday
        userStat.addOverSpeedDate(dateTime.plusDays(5));
        //+7 days = Wednesday
        userStat.addOverSpeedDate(dateTime.plusDays(7));
        //+6 days = Tuesday
        userStat.addOverSpeedDate(dateTime.plusDays(6));

        assertThat(userStat.getOverSpeedDay().equals("Wednesday"), is(true));
    }

    @Test
    public void testAverageJourneyTime() {
        UserStat userStat = new UserStat();
        ArrayList<JourneyInfo> journeyInfos = new ArrayList<>();
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startTime = dateStringFormat.parseDateTime("2017-03-16 17:08:26");
        DateTime endTime = dateStringFormat.parseDateTime("2017-03-16 17:31:12");
        DateTime journey2startTime = dateStringFormat.parseDateTime("2017-04-01 14:11:11");
        DateTime journey2endTime = dateStringFormat.parseDateTime("2017-04-01 14:57:11");

        double startLat = 53.2921336, startLong = -6.3641862, endLat = 53.3513313, endLong = -6.380373;
        double start2Lat = 53.212151, start2Long = -6.4535435, end2Lat = 53.323323, end2Long = -6.9382493;

        //1st journey duration 22 mins
        journeyInfos.add(new JourneyInfo(startTime, endTime, startLat, startLong, endLat, endLong));
        //2nd journey duration 34 mins
        journeyInfos.add(new JourneyInfo(journey2startTime, journey2endTime,
                start2Lat, start2Long, end2Lat, end2Long));
        userStat.setJourneyInfos(journeyInfos);
        //(22 + 34) / 2 = 34
        assertEquals(34.0f, userStat.getAverageJourneyTime(), 0.1);
    }

    @Test
    public void testOverSpeedPercentage() {
        UserStat userStat = new UserStat();
        ArrayList<JourneyInfo> journeyInfos = new ArrayList<>();
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startTime = dateStringFormat.parseDateTime("2017-03-16 17:08:26");
        DateTime endTime = dateStringFormat.parseDateTime("2017-03-16 17:31:12");
        DateTime journey2startTime = dateStringFormat.parseDateTime("2017-04-01 14:11:11");
        DateTime journey2endTime = dateStringFormat.parseDateTime("2017-04-01 14:57:11");

        double startLat = 53.2921336, startLong = -6.3641862, endLat = 53.3513313, endLong = -6.380373;
        double start2Lat = 53.212151, start2Long = -6.4535435, end2Lat = 53.323323, end2Long = -6.9382493;

        //1st journey duration 22 mins
        journeyInfos.add(new JourneyInfo(startTime, endTime, startLat, startLong, endLat, endLong));
        //2nd journey duration 34 mins
        journeyInfos.add(new JourneyInfo(journey2startTime, journey2endTime,
                start2Lat, start2Long, end2Lat, end2Long));
        userStat.setJourneyInfos(journeyInfos);

        userStat.setJourneysWithOverSpeed(1);

        System.out.println(userStat.getJourneyInfos().size());
        assertEquals("50.0%", userStat.getOverSpeedPercentage());
    }

    @Test
    public void testJourneyDuration() {
        DateTimeFormatter dateStringFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startTime = dateStringFormat.parseDateTime("2017-04-01 11:12:00");
        DateTime endTime = dateStringFormat.parseDateTime("2017-04-01 18:57:00");

        UserStat.getJourneyDuration(startTime, endTime);
        assertEquals(UserStat.getJourneyDuration(startTime, endTime), 465, 0.1);
    }

    @Test
    public void testJSONMessageBuilder(){
        String messageBody =
                VolleyService.createMessageJSON("Palmerstown","53.3554843", "-6.3716409", "Dublin");
        String testReponse = "{\"notification\":{\"title\":\"Bad Traffic Reported\",\"body\":\"Palmerstown\"}," +
                "\"data\":{\"address\":\"Palmerstown\",\"latitude\":\"53.3554843\",\"title\":\"Bad Traffic Reported\"," +
                "\"longitude\":\"-6.3716409\"},\"to\":\"/topics/Dublin\"}";
        assertEquals(messageBody, testReponse);
    }

//    @Test
//    public void testCameraDistance(){
//    //53.297698, -6.370387
//        TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(53.293159, -6.369055, "2017-03-01"));
//        TemporarySpeedCamera.addTemporaryCamera(new TemporarySpeedCamera(54.293159, -7.369055, "2017-03-02"));
//
//
//        mContext = new MockContext();
//        MockLocation mockLocation = new MockLocation("", mContext);
//        mockLocation.pushLocation(53.297698, -6.370387);
//
//        Location testLocation = new Location("test");
//        testLocation.setLatitude(53.297698);
//        testLocation.setLongitude(-6.370387);
//        //TemporarySpeedCamera.checkCameraDistance(testLocation);
//        Location location = (Location)mockLocation;
//        System.out.println(TemporarySpeedCamera.checkCameraDistance(mockLocation));
//
//    }











//
//    @Test
//    public void testDaysOverSpeed(){
////        provider = new CognitoUserPoolsSignInProvider(context);
////        context = new MockContext();
////        VolleyService volleyService = new VolleyService(context);
////        volleyService.getDaysSinceLastOverSpeed(provider, context);
//    }
//
//
//    @Test
//    public void testCameraDistance(){
//        context = new MockContext();
//        TemporarySpeedCamera t = new TemporarySpeedCamera(53.763010, -6.718305, "");
//        TemporarySpeedCamera.addTemporaryCamera(t);
//        temporarySpeedCameras.add(t);
//
//        MockLocation mockLocation = new MockLocation("", context);
//        mockLocation.pushLocation(53.748379, -6.628763);
////        Location location = new Location("location");
////        location.setLatitude(53.748379);
////        location.setLongitude(-6.628763);
//        boolean testBool = true;
//
//        boolean cameraLocations = TemporarySpeedCamera.checkCameraDistance(mockLocation);
//        Log.wtf("cameraLocation", String.valueOf(cameraLocations));
//        System.out.println(String.valueOf(cameraLocations));
//    }
//}
}