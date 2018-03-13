package com.example.sameh.sensordatatest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {


    private boolean startlocation;
    private int count;

    class MyServiceBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    private IBinder mBinder=new MyServiceBinder();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startlocation = true;
        count=0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                    startGetLocation();
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startlocation = false;
    }

    public void stopGetLocation()
    {
        startlocation=false;
    }

    public void startGetLocation(){

        while (startlocation)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            Log.i("count",count+"");

        }
    }
}

