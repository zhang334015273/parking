package com.ctfo.parking.splash;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ctfo.parking.R;
import com.ctfo.parking.util.VolleyUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class NaviTabActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navi_tab_activity);
        testVolley();

    }

    private void testVolley(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://appback.ctfoparking.com/app/homePage?id=%E5%8C%97%E4%BA%AC%E5%B8%82", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("state");
                            if (status) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.toString();
                        //Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        VolleyUtil.doRequest(request);
    }
}
