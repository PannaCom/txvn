package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DriverLoginActivity extends AppCompatActivity {
    private SharePreference preference;
    private Context mContext;
    private EditText edtPhone, edtPass;
    private TextInputLayout newPhone, newPass;
    private TextView txtRegister;
    private Button btnLogin;
    private ImageView btnBack;
    private ProgressDialog dialog;
    private ImageView btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        preference = new SharePreference(this);
        mContext = this;
        initComponents();
    }
    private void initComponents() {
        // Toolbar toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        edtPhone                = (EditText)            findViewById(R.id.edt_phone);
        edtPass                 = (EditText)            findViewById(R.id.edt_pass);
        newPhone                = (TextInputLayout)     findViewById(R.id.new_phone);
        newPass                 = (TextInputLayout)     findViewById(R.id.new_pass);
        btnLogin                = (Button)              findViewById(R.id.btn_login);
        txtRegister             = (TextView)            findViewById(R.id.txt_register);
        btnBack                 = (ImageView)           findViewById(R.id.img_back);
        btnMenu             = (ImageView)                  findViewById(R.id.btn_menu);
        txtRegister.setPaintFlags(txtRegister.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        btnLogin.setOnClickListener(login_click_listener);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterDriverActivity.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.intro_menu_login, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.change_role:
                                showDialogSwitchUser();
                                return true;
                            case R.id.share_social:
                                showDialogShareSocial();
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

    }
    private void showDialogShareSocial() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ");
        String shareMessage = "Bạn cần thuê xe hay bạn là tài xế/nhà xe/hãng xe có xe riêng, hãy dùng thử ứng dụng thuê xe  trên di động tại http://thuexevn.com";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Chọn phương thức để chia sẻ"));
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
    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            letsLogin();
        }
    };

    private void letsLogin() {
        String phone = edtPhone.getText().toString();
        String pass = edtPass.getText().toString();
        newPhone.setError("");
        newPass.setError("");
        if (phone == null || phone.equals("")) {
            newPhone.setError("Hãy nhập số điện thoại của bạn");
            requestFocus(edtPhone);
            return;
        }

        if (pass == null || pass.equals("")) {
            newPass.setError("Hãy nhập password của bạn");
            requestFocus(edtPass);
            return;
        }
        requestLogin(phone, pass);
    }

    private void requestLogin(final String phone, String pass) {
        RequestParams params;
        params = new RequestParams();
        params.put("phone", phone);
        params.put("pass",pass);
        Log.i("params deleteDelivery", params.toString());
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
        BaseService.getHttpClient().post(Defines.URL_LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                if (new String(responseBody).equals("")){
                    Toast.makeText(mContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    if (data.length() == 0){
                        Toast.makeText(mContext, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    Toast.makeText(mContext, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Defines.isDriver = true;
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        saveVehicleInfor(jsonobject);
                        Intent intent = new Intent(mContext,ListPassengerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
    private void saveVehicleInfor(JSONObject result) {
        preference.saveRole(1);
        try {

            int id      = result.getInt("id");
            preference.saveDriverId(id);
            String name         = result.getString("name");
            preference.saveName(name);
            String phone        = result.getString("phone");
            preference.savePhone(phone);
            String email        = result.getString("email");
            String carModel     = result.getString("car_model");
            String carMade      = result.getString("car_made");
            String carYear      = result.getString("car_years");
            String carSize      = "";
            String carNumber    = result.getString("car_number");
            String carType      = result.getString("car_type");
            String carPrice     = result.getString("car_price");
            String carAddress   = result.getString("address");
            String carPass      = result.getString("pass");
            double lon            = result.getDouble("lon");
            double lat            = result.getDouble("lat");
            if (result.getString("car_size").equals("4"))
                carSize = result.getString("car_size")+" chỗ(giá siêu rẻ, không cốp)";
            else if (result.getString("car_size").equals("5"))
                carSize = result.getString("car_size")+" chỗ(có cốp)";
            else
                carSize = result.getString("car_size")+" chỗ";
            preference.savePhone(phone);
            preference.saveLicense(carNumber);
            preference.saveLogin();
            preference.saveDriverId(id);
            preference.saveActive(true);
            JSONObject carObject = new JSONObject();
            try {
                carObject.put("hoten", name);
                carObject.put("sodienthoai", phone);
                carObject.put("bienso", carNumber);
                carObject.put("hangxe", carMade);
                carObject.put("tenxe", carModel);
                carObject.put("socho", carSize);
                carObject.put("loaixe", carType);
                carObject.put("namxe", carYear);
                carObject.put("gia", carPrice);
                carObject.put("email", email);
                carObject.put("diachi", carAddress);
                carObject.put("pass", carPass);
                carObject.put("lon", lon);
                carObject.put("lat", lat);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            preference.saveCarInfor(carObject.toString());

            //preference.saveCarNumber(carNumber);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
