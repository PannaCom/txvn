package com.quickcar.thuexe.UI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new SharePreference(this);
        Utilites.systemUiVisibility(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preference.getLogin()) {
                    Intent intent = new Intent(SplashActivity.this, ListPassengerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if(preference.getRole() == 2) {
                        Intent intent = new Intent(SplashActivity.this, ListVehicleActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(preference.getRole() == 0){
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(SplashActivity.this, NewVehicleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 2000);
    }



}
