package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;
import com.quickcar.thuexe.Widget.GPSTracker;

public class SearchActiveBusScreen extends AppCompatActivity{

    private TextView                txtType, txtCategory, txtSize;
    private FrameLayout             layoutType, layoutCategory, layoutSize, layoutCarName;
    private RelativeLayout          root;
    private AutoCompleteTextView    txtCarName;
    private Context                 mContext;
    private Button                  btnSearch;
    private ArrayList<String>       arrCarmade, arrCarname;
    private SharePreference         preference;
    private ProgressDialog dialog;
    private ImageView               imgMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_active_bus_screen);
        mContext = this;
        preference = new SharePreference(this);
        initComponents();
        if (!preference.getKeySearch().equals(""))
        {
            Intent intent = new Intent(mContext, ListVehicleActivity.class);
            startActivity(intent);
            finish();
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        arrCarmade = new ArrayList<>();
        if (Utilites.isOnline(mContext)) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            BaseService.getHttpClient().get(Defines.URL_GET_CAR_MADE,new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    Log.i("JSON", new String(responseBody));
                    dialog.dismiss();
                    try {
                        JSONArray arrayresult = new JSONArray(new String(responseBody));
                        for (int i = 0; i < arrayresult.length(); i++) {
                            JSONObject result = arrayresult.getJSONObject(i);
                            String name = result.getString("name");
                            arrCarmade.add(name);
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
        }else{
           Snackbar snackbar = Snackbar
                    .make(root, "Không có kết nối mạng!", Snackbar.LENGTH_LONG)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getDataFromServer();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    /*
  Initiate all component of activity
  */
    private void initComponents() {
        txtType                     =       (TextView)              findViewById(R.id.edt_vehicle_type);
        txtCategory                 =       (TextView)              findViewById(R.id.txt_category);
        txtSize                     =       (TextView)              findViewById(R.id.edt_size);

        layoutType                  =       (FrameLayout)           findViewById(R.id.layout_vehicle_type);
        layoutCategory              =       (FrameLayout)           findViewById(R.id.layout_category);
        layoutSize                  =       (FrameLayout)           findViewById(R.id.layout_size);

        btnSearch                   =       (Button)                findViewById(R.id.btn_search);

        txtCarName                  =       (AutoCompleteTextView)  findViewById(R.id.txt_car_name);
        imgMenu                     =       (ImageView)             findViewById(R.id.btn_menu);

        root                        =       (RelativeLayout)        findViewById(R.id.root);
        layoutType.setOnClickListener(click_to_type_listener);

        layoutCategory.setOnClickListener(click_to_category_listener);

        layoutSize.setOnClickListener(click_to_size_listener);


        btnSearch.setOnClickListener(search_bus_listener);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.intro_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.switch_user){
                            showDialogSwitchUser();
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        txtCarName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requestCarName(s.toString());
                Log.e("TAG",s.toString());

            }

        });
    }

    private void requestCarName(String s) {
        arrCarname = new ArrayList<>();
        RequestParams params;
        params = new RequestParams();
        params.put("keyword", s);
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
                        arrCarname.add(name);
                    }
                    ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, arrCarname);
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

    private View.OnClickListener click_to_category_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(arrCarmade.toArray(new String[arrCarmade.size()]),-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = arrCarmade.get(which);
                            txtCategory.setText(type);
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



    private View.OnClickListener click_to_type_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Chọn loại xe")
                    .setSingleChoiceItems(R.array.type_array,-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String type = mContext.getResources().getStringArray(R.array.type_array)[which];
                            txtType.setText(type);
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };


    public View.OnClickListener search_bus_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String category       = txtCategory.getText().toString();
            String type           = txtType.getText().toString();
            String size           = txtSize.getText().toString();
            String name           = txtCarName.getText().toString();
            Defines.FilterInfor = new CarInforObject();
            if (size == null || size.equals(""))
                Defines.FilterInfor.setCarSize("Tất cả");
            else
                Defines.FilterInfor.setCarSize(size);

            if (category == null || category.equals(""))
                Defines.FilterInfor.setCarMade("Tất cả");
            else
                Defines.FilterInfor.setCarMade(category);

            if (type == null || type.equals(""))
                Defines.FilterInfor.setCarType("Tất cả");
            else
                Defines.FilterInfor.setCarType(type);

            if (name == null || name.equals(""))
                Defines.FilterInfor.setCarModel("Tất cả");
            else
                Defines.FilterInfor.setCarModel(name);
            GPSTracker gps = new GPSTracker(mContext);
            // check if GPS enabled
            if (!gps.canGetLocation()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Thông báo");  // GPS not found
                builder.setMessage("Chức năng này cần lấy vị trí hiện tại của bạn.Bạn có muốn bật định vị?"); // Want to enable?
                builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }else {
                if (gps.handlePermissionsAndGetLocation()){
                    saveKeySearch();
                    Intent intent = new Intent(mContext, ListVehicleActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
    };
    private void saveKeySearch() {
        JSONObject carObject = new JSONObject();
        try {
            carObject.put("hangxe", txtCategory.getText().toString());
            carObject.put("tenxe", txtCarName.getText().toString());
            carObject.put("socho", txtSize.getText().toString());
            carObject.put("loaixe", txtType.getText().toString());

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        preference.saveKeySearch(carObject.toString());

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS) {
            Intent intent = new Intent(mContext, ListVehicleActivity.class);
            intent.putExtra(Defines.CAR_MADE_TO_ACTION, txtCategory.getText().toString());
            intent.putExtra(Defines.CAR_TYPE_FROM_ACTION, txtType.getText().toString());
            intent.putExtra(Defines.CAR_SIZE_ACTION, txtSize.getText().toString());
            intent.putExtra(Defines.CAR_NAME_ACTION, "");
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro_menu, menu);
        return true;
    }
    private void showDialogSwitchUser() {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Bạn có muốn chọn lại vai trò của mình?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        preference.saveRole(0);
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
