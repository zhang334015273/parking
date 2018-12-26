package com.ctfo.parking;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * application 入口
 */
public class ParkingApplication extends Application {

    //http请求队列
    private static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        queues = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getQueues(){
        return queues;
    }


}
