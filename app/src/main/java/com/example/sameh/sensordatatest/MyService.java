package com.example.sameh.sensordatatest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyService extends Service {


    private boolean startlocation;
    private int count;

    class MyServiceBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }

    private IBinder mBinder=new MyServiceBinder();
    GoogleMap mMap;
    double rad;
    SharedPreferences sharedPreferences;
    public MyService(GoogleMap googleMap,double range) {
        mMap =googleMap;
        rad = range;
        Log.i("String",rad+"");

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
            getNearestTrucks(rad);

        }
    }

    public void getNearestTrucks(double range) {
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String driverId = sharedPreferences.getString("driverId","");
        String url = "http://seelsapp.herokuapp.com/getNearLocation/"+driverId+"/"+range+"";
        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Log.i("tag",response.length()+" res");
                    LatLng temp;
                    for (int i=0;i<response.length();i++)
                    {
                        JSONObject jsonObject = response.getJSONObject(i);
                        double lat = jsonObject.getDouble("lat");
                        double lon = jsonObject.getDouble("lon");
                        temp = new LatLng(lat,lon);
                        MarkerOptions markerOptions =new MarkerOptions();
                        markerOptions.position(temp);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(markerOptions);
                        Log.i("location",lat+"");
                        //locations.add(temp);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);

    }
}

