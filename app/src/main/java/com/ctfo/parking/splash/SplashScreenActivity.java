package com.ctfo.parking.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ctfo.parking.MainTabActivity;
import com.ctfo.parking.R;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1000);
                    startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
//                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
