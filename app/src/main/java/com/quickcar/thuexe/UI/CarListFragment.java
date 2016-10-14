package com.quickcar.thuexe.UI;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
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

import java.util.ArrayList;

/**
 * Created by DatNT on 10/7/2016.
 */

public class CarListFragment extends Fragment {
    private RecyclerView vehicleView;
    private ArrayList<CarInforObject> vehicles;
    private Context mContext;
    private ImageView imgLoading;
    private boolean isFilter = false;
    private TextView txtNoResult;
    private MenuItem itemFilter;
    private double longitude, latitude;
    private ActiveCarAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_list_vehicle, container, false);
        //Get Argument that passed from activity in "data" key value
        ((ListVehicleActivity) getActivity()).updateApi(new ListVehicleActivity.OnDataMap() {
            @Override
            public void OnDataMap() {
                resetBeforeFilter();
                getCurrentLocation();
                requestToGetListVehicle();
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
        imgLoading                  =   (ImageView)             getView().findViewById(R.id.img_loading);




        AnimationDrawable frameAnimation = (AnimationDrawable) imgLoading.getBackground();
        frameAnimation.start();



        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);
        if (Utilites.isOnline(mContext)) {
            getCurrentLocation();
            requestToGetListVehicle();
        }else
            showOffline();

    }

    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        imgLoading.setVisibility(View.GONE);
        txtNoResult.setText("Không có kết nối mạng");
    }
    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(mContext);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
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
            params.put("car_size", Defines.FilterInfor.getCarSize().substring(0,1));

        params.put("lon", longitude);
        params.put("lat", latitude);
        Log.i("params deleteDelivery", params.toString());
        vehicles = new ArrayList<>();
        //swipeToRefresh.setRefreshing(true);
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
                    imgLoading.setVisibility(View.GONE);
                    //prepareDataSliding();
                    //swipeToRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                imgLoading.setVisibility(View.GONE);
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                imgLoading.setVisibility(View.GONE);
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
            String phone           = jsonobject.getString("phone");
            String carModel         = jsonobject.getString("car_model");

            String carSize         = jsonobject.getString("car_size");
            String carType    = jsonobject.getString("car_type");
            String carMade     = jsonobject.getString("car_made");
            double distance     = jsonobject.getDouble("D");

            CarInforObject busInfor = new CarInforObject(name,phone,carModel,carMade,carType,carSize,distance);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
