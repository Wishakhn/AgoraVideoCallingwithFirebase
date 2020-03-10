package com.testapp.videocallingwithsnich;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.testapp.videocallingwithsnich.Activities.MainActivity;
import com.testapp.videocallingwithsnich.Activities.StartActivity;
import com.testapp.videocallingwithsnich.Helpers.PrefernceManager;

public class Splash extends AppCompatActivity {
    Handler handler;
    PrefernceManager prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        prefs = new PrefernceManager(Splash.this);
        prefs.initPrefernce();
        final Boolean isuserOnline = prefs.loadBooleanPrefernce(PrefernceManager.USER_ONLINE_KEY);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isuserOnline){
                    Intent tent = new Intent(Splash.this, MainActivity.class);
                    startActivity(tent);
                    finish();
                }
                else {
                    Intent tent = new Intent(Splash.this, StartActivity.class);
                    startActivity(tent);
                    finish();
                }

            }
        },2500);
    }
}
