package com.quickcar.thuexe.UI;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.ActiveCarAdapter;
import com.quickcar.thuexe.Controller.PlaceArrayAdapter;
import com.quickcar.thuexe.Controller.RentalCarAdapter;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.Models.RentalInfo;
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

public class PassengerListRentalActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private AutoCompleteTextView autoPlaceFrom, autoPlaceTo;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter, mPlaceToArrayAdapter;
    private Context mContext;
    private Button btnConfirm;
    private LatLng llFrom, llTo;
    private ProgressDialog dialog;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private FloatingActionButton btnFilter;
    private RecyclerView vehicleView;
    private ArrayList<RentalInfo> vehicles;
    private TextView txtNoResult;
    private RentalCarAdapter adapter;
    private int currentPosition;
    private double longitude, latitude;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list_rental);
        mContext = this;
        mGoogleApiClient = new GoogleApiClient.Builder(PassengerListRentalActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        initComponents();
    }

    private void initComponents() {
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this, BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this, BOUNDS_MOUNTAIN_VIEW, null);
        ImageView btnBack = (ImageView) findViewById(R.id.img_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        vehicleView = (RecyclerView) findViewById(R.id.vehicle_view);
        txtNoResult = (TextView) findViewById(R.id.txt_no_result);

        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        vehicleView.setLayoutManager(llm);

        btnFilter = (FloatingActionButton) findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSheetFilter();
            }
        });
    }

    private void showSheetFilter() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        View sheetView = getLayoutInflater().inflate(R.layout.filter_passenger_driver, null);
        mBottomSheetDialog.setContentView(sheetView);

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) sheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        autoPlaceFrom = (AutoCompleteTextView) sheetView.findViewById(R.id.auto_place_from);
        autoPlaceFrom.setThreshold(1);
        autoPlaceFrom.setOnItemClickListener(mAutocompleteFromClickListener);
        autoPlaceFrom.setAdapter(mPlaceArrayFromAdapter);

        autoPlaceTo = (AutoCompleteTextView) sheetView.findViewById(R.id.auto_place_to);
        autoPlaceTo.setThreshold(1);
        autoPlaceTo.setOnItemClickListener(mAutocompleteToClickListener);
        autoPlaceTo.setAdapter(mPlaceToArrayAdapter);
        llFrom = null;
        llTo   = null;
        final ImageView imgFrom = (ImageView) sheetView.findViewById(R.id.img_from);
        final ImageView imgTo = (ImageView) sheetView.findViewById(R.id.img_to);


        imgFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceFrom.setText("");
                imgFrom.setVisibility(View.GONE);
                autoPlaceFrom.setFocusable(true);
                autoPlaceFrom.setFocusableInTouchMode(true);
            }
        });
        imgTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceTo.setText("");
                imgTo.setVisibility(View.GONE);

                autoPlaceTo.setFocusable(true);
                autoPlaceTo.setFocusableInTouchMode(true);
            }
        });
        autoPlaceFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 > 0) {
                    imgFrom.setVisibility(View.VISIBLE);
                } else
                    imgFrom.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        autoPlaceTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 > 0) {
                    imgTo.setVisibility(View.VISIBLE);
                } else
                    imgTo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button btnConfirm = (Button) sheetView.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationAndRequest(llFrom, llTo);
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();
    }

    private AdapterView.OnItemClickListener mAutocompleteFromClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //hideSoftKeyboard(getActivity());
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayFromAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsFromCallback);
            //Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsFromCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            llFrom = place.getLatLng();
            autoPlaceFrom.setSelection(0);

            autoPlaceFrom.setFocusable(false);
            autoPlaceFrom.setFocusableInTouchMode(false);

        }
    };
    private AdapterView.OnItemClickListener mAutocompleteToClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //hideSoftKeyboard(getActivity());
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceToArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsToCallback);

        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsToCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            llTo = place.getLatLng();
            autoPlaceTo.setSelection(0);
            autoPlaceTo.setFocusable(false);
            autoPlaceTo.setFocusableInTouchMode(false);

        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        getLocationAndRequest(llFrom, llTo);

    }
    private void getLocationAndRequest(LatLng llFrom, LatLng llTo){
        if (Utilites.isOnline(mContext)) {
            GPSTracker gps = new GPSTracker(mContext);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                String latFrom, latTo, lonFrom, lonTo;
                if (llFrom == null){
                    latFrom = "";
                    lonFrom = "";
                }else{
                    latFrom = String.valueOf(llFrom.latitude);
                    lonFrom = String.valueOf(llFrom.longitude);
                }
                if (llTo == null){
                    latTo = "";
                    lonTo = "";
                }else{
                    latTo = String.valueOf(llTo.latitude);
                    lonTo = String.valueOf(llTo.longitude);
                }
                requestListRental(String.valueOf(longitude),String.valueOf(latitude),lonFrom,latFrom,lonTo,latTo);
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
        } else
            showOffline();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog locate = new ProgressDialog(mContext);
        locate.setIndeterminate(true);
        locate.setCancelable(false);
        locate.setMessage("Đang lấy vị trí...");
        locate.show();
        GPSTracker gps = new GPSTracker(mContext);
        if (gps.getLongitude() == 0 && gps.getLatitude() ==0) {
            gps.getLocationCoodinate(new GPSTracker.LocateListener() {
                @Override
                public void onLocate(double mlongitude, double mlatitude) {
                    latitude = mlatitude;
                    longitude = mlongitude;
                    locate.dismiss();
                    requestListRental(String.valueOf(longitude),String.valueOf(latitude),"","","","");

                    //Toast.makeText(mContext, mlongitude+","+mlatitude,Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            locate.dismiss();
            requestListRental(String.valueOf(longitude),String.valueOf(latitude),"","","","");
            //Toast.makeText(mContext, gps.getLongitude()+","+gps.getLatitude(),Toast.LENGTH_SHORT).show();

        }
    }
    private void requestListRental(String lon, String lat,String lon1, String lat1,String lon2, String lat2) {
        RequestParams params;
        params = new RequestParams();
        params.put("lon", lon);
        params.put("lat", lat);
        params.put("lon_from", lon1);
        params.put("lat_from", lat1);
        params.put("lon_to", lon2);
        params.put("lat_to", lat2);
        Log.i("params deleteDelivery", params.toString());
        vehicles = new ArrayList<>();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_BOOKING_FOR_CUSTOMER, params, new AsyncHttpResponseHandler() {

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
                    if (vehicles.size() > 0) {
                        adapter = new RentalCarAdapter(mContext, vehicles);
                        vehicleView.setAdapter(adapter);
                        adapter.setOnCallListener(new RentalCarAdapter.onClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                currentPosition = position;
                                if (Build.VERSION.SDK_INT >= 22) {
                                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CALL_PHONE}, Defines.REQUEST_CODE_CALL_PERMISSIONS);
                                        return;
                                    }
                                }
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + vehicles.get(position).getPhone()));
                                mContext.startActivity(callIntent);
                            }
                        });
                        //swipeToRefresh.setRefreshing(false);
                        txtNoResult.setVisibility(View.GONE);
                        vehicleView.setVisibility(View.VISIBLE);
                    } else {
                        txtNoResult.setVisibility(View.VISIBLE);
                        vehicleView.setVisibility(View.GONE);
                        txtNoResult.setText("Không có xe nào cho tuyến này");
                        //swipeToRefresh.setRefreshing(false);
                    }
                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Toast.makeText(mContext, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                Toast.makeText(mContext, getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_CALL_PERMISSIONS) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + vehicles.get(currentPosition).getPhone()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mContext.startActivity(callIntent);
        }
    }

    private void parseJsonResult(JSONObject jsonobject) {
        try {
            int id              = jsonobject.getInt("id");
            String carFrom      = jsonobject.getString("car_from");
            String carTo        = jsonobject.getString("car_to");
            String carType      = jsonobject.getString("car_type");
            String carHireType  = jsonobject.getString("car_hire_type");
            int carSize         = jsonobject.getInt("car_size");
            String phone        = jsonobject.getString("phone");
            String name         = jsonobject.getString("name");
            String dateFrom         = jsonobject.getString("date_from");
            String dateTo         = jsonobject.getString("date_to");
            String dateTime         = jsonobject.getString("date_time");
            long lon1         = jsonobject.getLong("lon1");
            long lat1         = jsonobject.getLong("lat1");
            long lon2         = jsonobject.getLong("lon2");
            long lat2         = jsonobject.getLong("lat2");
            int status         = jsonobject.getInt("status");
            double distance     = jsonobject.getDouble("D");



            RentalInfo busInfor = new RentalInfo(id,carFrom,carTo,carType,carHireType,carSize,phone,name,dateFrom,dateTo,new LatLng(lat1,lon1), new LatLng(lat2,lon2),status,distance);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showOffline() {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText("Không có kết nối mạng");
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
