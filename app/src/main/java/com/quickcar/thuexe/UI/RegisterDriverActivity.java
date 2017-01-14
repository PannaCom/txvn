package com.quickcar.thuexe.UI;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import com.quickcar.thuexe.Controller.PlaceArrayAdapter;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.GetAllCarData;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;

public class RegisterDriverActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private ArrayList<String> aCategory, placeFrom, placeTo, aTimes, aReceive, aVehicleType, aName,aCarSize;
    private FrameLayout layoutBienSo, layoutCategory, layoutName, layoutPhone, layoutPrice,layoutCarName,layoutType, layoutSize, layoutProduceYear, layoutPass, layoutEmail, layoutAddress;
    private ImageView imgCategory, imgSize, imgProduceYear, imgVehicleType;
    private EditText txtName, txtTelephone, txtBienSo, txtPass, txtEmail, txtAddress;
    private TextView txtType, txtCategory, txtSize, txtProduceYear,txtPrice,txtCarName,txtLogin;
    private TextView errBienSo, errCategory, errName, errPhone, errPrice,errCarName,errType, errSize, errProduceYear, errPass, errEmail, errAddress;
    private ProgressDialog dialog;
    private FrameLayout toolbar;
    private Context mContext;

    private Button btnRegister;
    private GetAllCarData carData;
    private SharePreference preference;
    private int price;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private LatLng llFrom;
    private AutoCompleteTextView autoPlace;
    private GoogleApiClient mGoogleApiClient;
    private ImageView btnBack;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);
        mContext = this;
        preference = new SharePreference(this);
        mGoogleApiClient = new GoogleApiClient.Builder(RegisterDriverActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        initComponents();
    }




    private void initComponents() {

        layoutName          = (FrameLayout)             findViewById(R.id.layout_name);
        layoutPhone         = (FrameLayout)             findViewById(R.id.layout_telephone);
        layoutBienSo        = (FrameLayout)             findViewById(R.id.layout_bien_so);
        layoutCategory      = (FrameLayout)             findViewById(R.id.layout_category);
        layoutCarName       = (FrameLayout)             findViewById(R.id.layout_car_name);
        layoutSize          = (FrameLayout)             findViewById(R.id.layout_size);
        layoutType          = (FrameLayout)             findViewById(R.id.layout_vehicle_type);
        layoutProduceYear   = (FrameLayout)             findViewById(R.id.layout_produce_year);
        layoutPrice         = (FrameLayout)             findViewById(R.id.layout_price);
        layoutPass          = (FrameLayout)             findViewById(R.id.layout_pass);
        layoutEmail         = (FrameLayout)             findViewById(R.id.layout_email);
        layoutAddress       = (FrameLayout)             findViewById(R.id.layout_address);


        imgCategory         = (ImageView)               findViewById(R.id.btn_get_category);
        imgSize             = (ImageView)               findViewById(R.id.btn_get_size);
        imgVehicleType      = (ImageView)               findViewById(R.id.btn_get_vehicle_type);
        imgProduceYear      = (ImageView)               findViewById(R.id.btn_get_produce_year);

        txtName             = (EditText)                findViewById(R.id.txt_name);
        txtTelephone        = (EditText)                findViewById(R.id.txt_telephone);
        txtBienSo           = (EditText)                findViewById(R.id.txt_bien_so);
        txtPrice            = (TextView)                findViewById(R.id.edt_price);
        txtPass             = (EditText)                findViewById(R.id.txt_password);
        txtEmail            = (EditText)                findViewById(R.id.txt_email);
        txtAddress          = (EditText)                findViewById(R.id.txt_address);

        txtType             = (TextView)                findViewById(R.id.edt_vehicle_type);
        txtCategory         = (TextView)                findViewById(R.id.txt_category);
        txtSize             = (TextView)                findViewById(R.id.edt_size);
        txtProduceYear      = (TextView)                findViewById(R.id.edt_produce_year);

        txtCarName          = (TextView)                findViewById(R.id.txt_car_name);


        errName             = (TextView)             findViewById(R.id.txt_name_error);
        errPhone            = (TextView)             findViewById(R.id.txt_telephone_error);
        errBienSo           = (TextView)             findViewById(R.id.txt_bien_so_error);
        errCategory         = (TextView)             findViewById(R.id.txt_category_error);
        errCarName          = (TextView)             findViewById(R.id.txt_car_name_error);
        errSize             = (TextView)             findViewById(R.id.txt_size_error);
        errType             = (TextView)             findViewById(R.id.txt_vehicle_type_error);
        errProduceYear      = (TextView)             findViewById(R.id.txt_produce_year_error);
        errPrice            = (TextView)             findViewById(R.id.txt_price_error);
        errPass             = (TextView)             findViewById(R.id.txt_pass_error);
        errEmail            = (TextView)             findViewById(R.id.txt_email_error);
        errAddress          = (TextView)             findViewById(R.id.txt_address_error);
        txtLogin            = (TextView)             findViewById(R.id.txt_register);
        autoPlace           = (AutoCompleteTextView) findViewById(R.id.txt_address);
        btnBack             = (ImageView)            findViewById(R.id.img_back);
        btnRegister         = (Button)                  findViewById(R.id.btn_register);


        toolbar             = (FrameLayout)                 findViewById(R.id.toolbar);

        txtLogin.setPaintFlags(txtLogin.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        mPlaceArrayAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
        autoPlace.setThreshold(1);
        autoPlace.setOnItemClickListener(mAutocompleteClickListener);
        autoPlace.setAdapter(mPlaceArrayAdapter);


        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        txtCarName.setOnKeyListener(onSoftKeyboardDonePress);
        layoutName.setOnClickListener(click_to_name_listener);
        layoutPhone.setOnClickListener(click_to_phone_listener);
        layoutBienSo.setOnClickListener(click_to_bien_so_listener);
        layoutCategory.setOnClickListener(click_to_category_listener);
        layoutCarName.setOnClickListener(click_to_car_name_listener);
        layoutSize.setOnClickListener(click_to_size_listener);
        layoutType.setOnClickListener(click_to_type_listener);
        layoutProduceYear.setOnClickListener(click_to_produce_year_listener);
        layoutPrice.setOnClickListener(click_to_price_listener);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllError();
                if (!checkParamsNull()) {
                    registerNewVehicle();
                }
            }
        });
        checkOnline();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void showDialogCarMade() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Thông báo")
                .setMessage("Bạn phải nhập hãng xe trước")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        txtCarName.setText("");
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
            carData = new GetAllCarData(this, new GetAllCarData.onDataReceived() {
                @Override
                public void onReceived(ArrayList<String> categories, ArrayList<String> types, ArrayList<String> size) {
                    aCategory = categories;
                    aVehicleType = types;
                    aCarSize = new ArrayList<>();
                    for (String item : size) {
                        if (item.equals("4")){
                            aCarSize.add(item + " chỗ(giá siêu rẻ, không cốp)");
                        }else  if (item.equals("5")){
                            aCarSize.add(item + " chỗ(có cốp)");
                        }else
                            aCarSize.add(item + " chỗ");
                    }
                }});
        }
    }
    private void hideAllError() {

        errName.setVisibility(View.INVISIBLE);
        errPhone.setVisibility(View.INVISIBLE);
        errBienSo.setVisibility(View.INVISIBLE);
        errCategory.setVisibility(View.INVISIBLE);
        errCarName.setVisibility(View.INVISIBLE);
        errSize.setVisibility(View.INVISIBLE);
        errType.setVisibility(View.INVISIBLE);
        errProduceYear.setVisibility(View.INVISIBLE);
        errPrice.setVisibility(View.INVISIBLE);

    }

    private boolean checkParamsNull() {
        if (txtName.getText().toString().equals("")|| txtName.getText().toString() == null){
            requestFocus(txtName);
            errName.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtTelephone.getText().toString().equals("")|| txtTelephone.getText().toString() == null){
            requestFocus(txtTelephone);
            errPhone.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtPass.getText().toString().equals("")|| txtPass.getText().toString() == null){
            requestFocus(txtPass);
            errPass.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtEmail.getText().toString().equals("")|| txtEmail.getText().toString() == null){
            requestFocus(txtEmail);
            errEmail.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtAddress.getText().toString().equals("")|| txtAddress.getText().toString() == null){
            requestFocus(txtAddress);
            errAddress.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtBienSo.getText().toString().equals("")|| txtBienSo.getText().toString() == null){
            requestFocus(txtBienSo);
            errBienSo.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtCategory.getText().toString().equals("")|| txtCategory.getText().toString() == null){
            requestFocus(txtCategory);
            errCategory.setVisibility(View.VISIBLE);
            return true;
        }

        if (txtCarName.getText().toString().equals("")|| txtCarName.getText().toString() == null){
            requestFocus(txtCarName);
            errCarName.setVisibility(View.VISIBLE);
            return true;
        }

        if (txtSize.getText().toString().equals("")|| txtSize.getText().toString() == null){
            requestFocus(txtSize);
            errSize.setVisibility(View.VISIBLE);
            return true;
        }

        if (txtType.getText().toString().equals("")|| txtType.getText().toString() == null){
            requestFocus(txtType);
            errType.setVisibility(View.VISIBLE);
            return true;
        }

        if (txtProduceYear.getText().toString().equals("")|| txtProduceYear.getText().toString() == null){
            requestFocus(txtProduceYear);
            errProduceYear.setVisibility(View.VISIBLE);
            return true;
        }
        if (txtPrice.getText().toString().equals("")|| txtPrice.getText().toString() == null){
            requestFocus(txtPrice);
            errPrice.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void registerNewVehicle() {
        RequestParams params;
        params = new RequestParams();
        params.put("name", txtName.getText().toString());
        params.put("phone",  txtTelephone.getText().toString());
        params.put("car_number", txtBienSo.getText().toString());
        params.put("car_made", txtCategory.getText().toString());
        params.put("car_model", txtCarName.getText().toString());
        int size = Integer.valueOf(txtSize.getText().toString().split(" ")[0]);
        params.put("car_size", size);
        params.put("car_type", txtType.getText().toString());
        params.put("car_year", txtProduceYear.getText().toString());
        params.put("car_price", price);
        params.put("address", txtAddress.getText().toString());
        params.put("email", txtEmail.getText().toString());
        params.put("lon", llFrom.longitude);
        params.put("lat", llFrom.latitude);
        params.put("pass", txtPass.getText().toString());
        params.put("regId", preference.getToken());
        Log.i("params deleteDelivery", params.toString());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Đang tải dữ liệu");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        BaseService.getHttpClient().post(Defines.URL_REGISTER_VEHICLE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result != 0) {
                    Defines.isDriver = true;
                    Toast.makeText(mContext, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    preference = new SharePreference(mContext);
                    preference.savePhone(txtTelephone.getText().toString());
                    preference.saveLicense(txtBienSo.getText().toString());
                    preference.saveName(txtName.getText().toString());
                    preference.saveLogin();
                    preference.saveDriverId(result);
                    preference.saveActive(true);
                    saveVehicleInfor();
                    Intent intent = new Intent(mContext, ListPassengerActivity.class);
                    startActivity(intent);;
                    finish();
                }else
                    Toast.makeText(mContext, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
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

    private void saveVehicleInfor() {
        JSONObject carObject = new JSONObject();
        try {
            carObject.put("hoten", txtName.getText().toString());
            carObject.put("sodienthoai", txtTelephone.getText().toString());
            carObject.put("bienso", txtBienSo.getText().toString());
            carObject.put("hangxe", txtCategory.getText().toString());
            carObject.put("tenxe", txtCarName.getText().toString());
            carObject.put("socho", txtSize.getText().toString());
            carObject.put("loaixe", txtType.getText().toString());
            carObject.put("namxe", txtProduceYear.getText().toString());
            carObject.put("gia", price);
            carObject.put("email", txtEmail.getText().toString());
            carObject.put("diachi", txtAddress.getText().toString());
            carObject.put("pass", Utilites.md5(txtPass.getText().toString()));
            carObject.put("lon", llFrom.longitude);
            carObject.put("lat", llFrom.latitude);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        preference.saveCarInfor(carObject.toString());

    }


    private View.OnClickListener click_to_produce_year_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            final Dialog dialog = new Dialog(mContext);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.year_picker_dialog);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            Button btnSet = (Button) dialog.findViewById(R.id.btn_set);
            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
            np.setMaxValue(year );
            np.setMinValue(year - 100);
            np.setValue(year - 1);
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            setDividerColor(np, android.R.color.white);
            //setDividerColor(np, android.R.color.white);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                }
            });
            btnSet.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    txtProduceYear.setText(String.valueOf(np.getValue()));
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    private void setDividerColor(NumberPicker picker, int color){
        Field[] pickerField = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerField){
            if (pf.getName().equals("mSelectionDivider")){
                pf.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(color);
                try {
                    pf.set(picker, colorDrawable);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }
    private View.OnClickListener click_to_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(aVehicleType.toArray(new CharSequence[aVehicleType.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type =aVehicleType.get(which);
                            txtType.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private View.OnClickListener click_to_size_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn số chỗ")
                    .setSingleChoiceItems(aCarSize.toArray(new CharSequence[aCarSize.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String size = aCarSize.get(which);
                            txtSize.setText(size);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };


    private View.OnClickListener click_to_category_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn hãng xe")
                    .setSingleChoiceItems(aCategory.toArray(new CharSequence[aCategory.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aCategory.get(which);
                            txtCategory.setText(type);
                            txtCarName.setText("");
                            requestCarName();
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    private View.OnClickListener click_to_name_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestFocus(txtName);
        }
    };


    private View.OnClickListener click_to_phone_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestFocus(txtTelephone);
        }
    };

    private View.OnClickListener click_to_bien_so_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            requestFocus(txtBienSo);
        }
    };

    private View.OnClickListener click_to_car_name_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (txtCategory.getText().toString().equals("")) {
                showDialogCarMade();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn tên xe")
                    .setSingleChoiceItems(aName.toArray(new CharSequence[aName.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = aName.get(which);
                            txtCarName.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };
    private View.OnClickListener click_to_price_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn giá xe")
                    .setSingleChoiceItems(R.array.price_array,-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String sPrice = mContext.getResources().getStringArray(R.array.price_array)[which];
                          /*  if (which == 0) {
                                price = -1;
                                txtPrice.setText(sPrice);
                            }else {
                                price = Integer.valueOf(sPrice);
                                txtPrice.setText(price+ " đ/km");
                            }*/
                            price = Integer.valueOf(sPrice);
                            txtPrice.setText(price+ " đ/km");
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
    // method not working:
    private View.OnKeyListener onSoftKeyboardDonePress=new View.OnKeyListener()
    {
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if (event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION)
            {
                // code to hide the soft keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtCarName.getApplicationWindowToken(), 0);
            }
            return false;
        }
    };


    private void requestCarName() {
        aName = new ArrayList<>();
        RequestParams params;
        params = new RequestParams();
        params.put("keyword", txtCategory.getText().toString());
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_NAME,params, new AsyncHttpResponseHandler() {

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
                        String name = result.getString("name");
                        aName.add(name);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
            }

            @Override
            public void onRetry(int retryNo) {
            }
        });
    }
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard((Activity) mContext);
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            //Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
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
            autoPlace.setSelection(0);

            autoPlace.setFocusable(false);
            autoPlace.setFocusableInTouchMode(false);

        }
    };
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
