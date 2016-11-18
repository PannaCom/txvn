package com.quickcar.thuexe.UI;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListPassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude, longitude;
    private SharePreference preference;
    private ImageView imgRefresh, imgMenu;
    private Button btnWait, btnBusy;
    private ArrayList<Marker> arrMaker = new ArrayList<>();
    private Context mContext;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passenger);

        mContext = this;
        preference = new SharePreference(this);
        if (!checkExpireToken()){
            getCurrentLocation();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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
                        switch (item.getItemId()){
                            case R.id.edit_user:
                                Intent intent = new Intent(ListPassengerActivity.this, EditOwnerActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.share_social:
                                showDialogShareSocial();
                                return true;
                        }
                        return false;
                    }
                });
            }
        });
        if (!preference.getRegisterToken()) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            registerToken(preference.getToken());
        }
    }

    private boolean checkExpireToken() {
        DateTime lastDay = new DateTime(preference.getDateActive());
        DateTime now = new DateTime();

        int days = Days.daysBetween(lastDay.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays();

        if (days > preference.getDayExpire()){
            showDialogExpire();
            return true;
        }
        return false;
    }

    private void showDialogExpire() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.renew_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText edtCode = (EditText) dialog.findViewById(R.id.edt_code);


        final Button btnActive = (Button) dialog.findViewById(R.id.btn_active);
        btnActive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (!Utilites.isOnline(mContext)){
                    Toast.makeText(mContext,"Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtCode.getText().toString().equals(""))
                {
                    Toast.makeText(mContext,"Bạn chưa nhập mã", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendActive(edtCode.getText().toString(), btnActive, dialog);
            }
        });
        dialog.show();
    }

    private void sendActive(String code, final Button btnActive, Dialog dialog ) {
        btnActive.setEnabled(false);
        btnActive.setClickable(false);
        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        params.put("code",  code);

        Log.i("params deleteDelivery", params.toString());

        BaseService.getHttpClient().post(Defines.URL_RENEW, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result > 0) {
                    preference.saveDayExpire(result);
                    preference.saveDateActive(new DateTime().toString());
                    getCurrentLocation();
                }else
                    Toast.makeText(mContext, "Tài khoản kích hoạt thất bại", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnActive.setEnabled(true);
                        btnActive.setClickable(true);
                    }
                },2000);

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

    private void registerToken(String token) {

        RequestParams params;
        params = new RequestParams();
        params.put("tobject", preference.getRole());
        params.put("regid",  token);
        params.put("os", 1);


        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_REGISTER_TOKEN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result == 1)
                    preference.saveRegisterToken(true);
                dialog.dismiss();
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
    private void showDialogShareSocial() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ");
        String shareMessage = "Bạn cần thuê xe hay bạn là tài xế/nhà xe/hãng xe có xe riêng, hãy dùng thử ứng dụng thuê xe  trên di động tại http://thuexevn.com";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Chọn phương thức để chia sẻ"));
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

