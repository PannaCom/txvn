package com.quickcar.thuexe.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
import com.quickcar.thuexe.Controller.PassengerBookingAdapter;
import com.quickcar.thuexe.Models.BookingObject;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
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

public class ListPassengerBookingFragment extends Fragment {
    private RecyclerView vehicleView;
    private TextView txtNoResult;
    private SwipeRefreshLayout swipeToRefresh;
    private double longitude, latitude;
    private ArrayList<BookingObject> passengers;
    private ProgressDialog dialog;
    private PassengerBookingAdapter adapter;
    private LinearLayout layoutNewRental, layoutResidualCar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_passenger_booking_fragment, container, false);
        ((ListPassengerActivity) getActivity()).updateApi(new ListPassengerActivity.OnDataPass() {
            @Override
            public void onDataPass() {
                getAllPassengerBooking();
            }
        });
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();
    }

    private void initComponents() {
        vehicleView                 =   (RecyclerView)          getView().findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              getView().findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    getView().findViewById(R.id.swipe_view);
        layoutNewRental             =   (LinearLayout)          getView().findViewById(R.id.layout_new_car_rental);
        layoutResidualCar           =   (LinearLayout)          getView().findViewById(R.id.layout_new_residual_car);
        layoutNewRental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentList = new Intent(getActivity(), NewCarRentalActivity.class);
                startActivity(intentList);
            }
        });
        layoutResidualCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentList = new Intent(getActivity(), DriverBookingInfoActivity.class);
                startActivity(intentList);
                getActivity().finish();
            }
        });
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentLocation();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        vehicleView.setLayoutManager(llm);
        if (Utilites.isOnline(getActivity())) {
            getAllPassengerBooking();
        }else
            showOffline();
    }
    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.handlePermissionsAndGetLocation())
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                getAllPassengerBooking();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                passengers = new ArrayList<>();
                adapter = new PassengerBookingAdapter(getActivity(), passengers);
                vehicleView.setAdapter(adapter);
            }

    }
    private void getAllPassengerBooking() {
        GPSTracker gps = new GPSTracker(getActivity());
        if (!handlePermissionsAndGetLocation() || !gps.canGetLocation()){
            passengers = new ArrayList<>();
            adapter = new PassengerBookingAdapter(getActivity(), passengers);
            vehicleView.setAdapter(adapter);
            return;
        }
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        RequestParams params;
        params = new RequestParams();
        DecimalFormat df = new DecimalFormat("#.######");
        String lon = df.format(longitude).replace(",", ".");
        String lat = df.format(latitude).replace(",", ".");
        params.put("lon", lon);
        params.put("lat", lat);
        Log.i("params deleteDelivery", params.toString());
        //swipeToRefresh.setRefreshing(true);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        passengers = new ArrayList<>();
        txtNoResult.setVisibility(View.GONE);
        BaseService.getHttpClient().post(Defines.URL_GET_BOOKING, params, new AsyncHttpResponseHandler() {

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
                    if(passengers.size()>0) {
                        adapter = new PassengerBookingAdapter(getActivity(), passengers);
                        vehicleView.setAdapter(adapter);
                        //swipeToRefresh.setRefreshing(false);

                    }else{
                        passengers = new ArrayList<>();
                        adapter = new PassengerBookingAdapter(getActivity(), passengers);
                        vehicleView.setAdapter(adapter);
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Bạn nên bật ứng dụng thường xuyên để đón khách gần đây");
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
                Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }
    public boolean handlePermissionsAndGetLocation() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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
            passengers.add(passenger);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
}
