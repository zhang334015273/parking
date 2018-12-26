package com.ctfo.parking.util;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.ctfo.parking.ParkingApplication;

import org.json.JSONObject;

/**
 * volley 工具类
 */
public class VolleyUtil {


    public static void doPostJson(int method, String url, JSONObject jsonRequest,
                                  Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
//        JsonObjectRequest request = new JsonObjectRequest(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
//                errorListener);
    }

    /**
     * 添加请求，交给volly进行处理
     */
    public static void doRequest(Request request){
        ParkingApplication.getQueues().add(request);
    }

}
