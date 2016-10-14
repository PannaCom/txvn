package com.quickcar.thuexe.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

public class MainActivity extends AppCompatActivity {
    private Button btnPassenger, btnBusOwner;
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new SharePreference(this);
        initComponents();
    }


    /*
    Initiate all component of activity
    */
    private void initComponents() {
        btnPassenger            =       (Button)        findViewById(R.id.btn_passenger);
        btnBusOwner             =       (Button)        findViewById(R.id.btn_bus_owner);


        //set text about me
        //txtIntroduce.setText(Html.fromHtml(getString(R.string.about_me)));
       // txtContact.setText(Html.fromHtml(getString(R.string.contact_me)));

        btnPassenger.setOnClickListener(passenger_click_listener);
        btnBusOwner.setOnClickListener(bus_owner_click_listener);

        Log.e("TAG", Defines.token);

    }


    /*
    Passenger click event
       1: owner
       2: passenger
    */

    private View.OnClickListener passenger_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preference.saveRole(2);
            Intent intent = new Intent(MainActivity.this, SearchActiveBusScreen.class);
            startActivity(intent);
            finish();
        }
    };

     /*
    Bus owner click event
    */

    private View.OnClickListener bus_owner_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preference.saveRole(1);
            Intent intent = new Intent(MainActivity.this, NewVehicleActivity.class);
            startActivity(intent);
            finish();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        Utilites.systemUiVisibility(this);
    }
}
