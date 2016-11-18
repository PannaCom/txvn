package com.quickcar.thuexe.UI;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.GetAllCarData;
import com.quickcar.thuexe.Utilities.SharePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class EditOwnerActivity extends AppCompatActivity {
    private AutoCompleteTextView txtCarName;
    private ArrayList<String> aCategory, placeFrom, placeTo, aTimes, aReceive, aVehicleType, aName;
    private FrameLayout layoutBienSo, layoutCategory, layoutName, layoutPhone, layoutPrice,layoutCarName,layoutType, layoutSize, layoutProduceYear;
    private ImageView imgBack;
    private EditText txtName, txtTelephone, txtBienSo;
    private TextView txtType, txtCategory, txtSize, txtProduceYear,txtPrice;
    private TextView errBienSo, errCategory, errName, errPhone, errPrice,errCarName,errType, errSize, errProduceYear;;
    private ProgressDialog dialog;
    private FrameLayout toolbar;
    private Context mContext;
    private Button btnRegister;
    private int carPossition = 0;
    private SharePreference preference;
    private int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);
        mContext = this;
        preference = new SharePreference(this);
        initComponents();
        getCarInfor();
        /*for (int i=0; i< Defines.CarMade.length;i++)
            if (Defines.CarMade[i].equals(txtCategory.getText())) {
                ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Defines.category[i]);
                txtCarName.setAdapter(adapterProvinceFrom);
                txtCarName.setThreshold(1);
            }*/

    }

    private void getCarInfor() {
        try {
            JSONObject carObject = new JSONObject(preference.getCarInfor());
            String hoten        = carObject.getString("hoten");
            String sodienthoai  = carObject.getString("sodienthoai");
            String bienso       = carObject.getString("bienso");
            String hangxe       = carObject.getString("hangxe");
            String tenxe        = carObject.getString("tenxe");
            String socho        = carObject.getString("socho");
            String loaixe       = carObject.getString("loaixe");
            String namxe        = carObject.getString("namxe");
            price               = carObject.getInt("gia");

            txtName.setText(hoten);
            txtTelephone.setText(sodienthoai);
            txtBienSo.setText(bienso);
            txtCategory.setText(hangxe);
            txtCarName.setText(tenxe);
            txtSize.setText(socho);
            txtType.setText(loaixe);
            txtProduceYear.setText(namxe);
            if (price== -1) {
                txtPrice.setText("Thỏa thuận");
            }else {
                txtPrice.setText(price + " đ/km");
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
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



        imgBack             = (ImageView)               findViewById(R.id.img_back);

        txtName             = (EditText)                findViewById(R.id.txt_name);
        txtTelephone        = (EditText)                findViewById(R.id.txt_telephone);
        txtBienSo           = (EditText)                findViewById(R.id.txt_bien_so);
        txtPrice            = (TextView)                findViewById(R.id.edt_price);

        txtType             = (TextView)                findViewById(R.id.edt_vehicle_type);
        txtCategory         = (TextView)                findViewById(R.id.txt_category);
        txtSize             = (TextView)                findViewById(R.id.edt_size);
        txtProduceYear      = (TextView)                findViewById(R.id.edt_produce_year);

        txtCarName          = (AutoCompleteTextView)    findViewById(R.id.txt_car_name);
        txtCarName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtCategory.getText().toString().equals("")&& count >0)
                    showDialogCarMade();

            }

        });

        errName             = (TextView)             findViewById(R.id.txt_name_error);
        errPhone            = (TextView)             findViewById(R.id.txt_telephone_error);
        errBienSo           = (TextView)             findViewById(R.id.txt_bien_so_error);
        errCategory         = (TextView)             findViewById(R.id.txt_category_error);
        errCarName          = (TextView)             findViewById(R.id.txt_car_name_error);
        errSize             = (TextView)             findViewById(R.id.txt_size_error);
        errType             = (TextView)             findViewById(R.id.txt_vehicle_type_error);
        errProduceYear      = (TextView)             findViewById(R.id.txt_produce_year_error);
        errPrice            = (TextView)             findViewById(R.id.txt_price_error);

        btnRegister         = (Button)                  findViewById(R.id.btn_register);
        toolbar             = (FrameLayout)                 findViewById(R.id.toolbar);





        layoutName.setOnClickListener(click_to_name_listener);
        layoutPhone.setOnClickListener(click_to_phone_listener);
        txtTelephone.setOnClickListener(click_to_phone_listener);
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
                    editVehicle();
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetAllCarData carData = new GetAllCarData(this, new GetAllCarData.onDataReceived() {
            @Override
            public void onReceived(ArrayList<String> categories, ArrayList<String> types) {
                aCategory = categories;
                aVehicleType = types;
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

    private void editVehicle() {
        RequestParams params;
        params = new RequestParams();
        params.put("id", preference.getDriverId());
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
                    Toast.makeText(mContext, "Sửa thông tin thành công", Toast.LENGTH_SHORT).show();
                    preference = new SharePreference(mContext);
                    preference.savePhone(txtTelephone.getText().toString());
                    preference.saveLicense(txtBienSo.getText().toString());
                    saveVehicleInfor();
                    Intent intent = new Intent(mContext, ListPassengerActivity.class);
                    startActivity(intent);;
                    finish();
                }else
                    Toast.makeText(mContext, "Sửa thông tin thất bại", Toast.LENGTH_SHORT).show();
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
                    .setSingleChoiceItems(R.array.size_array,-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String size = mContext.getResources().getStringArray(R.array.size_array)[which];
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
            builder.setTitle("Chọn loại xe")
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
            Toast.makeText(mContext,"Bạn không thể thay đổi số điện thoại",Toast.LENGTH_SHORT).show();
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
            requestFocus(txtCarName);
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
                            if (which == 0) {
                                price = -1;
                                txtPrice.setText(sPrice);
                            }else {
                                price = Integer.valueOf(sPrice);
                                txtPrice.setText(price+ " đ/km");
                            }
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
                    ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, aName);
                    txtCarName.setAdapter(adapterProvinceFrom);
                    txtCarName.setThreshold(1);
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
}
