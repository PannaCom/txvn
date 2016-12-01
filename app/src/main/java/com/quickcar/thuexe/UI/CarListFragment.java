package com.quickcar.thuexe.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
import com.quickcar.thuexe.Controller.CarTypesAdapter;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.Models.FilterObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.Utilites;
import com.quickcar.thuexe.Widget.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by DatNT on 10/7/2016.
 */

public class CarListFragment extends Fragment {
    private RecyclerView vehicleView;
    private ArrayList<CarInforObject> vehicles;
    private Context mContext;
    private boolean isFilter = false;
    private TextView txtNoResult;
    private MenuItem itemFilter;
    private double longitude, latitude;
    private ActiveCarAdapter adapter;
    private ProgressDialog dialog;
    private SwipeRefreshLayout swipeToRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_list_vehicle, container, false);
        //Get Argument that passed from activity in "data" key value
        ((ListVehicleActivity) getActivity()).updateApi(new ListVehicleActivity.OnDataMap() {
            @Override
            public void OnDataMap() {
                resetBeforeFilter();
                getCurrentLocation();
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
        vehicleView                 =   (RecyclerView)          getView().findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              getView().findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    getView().findViewById(R.id.swipe_view);


        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCurrentLocation();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilites.isOnline(mContext)) {
            getCurrentLocation();
        }else
            showOffline();
    }

    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(mContext);
        // check if GPS enabled
        if (handlePermissionsAndGetLocation())
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                requestToGetListVehicle();

        }else{
            if (swipeToRefresh.isRefreshing())
                swipeToRefresh.setRefreshing(false);
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

    private void requestToGetListVehicle() {
        RequestParams params;
        params = new RequestParams();
        if (Defines.FilterInfor.getCarType().equals("Tất cả"))
            params.put("car_type", "");
        else
            params.put("car_type", Defines.FilterInfor.getCarType());

        if (Defines.FilterInfor.getCarMade().equals("Tất cả"))
            params.put("car_made", "");
        else
            params.put("car_made", Defines.FilterInfor.getCarMade());

        if (Defines.FilterInfor.getCarModel().equals("Tất cả"))
            params.put("car_model", "");
        else
            params.put("car_model", Defines.FilterInfor.getCarModel());


        if (Defines.FilterInfor.getCarSize().equals("Tất cả"))
            params.put("car_size", "");
        else
            params.put("car_size", Defines.FilterInfor.getCarSize().split(" ")[0]);
        DecimalFormat df = new DecimalFormat("#.######");
        String lon = df.format(longitude).replace(",", ".");
        String lat = df.format(latitude).replace(",", ".");
        params.put("lon", lon);
        params.put("lat",  lat);
        params.put("order", Defines.FILTER_ORDER);
        Log.i("params deleteDelivery", params.toString());
        vehicles = new ArrayList<>();
        dialog = new ProgressDialog(getContext());
        if (!swipeToRefresh.isRefreshing()) {
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
        BaseService.getHttpClient().post(Defines.URL_LIST_ONL_VEHICLE, params, new AsyncHttpResponseHandler() {

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
                        adapter = new ActiveCarAdapter(mContext, vehicles);
                        vehicleView.setAdapter(adapter);
                        //swipeToRefresh.setRefreshing(false);

                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        txtNoResult.setText("Không có xe nào cho tuyến này");
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
                Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private void resetBeforeFilter() {
        vehicles = new ArrayList<>();
        adapter = new ActiveCarAdapter(mContext, vehicles);
        vehicleView.setAdapter(adapter);
        txtNoResult.setVisibility(View.GONE);
    }
    private void parseJsonResult(JSONObject jsonobject) {
        try {
            String name         = jsonobject.getString("name");
            String phone        = jsonobject.getString("phone");
            String carModel     = jsonobject.getString("car_model");

            String carSize      = jsonobject.getString("car_size");
            String carType      = jsonobject.getString("car_type");
            String carMade      = jsonobject.getString("car_made");
            double distance     = jsonobject.getDouble("D");
            String price        = jsonobject.getString("car_price");
            CarInforObject busInfor = new CarInforObject(name,phone,carModel,carMade,carType,carSize,distance,price);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public boolean handlePermissionsAndGetLocation() {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        int hasWriteContactsPermission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Defines.REQUEST_CODE_LOCATION_PERMISSIONS);
            return false;
        }
        return true;
    }
}
