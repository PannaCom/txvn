package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Widget.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DatNT on 10/7/2016.
 */

public class MapCarActiveFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mMapView;
    private double longitude, latitude;
    private ProgressDialog dialog;
    private ArrayList<Marker> markerList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_active_car_fragment, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        ((ListVehicleActivity) getActivity()).updateApi(new ListVehicleActivity.OnDataPass() {
            @Override
            public void onDataPass() {
                removeAllMarker();
                getCurrentLocation();
                requestToGetListVehicle();
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void getCurrentLocation() {
        GPSTracker gps = new GPSTracker(getContext());
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
        //swipeToRefresh.setRefreshing(true);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        // show current position
        LatLng sydney = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Vị trí của bạn"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));


        BaseService.getHttpClient().post(Defines.URL_LIST_ONL_VEHICLE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                dialog.dismiss();
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        parseJsonResult(jsonobject,i);
                    }
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
    private void parseJsonResult(JSONObject jsonobject, int position) {
        try {
            String name         = jsonobject.getString("name");
            String phone           = jsonobject.getString("phone");
            String carModel         = jsonobject.getString("car_model");

            String carSize         = jsonobject.getString("car_size");
            String carType    = jsonobject.getString("car_type");
            String carMade     = jsonobject.getString("car_made");
            double distance     = jsonobject.getDouble("D");
            double lat        = jsonobject.getDouble("lat");
            double lon        = jsonobject.getDouble("lon");


            LatLng sydney = new LatLng(lat,lon);
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(name));
            switch (position%5){
                case 0:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon));
                    break;
                case 1:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon_1));
                    break;
                case 2:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon_2));
                    break;
                case 3:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon_4));
                    break;
                case 4:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon_5));
                    break;
                default:
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon));
            }


            markerList.add(marker);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        getCurrentLocation();
        requestToGetListVehicle();

    }
    private void removeAllMarker(){
        for (Marker marker : markerList)
            marker.remove();
    }
}
