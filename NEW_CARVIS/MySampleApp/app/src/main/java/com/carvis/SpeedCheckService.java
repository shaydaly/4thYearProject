//package com.carvis;
////
//import android.app.Service;
//import android.content.Intent;
//import android.location.Location;
//import android.os.Binder;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.os.SystemClock;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
////
//public class SpeedCheckService extends Service {
//
//    private final IBinder speedBinder =  new SpeedLocalBinder();
//
//
//    public SpeedCheckService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return speedBinder;
//    }
//
//    public void wait10Seconds() {
//        System.out.println("wait ten seconds called");
////        long futureTime = System.currentTimeMillis() + 10000;
////        while (System.currentTimeMillis() < futureTime) {
////            synchronized (this) {
////                try {
////                    System.out.println(System.currentTimeMillis());
////                    wait(futureTime - System.currentTimeMillis());
////                } catch (Exception e) {
////
////                }
////            }
////        }
//    }
//
//    @Override
//    public void onLocationChanged(final Location location) {
//
//
//    }
//
//    private static void sendMessageToActivity(String msg) {
////        Intent intent = new Intent("GPSLocationUpdates");
////        // You can also include some extra data.
////        intent.putExtra("Status", msg);
//////        Bundle b = new Bundle();
//////        b.putParcelable("Location", l);
//////        intent.putExtra("Location", b);
////        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//    }
//
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    public class SpeedLocalBinder extends Binder{
//        SpeedCheckService getService(){
//            return SpeedCheckService.this;
//        }
//    }
//}
