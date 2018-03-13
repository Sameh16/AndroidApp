package com.example.sameh.sensordatatest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by sameh on 3/12/2018.
 */

public class SingleTon {
    private static SingleTon singleTon;
    private static Context context;
    private RequestQueue requestQueue;

    private SingleTon(Context context)
    {
        this.context = context;
        requestQueue =getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        return requestQueue;
    }

    public  static synchronized SingleTon getInstance(Context context)
    {
        if(singleTon == null)
            singleTon = new SingleTon(context);
        return singleTon;
    }


    public<T> void addToRequestQueue(Request<T> request)
    {
        getRequestQueue().add(request);
    }

}
