package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;
import com.quickcar.thuexe.Widget.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListPassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;
    private SharePreference preference;
    private ImageView imgRefresh, imgMenu;
    private Button btnWait, btnBusy;
    private ArrayList<Marker> arrMaker = new ArrayList<>();
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passenger);
        mContext = this;
        preference = new SharePreference(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        getCurrentLocation();
        mapFragment.getMapAsync(this);

        btnBusy     = (Button)      findViewById(R.id.btn_busy);
        btnWait     = (Button)      findViewById(R.id.btn_wait);
        imgRefresh  = (ImageView)   findViewById(R.id.img_refresh);
        imgMenu     = (ImageView)   findViewById(R.id.img_menu);
        if (preference.getStatus() == 1) {
            btnBusy.setBackgroundColor(Utilites.getColor(mContext,R.color.red_1));
            btnWait.setBackgroundColor(Utilites.getColor(mContext,R.color.white));
        }else {
            btnBusy.setBackgroundColor(Utilites.getColor(mContext,R.color.white));
            btnWait.setBackgroundColor(Utilites.getColor(mContext,R.color.red_1));
        }

        btnBusy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preference.getStatus() == 0 ) {
                    preference.saveStatus(1);
                    btnBusy.setBackgroundColor(Utilites.getColor(mContext, R.color.red_1));
                    btnWait.setBackgroundColor(Utilites.getColor(mContext, R.color.white));
                    sendLocationToServer(preference.getStatus());
                }
            }
        });
        btnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preference.getStatus() == 1 ) {
                    preference.saveStatus(0);
                    btnBusy.setBackgroundColor(Utilites.getColor(mContext, R.color.white));
                    btnWait.setBackgroundColor(Utilites.getColor(mContext, R.color.red_1));
                }
            }
        });
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllMarker();
                getCurrentLocation();
                getCarAround();

            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ListPassengerActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.intro_menu_owner, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.edit_user){
                            Intent intent = new Intent(ListPassengerActivity.this, EditOwnerActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(this);
        if (gps.handlePermissionsAndGetLocation())
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                if (!Defines.startThread) {
                    Defines.startThread = true;
                    Thread t = new Thread(new locate());
                    t.start();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Thông báo");  // GPS not found
                builder.setMessage("Chức năng này cần lấy vị trí hiện tại của bạn.Bạn có muốn bật định vị?"); // Want to enable?
                builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),1000);
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                final ProgressDialog locate = new ProgressDialog(mContext);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Đang lấy vị trí...");
                locate.show();
                GPSTracker gps  = new GPSTracker(this);
                if (gps.getLongitude() == 0 && gps.getLatitude() ==0) {
                    gps.getLocationCoodinate(new GPSTracker.LocateListener() {
                        @Override
                        public void onLocate(double mlongitude, double mlatitude) {
                            removeAllMarker();
                            getCurrentLocation();
                            getCarAround();
                            locate.dismiss();
                            //Toast.makeText(mContext, mlongitude+","+mlatitude,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    getCurrentLocation();
                    //Toast.makeText(mContext, gps.getLongitude()+","+gps.getLatitude(),Toast.LENGTH_SHORT).show();
                    locate.dismiss();
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS) {
            getCurrentLocation();
        }
    }
    private void sendLocationToServer(int status) {
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()){
            if (gps.handlePermissionsAndGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
        }
        RequestParams params;
        params = new RequestParams();
        params.put("lon", longitude);
        params.put("lat", latitude);
        params.put("car_number", preference.getLicense());
        params.put("phone", preference.getPhone());
        params.put("status", status);
        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_LOCATE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getCarAround();
    }

    private void getCarAround() {
        // show current position
        LatLng sydney = new LatLng(latitude,longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Vị trí của bạn"));
        arrMaker.add(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));


        RequestParams params;
        params = new RequestParams();
        params.put("lon", longitude);
        params.put("lat", latitude);
        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_AROUND, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        double lat        = jsonobject.getDouble("lat");
                        double lon        = jsonobject.getDouble("lon");
                        String timeDate   = jsonobject.getString("date_time");

                        double distance   = jsonobject.getDouble("D");
                        DecimalFormat df = new DecimalFormat("#.#");
                        String gap = "";
                        if ((int) distance == 0)
                            gap = df.format(distance*1000) + " m";
                        else
                            gap = df.format(distance) + " km";
                        LatLng sydney = new LatLng(lat,lon);
                        Marker maker = mMap.addMarker(new MarkerOptions().position(sydney).title(gap));
                        maker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon));
                        arrMaker.add(maker);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("JSON", new String(responseBody));
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }

    private class locate implements Runnable {
        public void run() {
            try {
                while (true) {
                    if (preference.getStatus() == 0) {
                        Log.e("TAG", "loop");
                        sendLocationToServer(preference.getStatus());
                        Thread.sleep(Defines.LOOP_TIME);
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro_menu_owner, menu);
        return true;
    }
    private void removeAllMarker(){
        for (Marker marker : arrMaker)
            marker.remove();
    }

}

