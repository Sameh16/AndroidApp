package com.example.sameh.sensordatatest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sameh on 3/12/2018.
 */

public class FcmInstanceIdService extends FirebaseInstanceIdService {

    private String token;
    private String lastToken;
    private String driverId;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // get token from firebase when it chanaged
        token = FirebaseInstanceId.getInstance().getToken();
        // save token in sharedPreferences
        SharedPreferences sharedPreferences =  getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String tokenId= getString(R.string.FCM_TOKEN);
        if (sharedPreferences.contains(tokenId))
            lastToken = sharedPreferences.getString(tokenId,"");
        else
            lastToken="";
        editor.putString(getString(R.string.FCM_TOKEN),token);
        editor.commit();
        if (sharedPreferences.contains("driverId"))
            driverId= sharedPreferences.getString("driverId","");
        else
            driverId="";
        // call service that update token
        //sendToken();


    }

    public void sendToken()
    {
        String app_server_url = "http://192.168.1.5:80/app/insert.php";
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
                par.put("token",token);
                if (driverId.equals(""))
                    par.put("pervToken",lastToken);
                par.put("id",driverId);
                return par;
            }
        };
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }
}
