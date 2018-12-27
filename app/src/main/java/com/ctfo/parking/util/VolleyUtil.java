package com.ctfo.parking.util;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ctfo.parking.ParkingApplication;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * volley 工具类
 */
public class VolleyUtil {


    /**
     * get 带参数请求
     * @param url
     * @param param 参数
     * @param listener
     * @param errorListener
     */
    public static void doGet(String url,Map<String,String> param, Response.Listener<String> listener, Response.ErrorListener errorListener){
        //拼接参数
        StringBuffer sb = new StringBuffer(url);
        if(param!=null && !param.isEmpty()){
            Set<String> keys = param.keySet();
            for (int i=0; i<keys.size();i++){
                if (i ==0 && url.indexOf("?") == -1){
                    //没有参数的情况下，拼接？

                }else{

                }
            }
        }
        doGet(url,listener,errorListener);
    }

    /**
     *   get请求
     * @param url 路径
     * @param listener 回调监听
     * @param errorListener 异常监听
     */
    public static void doGet(String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        StringRequest request = new StringRequest(url,listener,errorListener);
        doRequest(request);
    }

    /**
     * 添加请求，交给volly进行处理
     */
    public static void doRequest(Request request){
        ParkingApplication.getQueues().add(request);
    }

}
