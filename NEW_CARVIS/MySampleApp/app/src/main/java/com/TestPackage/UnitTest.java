//package com.TestPackage;
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
//
///**
// * Created by Seamus on 18/02/2017.
// */
//
//
//public class UnitTest {
//
//    Context context;
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
//    @Test
//    public void testOverSpeedDay(){
//        UserStat userStat = new UserStat();
//        DateTime dateTime = new DateTime();
//
//        userStat.addOverSpeedDate(dateTime);
//        userStat.addOverSpeedDate(dateTime.plusDays(5));
//        userStat.addOverSpeedDate(dateTime.plusDays(7));
//        userStat.addOverSpeedDate(dateTime.plusDays(6));
//
//        //assertThat(userStat.getOverSpeedDay().equals("Friday"),is(true));
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
