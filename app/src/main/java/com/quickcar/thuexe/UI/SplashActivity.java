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

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

public class SplashActivity extends AppCompatActivity {
    private SharePreference preference;
    private AlertDialog dialog;
    private ImageView imgLoading;
    private LinearLayout layoutLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new SharePreference(this);
        Utilites.systemUiVisibility(this);
        imgLoading = (ImageView) findViewById(R.id.img_loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();
        FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("test");


    }
    @Override
    protected void onResume() {
        super.onResume();
        layoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        checkOnline();

    }
    private void checkOnline() {
        if (!Utilites.isOnline(this)){
            dialog = new AlertDialog.Builder(this)
                    .setTitle("Thông báo")
                    .setMessage("Bạn chưa kết nối mạng")
                    .setPositiveButton("Thử lại", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkOnline();
                        }
                    })
                    .setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return;
        }
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        layoutLoading.setVisibility(View.VISIBLE);
        if (preference.getToken().equals("")) {
            LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (preference.getLogin()) {
                        if (preference.getActive()) {
                            Intent intent = new Intent(SplashActivity.this, ListPassengerActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, ActiveAccountActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (preference.getRole() == 0) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (preference.getRole() == 2) {
                            Intent intent = new Intent(SplashActivity.this, ListVehicleActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(SplashActivity.this, NewVehicleActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }, 2000);
        }
    }
    BroadcastReceiver tokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra("token");
            if(token != null)
            {
                Intent in = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }


        }
    };

}
