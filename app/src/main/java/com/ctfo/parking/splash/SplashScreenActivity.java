package com.ctfo.parking.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ctfo.parking.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1000);
                    startActivity(new Intent(getApplicationContext(), NaviTabActivity.class));
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
