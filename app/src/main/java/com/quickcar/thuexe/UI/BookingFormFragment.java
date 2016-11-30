package com.quickcar.thuexe.UI;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by DatNT on 11/15/2016.
 */

public class BookingFormFragment extends Fragment {
    private AutoCompleteTextView autoPlaceFrom, autoPlaceTo;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private ArrayList<String> aCarSize, aHireType, aVehicleType, aAirport;
    private TextView edtCarType, edtHireType, edtDateFrom, edtDateTo, txtWarn, edtCarSize;
    private Context mContext;
    private ImageView imgFrom, imgTo;
    private Button btnConfirm;
    private LatLng llFrom, llTo;
    private ProgressDialog dialog;
    private SharePreference preference;
    DataPassListener mCallback;
    OnDataResult dResult;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.booking_form_fragment, container, false);
        //Get Argument that passed from activity in "data" key value

        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        preference = new SharePreference(mContext);
        ((BookingNowActivity) getActivity()).updateApi(new BookingNowActivity.OnConnected() {
            @Override
            public void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo) {
                mPlaceArrayFromAdapter = placeFrom;
                mPlaceToArrayAdapter = placeTo;
                mGoogleApiClient = googleApi;
                initComponents();
            }
        });
    }

    private void initComponents() {
        autoPlaceFrom = (AutoCompleteTextView) getActivity().findViewById(R.id.auto_place_from);
        autoPlaceFrom.setThreshold(1);
        autoPlaceFrom.setOnItemClickListener(mAutocompleteFromClickListener);
        autoPlaceFrom.setAdapter(mPlaceArrayFromAdapter);

        autoPlaceTo = (AutoCompleteTextView) getActivity().findViewById(R.id.auto_place_to);
        autoPlaceTo.setThreshold(1);
        autoPlaceTo.setOnItemClickListener(mAutocompleteToClickListener);
        autoPlaceTo.setAdapter(mPlaceToArrayAdapter);

        edtCarType      = (TextView)        getActivity().findViewById(R.id.edt_car_type);
        edtHireType     = (TextView)        getActivity().findViewById(R.id.edt_hire_type);
        edtDateFrom     = (TextView)        getActivity().findViewById(R.id.edt_date_from);
        edtCarSize      = (TextView)        getActivity().findViewById(R.id.edt_car_size);
        edtDateTo       = (TextView)        getActivity().findViewById(R.id.edt_date_to);
        btnConfirm      = (Button)          getActivity().findViewById(R.id.btn_confirm);
        txtWarn         = (TextView)        getActivity().findViewById(R.id.txt_warn);

        imgFrom         = (ImageView)getActivity().findViewById(R.id.img_from);
        imgTo           = (ImageView)getActivity().findViewById(R.id.img_to);

        imgFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceFrom.setText("");
                imgFrom.setVisibility(View.GONE);
                autoPlaceFrom.setFocusable(true);
                autoPlaceFrom.setFocusableInTouchMode(true);
                mCallback.passData("from", null);
            }
        });
        imgTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceTo.setText("");
                imgTo.setVisibility(View.GONE);

                autoPlaceTo.setFocusable(true);
                autoPlaceTo.setFocusableInTouchMode(true);
                mCallback.passData("to", null);
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

        edtCarType.setOnClickListener(get_car_type_listener);
        edtHireType.setOnClickListener(get_hire_type_listener);
        edtDateFrom.setOnClickListener(get_date_from_listener);
        edtDateTo.setOnClickListener(get_date_to_listener);
        btnConfirm.setOnClickListener(booking_ticket_listener);
        edtCarSize.setOnClickListener(get_car_size_listener);
        checkOnline();

    }

    private void checkOnline(){
        if (!Utilites.isOnline(getActivity())){
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getActivity())
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
            GetAllDataBooking carData = new GetAllDataBooking(getActivity(), new GetAllDataBooking.onDataReceived() {
                @Override
                public void onReceived(ArrayList<String> carType, ArrayList<String> size, ArrayList<String> hireType) {
                   aHireType = hireType;


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
    private View.OnClickListener get_car_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(aVehicleType.toArray(new CharSequence[aVehicleType.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aVehicleType.get(which);
                            edtCarType.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };
    private View.OnClickListener get_car_size_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn số chỗ")
                    .setSingleChoiceItems(aCarSize.toArray(new CharSequence[aCarSize.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aCarSize.get(which);
                            edtCarSize.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };
    private View.OnClickListener get_hire_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String prevHire = edtHireType.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn hình thức thuê")
                    .setSingleChoiceItems(aHireType.toArray(new CharSequence[aHireType.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aHireType.get(which);
                            edtHireType.setText(type);
                            /*if (type.equals("Sân bay"))
                                showDialogAirport();
                            else {
                                if (prevHire.equals("Sân bay")) {
                                    autoPlaceFrom.setFocusable(true);
                                    autoPlaceFrom.setFocusableInTouchMode(true);
                                    autoPlaceFrom.setSelected(true);

                                    autoPlaceTo.setFocusable(true);
                                    autoPlaceTo.setFocusableInTouchMode(true);

                                    autoPlaceTo.setText("");
                                    autoPlaceFrom.setText("");

                                    llFrom = null;
                                    llTo = null;
                                }


                            }*/
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    /*private void showDialogAirport() {

        final Dialog dialog = new Dialog(mContext);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.air_port_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        Button btnSet           = (Button)  dialog.findViewById(R.id.btn_set);
        Button btnCancel        = (Button)  dialog.findViewById(R.id.btn_cancel);
        Spinner spnAirport      = (Spinner) dialog.findViewById(R.id.spn_airport);
        Spinner spnMoveType     = (Spinner) dialog.findViewById(R.id.spn_move);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line,aAirport);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAirport.setAdapter(adapterCategory);
        spnAirport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                autoPlaceFrom.setText(aAirport.get(index));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final String[] moveArray = getResources().getStringArray(R.array.move_array);
        ArrayAdapter<String> adapterMove = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line, moveArray);
        adapterMove.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMoveType.setAdapter(adapterMove);
        spnMoveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                autoPlaceTo.setText(moveArray[index]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                autoPlaceTo.setSelection(0);
                autoPlaceTo.setFocusable(false);
                autoPlaceTo.setFocusableInTouchMode(false);

                autoPlaceFrom.setSelection(0);
                autoPlaceFrom.setFocusable(false);
                autoPlaceFrom.setFocusableInTouchMode(false);

                imgFrom.setVisibility(View.GONE);
                imgTo.setVisibility(View.GONE);

                getLonLatPlace();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                edtHireType.setText("");
                dialog.dismiss();
                autoPlaceTo.setText("");
                autoPlaceFrom.setText("");
            }
        });
        dialog.show();
    }*/
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
            mCallback.passData("from", llFrom);
            autoPlaceFrom.setSelection(0);

            autoPlaceFrom.setFocusable(false);
            autoPlaceFrom.setFocusableInTouchMode(false);

        }
    };
    private AdapterView.OnItemClickListener mAutocompleteToClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard((Activity) mContext);
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
            mCallback.passData("to", llTo);
            autoPlaceTo.setSelection(0);
            autoPlaceTo.setFocusable(false);
            autoPlaceTo.setFocusableInTouchMode(false);

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
    private View.OnClickListener booking_ticket_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            txtWarn.setVisibility(View.GONE);
            if (!checkRequestParams()) {
                letsBooking();
            }
        }
    };

    /*private void getLonLatPlace() {
        RequestParams params;
        params = new RequestParams();
        params.put("airport", autoPlaceFrom.getText().toString());
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_GET_LONLAT_AIRPORT, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray arrayresult = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arrayresult.length(); i++) {
                        JSONObject result = arrayresult.getJSONObject(i);
                        double lonFrom = result.getDouble("lon1");
                        double latFrom = result.getDouble("lat1");

                        llFrom = new LatLng(latFrom, lonFrom);
                        mCallback.passData("from", llFrom);
                        double lonTo = result.getDouble("lon2");
                        double latTo = result.getDouble("lat2");
                        llTo = new LatLng(lonTo, latTo);
                        mCallback.passData("to", llTo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
*/
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
        params.put("name", preference.getName());
        params.put("phone", preference.getPhone());
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
                String x = new String(responseBody);
                dResult.onResult(x);
                Toast.makeText(getActivity(), "Đặt xe thành công, Vui lòng đợi lái xe gọi đến",Toast.LENGTH_LONG).show();
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
        if (edtHireType.getText().toString().equals("")|| edtHireType.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập hình thức thuê xe");
            requestFocus(txtWarn);
            return true;
        }
        if (edtCarSize.getText().toString().equals("")|| edtCarSize.getText().toString() == null){
            txtWarn.setVisibility(View.VISIBLE);
            txtWarn.setText("Bạn chưa nhập số chỗ");
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
        return false;
    }
    private void requestFocus(View view) {

        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showDateTimeDialog(final TextView txtDate){
        final View dialogView = View.inflate(mContext, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        dialogView.findViewById(R.id.datetimeset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);
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
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public interface DataPassListener{
        public void passData(String location, LatLng data);
    }
    public interface OnDataResult{
        public void onResult(String result);
    }
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (DataPassListener)activity;
            dResult   = (OnDataResult)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DataPassListener");
        }
    }

}
