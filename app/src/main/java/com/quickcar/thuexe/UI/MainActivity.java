package com.quickcar.thuexe.UI;

import android.content.Context;
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
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preference = new SharePreference(this);
        mContext = this;
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
    public static void share(Context context, String text, String subject, String title, String dialogHeaderText) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        context.startActivity(Intent.createChooser(intent, dialogHeaderText));
    }
    private View.OnClickListener passenger_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            preference.saveRole(2);
            Intent intent = new Intent(MainActivity.this, ListVehicleActivity.class);
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
