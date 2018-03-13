package com.example.sameh.sensordatatest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView view;
    private static  final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    private Intent intent;
    private Location prev_location;
    private Intent serviceIntent;
    private Button start;
    private Button stop;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prev_location = new Location("locationA");
        prev_location.setLatitude(0);
        prev_location.setLongitude(0);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        view = findViewById(R.id.view);
        start = findViewById(R.id.startService);
        stop = findViewById(R.id.stopService);

        if(!runtime_permissions())
            enable_buttons();

        FirebaseMessaging.getInstance().subscribeToTopic("topicA");

        //vollyGetLocation();
        //volleySetLocation(30.15,30.15);

    }




    private void enable_buttons() {

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("start","start");
                Intent i =new Intent(getApplicationContext(),GPSservice.class);
                startService(i);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("stop","stop");
                Intent i = new Intent(getApplicationContext(),GPSservice.class);
                stopService(i);

            }
        });

    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) return true;

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gyroscope) {
            intent = new Intent(this,Gyroscope.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_accumulator) {

        } else if (id == R.id.nav_Location) {
            intent = new Intent(this,LocationActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Location vollyGetLocation(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);

       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.1.7:8080/getCurrentLocation", new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
                view.setText(response.toString());
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
                view.setText("Error message  " +error.getMessage() );
           }
       });

       requestQueue.add(jsonObjectRequest);
        return null;
    }

    public void volleySetLocation(Location location, Location prev_location, final Context context)
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
        String url = "https://seels-application.herokuapp.com/"+lat+"/"+lon+"/"+speed+"/saveLocation";
        StringRequest request  = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context,"Send!",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(context,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void getLocation() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION);
        }
        else {
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            if (location != null)
            {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
               /* Lat.setText(""+lat);
                Long.setText(" "+lon);*/
               view.setText(lat+"  ");
               Context context = this;
               Toast.makeText(this,lon+"",Toast.LENGTH_SHORT).show();
                volleySetLocation(location,prev_location,context);
            }
            else{
               /* Lat.setText("Falied to get location");
                Long.setText("");*/
               Toast.makeText(this,"Error Connection",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    view.append("\n" +intent.getExtras().get("coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    private String app_server_url = "http://192.168.1.5:80/app/insert.php";
    public void sendToken(View v)
    {
        SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        view.setText(token);
        /*
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Hello",token);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error",error.getMessage());
            }
        }

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> par = new HashMap<>();
                par.put("fcm_token",token);
                return par;
            }
        };
        SingleTon.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
        */
    }

}
