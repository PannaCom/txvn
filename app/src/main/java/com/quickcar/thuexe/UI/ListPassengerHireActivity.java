package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
import com.quickcar.thuexe.Controller.PassengerBookingAdapter;
import com.quickcar.thuexe.Controller.PassengerHireAdapter;
import com.quickcar.thuexe.Models.BookingObject;
import com.quickcar.thuexe.Models.CarInforObject;
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

public class ListPassengerHireActivity extends AppCompatActivity {
    private RecyclerView vehicleView;
    private ArrayList<BookingObject> vehicles;
    private Context mContext;
    private TextView txtNoResult;
    private PassengerHireAdapter adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeToRefresh;
    private SharePreference preference;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passenger_hire);
        preference = new SharePreference(this);
        mContext = this;
        initComponents();
    }
    private void initComponents() {
        vehicleView                 =   (RecyclerView)          findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    findViewById(R.id.swipe_view);
        btnBack                     =   (ImageView)             findViewById(R.id.img_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListHire();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilites.isOnline(mContext)) {
            getListHire();
        }else
            showOffline();
    }
    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
    private void getListHire() {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        Log.i("params deleteDelivery", params.toString());
        //swipeToRefresh.setRefreshing(true);

        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        vehicles = new ArrayList<>();
        BaseService.getHttpClient().post(Defines.URL_GET_BOOKING_BY_PHONE, params, new AsyncHttpResponseHandler() {

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
                        parseJsonResult(jsonobject);
                    }
                    if(vehicles.size()>0) {
                        adapter = new PassengerHireAdapter(mContext, vehicles);
                        vehicleView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new PassengerHireAdapter.onClickListener() {
                            @Override
                            public void onItemClick() {
                                getListHire();
                            }
                        });
                        //swipeToRefresh.setRefreshing(false);

                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Bạn chưa đặt chuyến xe nào");
                        //swipeToRefresh.setRefreshing(false);
                    }
                    dialog.dismiss();
                    //prepareDataSliding();
                    if (swipeToRefresh.isRefreshing())
                        swipeToRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }
    private void parseJsonResult(JSONObject jsonobject) {
        try {
            int id             = jsonobject.getInt("id");
            String carFrom     = jsonobject.getString("car_from");
            String carTo       = jsonobject.getString("car_to");
            String carType     = jsonobject.getString("car_type");

            String hireType    = jsonobject.getString("car_hire_type");
            String carSize     = jsonobject.getString("car_size");
            String phone       = jsonobject.getString("phone");
            String name        = jsonobject.getString("name");
            String dateFrom    = jsonobject.getString("date_from");
            String dateTo      = jsonobject.getString("date_to");
            String dateTime    = jsonobject.getString("date_time");
            double lon1        = jsonobject.getDouble("lon1");
            double lat1        = jsonobject.getDouble("lat1");
            double lon2        = jsonobject.getDouble("lon2");
            double lat2        = jsonobject.getDouble("lat2");
            String status      = jsonobject.getString("status");
            double distance    = jsonobject.getDouble("D");

            BookingObject passenger = new BookingObject(id,carFrom,carTo,carType,hireType,carSize,phone,name,dateFrom,dateTo,dateTime,new LatLng(lat1,lon1), new LatLng(lat2,lon2),status,distance);
            vehicles.add(passenger);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
