package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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

/**
 * Created by DatNT on 11/29/2016.
 */

public class ListCarAroundFragment  extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double latitude, longitude;
    private SharePreference preference;
    private ImageView imgRefresh;
    private Button btnWait, btnBusy;
    private ArrayList<Marker> arrMaker = new ArrayList<>();
    private Context mContext;
    private ProgressDialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_car_around_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        ((ListPassengerActivity) getActivity()).updateApi(new ListPassengerActivity.OnDataMap() {
            @Override
            public void OnDataMap() {
                removeAllMarker();
                getCurrentLocation();
                getCarAround();
            }
        });
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        //moveDrawerToTop();
        initComponents();

    }

    private void initComponents() {
        preference = new SharePreference(getActivity());
     /*   if (!checkExpireToken()) {
            getCurrentLocation();
        }else{
            showDialogExpire();
        }*/
        getCurrentLocation();


        btnBusy     = (Button)      getActivity().findViewById(R.id.btn_busy);
        btnWait     = (Button)      getActivity().findViewById(R.id.btn_wait);
        imgRefresh  = (ImageView)   getActivity().findViewById(R.id.img_refresh);

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
    }
    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(getActivity());
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
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),1);
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
    private class locate implements Runnable {
        public void run() {
            try {
                while (Defines.isDriver) {
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
    private void sendLocationToServer(int status) {
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()){
            if (gps.handlePermissionsAndGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
            }
        }
        RequestParams params;
        params = new RequestParams();
        DecimalFormat df = new DecimalFormat("#.######");
        String lon = df.format(longitude).replace(",", ".");
        String lat = df.format(latitude).replace(",", ".");
        params.put("lon", lon);
        params.put("lat",  lat);
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

    private void removeAllMarker(){
        for (Marker marker : arrMaker)
            marker.remove();
    }
    private void getCarAround() {
        // show current position
        LatLng sydney = new LatLng(latitude,longitude);
        Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Vị trí của bạn"));
        arrMaker.add(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));


        RequestParams params;
        params = new RequestParams();
        DecimalFormat df = new DecimalFormat("#.######");
        String lon = df.format(longitude).replace(",", ".");
        String lat = df.format(latitude).replace(",", ".");
        params.put("lon", lon);
        params.put("lat",  lat);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if (!checkExpireToken())
        getCarAround();
    }
}
