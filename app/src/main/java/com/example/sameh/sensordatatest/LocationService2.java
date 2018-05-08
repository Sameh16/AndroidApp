package com.example.sameh.sensordatatest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LocationService2 extends Service {
    public LocationService2() {
    }

    private boolean startlocation;
    private int count;
    private LocationManager locationManager;
    private SharedPreferences sharedPreferences;
    private Location prev_location;

    class MyServiceBinder extends Binder {
        public LocationService2 getService(){
            return LocationService2.this;
        }
    }

    private IBinder mBinder=new MyServiceBinder();


    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startlocation = true;
        count=0;
        prev_location = new Location("");
        prev_location.setLatitude(0.0);
        prev_location.setLongitude(0.0);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        stopGetLocation();
        startlocation = false;
    }

    public void stopGetLocation()
    {
        startlocation=false;
    }

    private void getLocation() {
            @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null)
            {
                Log.i("Service",location.getLongitude()+"");
                volleySetLocation(location);
            }
    }

    public void startGetLocation(){

        while (startlocation)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getLocation();
            Log.i("count",count+"");

        }
    }

    public void volleySetLocation(Location location)
    {

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double speed =0.0;
        if (prev_location.getLongitude()!=0) {
            double distance = location.distanceTo(prev_location);
            double diffTime = location.getTime() - prev_location.getTime();
            if (location.hasSpeed())
                speed = location.getSpeed();
            else
                speed = distance / diffTime;
        }
        //Toast.makeText(context,"distance = "+distance,Toast.LENGTH_SHORT).show();
        //Toast.makeText(context,"diffTime = "+diffTime,Toast.LENGTH_SHORT).show();
        prev_location.set(location);
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String driverId = sharedPreferences.getString("driverId","");
        String url = "https://seelsapp.herokuapp.com/"+lat+"/"+lon+"/"+speed+"/"+driverId+"/saveLocation";
        StringRequest request  = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("location","send!!");// Toast.makeText(context,"Send!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(context,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

}


