package com.quickcar.thuexe.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.quickcar.thuexe.Controller.PlaceArrayAdapter;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.GetAllDataBooking;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewCarRentalActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private AutoCompleteTextView autoPlaceFrom, autoPlaceTo;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private ArrayList<String> aHireType, aVehicleType, aCarSize;
    private TextView edtCarType, edtHireType, edtCarSize, edtDateFrom, edtDateTo, txtWarn,edtCustomerName, edtCustomerPhone;;
    private ImageView imgFrom, imgTo;
    private Context mContext;
    private Button btnConfirm;
    private LatLng llFrom, llTo;
    private ProgressDialog dialog;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private SharePreference preference;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car_rental);
        mContext = this;
        preference = new SharePreference(this);
        mGoogleApiClient = new GoogleApiClient.Builder(NewCarRentalActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        initComponents();
    }
    private void initComponents() {
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);


        autoPlaceFrom = (AutoCompleteTextView) findViewById(R.id.auto_place_from);
        autoPlaceFrom.setThreshold(1);
        autoPlaceFrom.setOnItemClickListener(mAutocompleteFromClickListener);
        autoPlaceFrom.setAdapter(mPlaceArrayFromAdapter);

        autoPlaceTo = (AutoCompleteTextView) findViewById(R.id.auto_place_to);
        autoPlaceTo.setThreshold(1);
        autoPlaceTo.setOnItemClickListener(mAutocompleteToClickListener);
        autoPlaceTo.setAdapter(mPlaceToArrayAdapter);

        edtCarType      = (TextView)        findViewById(R.id.edt_car_type);
        edtCarSize      = (TextView)        findViewById(R.id.edt_car_size);

        edtHireType     = (TextView)        findViewById(R.id.edt_hire_type);
        edtDateFrom     = (TextView)        findViewById(R.id.edt_date_from);
        edtDateTo       = (TextView)        findViewById(R.id.edt_date_to);
        edtCustomerName = (TextView)        findViewById(R.id.edt_customer_name);
        edtCustomerPhone= (TextView)        findViewById(R.id.edt_customer_phone);
        btnConfirm      = (Button)          findViewById(R.id.btn_confirm);
        txtWarn         = (TextView)        findViewById(R.id.txt_warn);

        imgFrom         = (ImageView)       findViewById(R.id.img_from);
        imgTo           = (ImageView)       findViewById(R.id.img_to);

        getCarInfor();
        ImageView btnBack = (ImageView)     findViewById(R.id.img_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                if (i2 >0) {
                    imgFrom.setVisibility(View.VISIBLE);
                }else
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
                if (i2 >0) {
                    imgTo.setVisibility(View.VISIBLE);
                }else
                    imgTo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtDateFrom.setOnClickListener(get_date_from_listener);
        edtDateTo.setOnClickListener(get_date_to_listener);
        edtHireType.setOnClickListener(get_hire_type_listener);
        btnConfirm.setOnClickListener(booking_ticket_listener);
        checkOnline();

    }
    private void getCarInfor() {
        try {
            JSONObject carObject = new JSONObject(preference.getCarInfor());
            String hoten        = carObject.getString("hoten");
            String sodienthoai  = carObject.getString("sodienthoai");
            String socho        = carObject.getString("socho");
            String loaixe       = carObject.getString("loaixe");

            edtCustomerName.setText(hoten);
            edtCustomerPhone.setText(sodienthoai);
            edtCarSize.setText(socho);
            edtCarType.setText(loaixe);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private View.OnClickListener booking_ticket_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            txtWarn.setVisibility(View.GONE);
            if (!checkRequestParams()) {
                letsBooking();
            }
        }
    };
    private boolean checkRequestParams() {
        if (autoPlaceFrom.getText().toString().equals("")|| autoPlaceFrom.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập điểm đi");
            requestFocus(txtWarn);
            return true;
        }
        if (autoPlaceTo.getText().toString().equals("")|| autoPlaceTo.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập điểm đến");
            requestFocus(txtWarn);
            return true;
        }
        if (edtCarType.getText().toString().equals("")|| edtCarType.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập loại xe");
            requestFocus(txtWarn);
            return true;
        }
        if (edtCustomerName.getText().toString().equals("")|| edtCustomerName.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập tên");
            requestFocus(txtWarn);
            return true;
        }
        if (edtCustomerPhone.getText().toString().equals("")|| edtCustomerPhone.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số điện thoại");
            requestFocus(txtWarn);
            return true;
        }
        if (edtCarSize.getText().toString().equals("")|| edtCarSize.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số chỗ");
            requestFocus(txtWarn);
            return true;
        }
        if (edtDateFrom.getText().toString().equals("")|| edtDateFrom.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập ngày giờ đi");
            requestFocus(txtWarn);
            return true;
        }
        if (edtDateTo.getText().toString().equals("")|| edtDateTo.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập ngày giờ đến");
            requestFocus(txtWarn);
            return true;
        }
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");
        // Parsing the date
        DateTime fromDate = dtf.parseDateTime(edtDateFrom.getText().toString());
        DateTime toDate = dtf.parseDateTime(edtDateTo.getText().toString());
        DateTime now = new DateTime();
        long diffCurrent = fromDate.getMillis() - now.getMillis()- Defines.TIME_BEFORE_AUCTION_SHORT;;
        if (diffCurrent < 0){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Thời gian đi phải sau thời gian hiện tại ít nhất 1 tiếng");
            requestFocus(txtWarn);
            return true;
        }

        long diffInMillis = toDate.getMillis() - fromDate.getMillis() - Defines.TIME_BEFORE_AUCTION_SHORT;
        if (diffInMillis < 0){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Thời gian về phải lớn hơn thời gian đi ít nhất 1 tiếng");
            requestFocus(txtWarn);
            return true;
        }

        return false;
    }
    private void letsBooking() {
        RequestParams params;
        params = new RequestParams();
        params.put("car_from", autoPlaceFrom.getText().toString());
        params.put("car_to", autoPlaceTo.getText().toString());
        params.put("car_type", edtCarType.getText().toString());
        params.put("car_hire_type", edtHireType.getText().toString());
        params.put("car_size", edtCarSize.getText().toString().split(" ")[0]);
        params.put("from_datetime", edtDateFrom.getText().toString());
        params.put("to_datetime", edtDateTo.getText().toString());
        params.put("lon1", llFrom.longitude);
        params.put("lat1", llFrom.latitude);
        params.put("lon2", llTo.longitude);
        params.put("lat2", llTo.latitude);
        params.put("name", edtCustomerName.getText().toString());
        params.put("phone", edtCustomerPhone.getText().toString());
        Log.i("params deleteDelivery", params.toString());
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
        BaseService.getHttpClient().post(Defines.URL_BOOKING, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result > 0) {
                    Toast.makeText(mContext, "Đăng chuyến thành công, Vui lòng đợi khách hàng gọi đến", Toast.LENGTH_LONG).show();
                    finish();
                }else
                    Toast.makeText(mContext, "Đăng chuyến thất bại",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void checkOnline(){
        if (!Utilites.isOnline(mContext)){
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                    .setTitle("Thông báo")
                    .setMessage("Bạn chưa kết nối mạng")
                    .setCancelable(false)
                    .setPositiveButton("Thử lại", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkOnline();
                        }
                    })
                    .show();
        }else{
            GetAllDataBooking carData = new GetAllDataBooking(this, new GetAllDataBooking.onDataReceived() {
                @Override
                public void onReceived(ArrayList<String> carType, ArrayList<String> size, ArrayList<String> hireType) {
                    aHireType = new ArrayList<>();
                    aHireType.add("Chiều về");
                    aHireType.add("Đi chung");


                    aCarSize = new ArrayList<>();
                    for (String item : size) {
                        if (item.equals("4")){
                            aCarSize.add(item + " chỗ(giá siêu rẻ, không cốp)");
                        }else  if (item.equals("5")){
                            aCarSize.add(item + " chỗ(có cốp)");
                        }else
                            aCarSize.add(item + " chỗ");
                    }


                    aVehicleType = carType;
                }
            });
        }
    }
    private AdapterView.OnItemClickListener mAutocompleteFromClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard((Activity) mContext);
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
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private View.OnClickListener get_hire_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn hình thúc đi")
                    .setSingleChoiceItems(aHireType.toArray(new CharSequence[aHireType.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aHireType.get(which);
                            edtHireType.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private View.OnClickListener get_date_to_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDateTimeDialog(edtDateTo);
        }
    };
    private View.OnClickListener get_date_from_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDateTimeDialog(edtDateFrom);
        }
    };

    private void showDateTimeDialog(final TextView txtDate){
        final View dialogView = View.inflate(mContext, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int mHour, int mMinute) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH); // Note: zero based!
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (datePicker.getYear() == year && datePicker.getMonth() == month && datePicker.getDayOfMonth() == day ){
                    if (mHour <= hour) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (hour > 22)
                                timePicker.setHour(hour);
                            else
                                timePicker.setHour(hour+1);
                            timePicker.setMinute(minutes);
                        }else {
                            if (hour > 22)
                                timePicker.setCurrentHour(hour);
                            else
                                timePicker.setCurrentHour(hour+1);

                            timePicker.setCurrentMinute(minutes);
                        }
                    }
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22){
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            }else
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }else {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22)
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            else
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar now = Calendar.getInstance();
                int cYear = now.get(Calendar.YEAR);
                int cMonth = now.get(Calendar.MONTH);
                int cDay = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (cYear == year && cMonth == month && cDay == dayOfMonth ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (hour > 22)
                            timePicker.setHour(hour);
                        else
                            timePicker.setHour(hour+1);
                        timePicker.setMinute(minutes);
                    }else {
                        if (hour > 22)
                            timePicker.setCurrentHour(hour);
                        else
                            timePicker.setCurrentHour(hour+1);

                        timePicker.setCurrentMinute(minutes);
                    }
                }

            }
        });
        dialogView.findViewById(R.id.datetimeset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                SimpleDateFormat mSDF = new SimpleDateFormat("HH:mm:ss");
                String time = mSDF.format(calendar.getTime());
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formatedDate = sdf.format(new Date(year-1900, month, day));
                txtDate.setText(formatedDate + ' ' + time);
                alertDialog.dismiss();

            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
