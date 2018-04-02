package com.example.sameh.sensordatatest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GPSservice extends Service {

    LocationListener locationListener;
    LocationManager locationManager;
    Location prev_location;
    SharedPreferences sharedPreferences;
    public GPSservice() {
        prev_location = new Location("");
        prev_location.setLongitude(0);
        prev_location.setLatitude(0);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ss","onCreate");
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("getLocation",location.getLongitude()+"  "+prev_location.getLongitude());
                volleySetLocation(location, prev_location);
                Log.i("getLocation",location.getLongitude()+"  "+prev_location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Log.i("ss","how not send ??");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

  /*  @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ss","start");
        return START_STICKY_COMPATIBILITY;
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(locationListener);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void volleySetLocation(Location location, Location prev_location)
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
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF),Context.MODE_PRIVATE);
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
