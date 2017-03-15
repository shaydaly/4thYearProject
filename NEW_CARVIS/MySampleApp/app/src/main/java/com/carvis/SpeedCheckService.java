package com.carvis;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SpeedCheckService extends Service {

    private final IBinder speedBinder =  new speedLocalBinder();

    public SpeedCheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return speedBinder;
    }

    public void getSpeedLimit(){}

    public class speedLocalBinder extends Binder{
        SpeedCheckService getService(){
            return SpeedCheckService.this;
        }
    }
}
