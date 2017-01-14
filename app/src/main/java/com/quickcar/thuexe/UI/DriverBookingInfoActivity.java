package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;

public class DriverBookingInfoActivity extends AppCompatActivity {


    private EditText edtPass;
    private TextView edtName;
    private TextInputLayout newName, textInputPass;
    private Button btnConfirm;
    private ProgressDialog dialog;
    private TextView txtRegister;
    private RelativeLayout root;
    private SharePreference preference;
    private Context mContext;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_booking_info);
        preference = new SharePreference(this);
        mContext = this;
        initComponents();

    }

    private void initComponents() {
        // Toolbar toolbar         = (Toolbar)             findViewById(R.id.toolbar);
        edtName                 = (TextView)            findViewById(R.id.edt_name);
        edtPass                 = (EditText)            findViewById(R.id.edt_pass);
        textInputPass           = (TextInputLayout)     findViewById(R.id.new_pass);
        btnConfirm              = (Button)              findViewById(R.id.btn_login);
        root                    = (RelativeLayout)      findViewById(R.id.root);
        imgBack                 = (ImageView)           findViewById(R.id.img_back);
        edtName.setText(preference.getName());
        btnConfirm.setOnClickListener(login_click_listener);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ListPassengerActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(mContext, ListPassengerActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private View.OnClickListener login_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            letsLogin();
        }
    };

    private void letsLogin() {
        textInputPass.setError(null);
        String phone = edtPass.getText().toString();

        if (phone == null || phone.equals("")) {
            textInputPass.setError("Hãy nhập số điện thoại của khách");
            requestFocus(edtPass);
            return;
        }
        requestLogin( phone);
    }

    private void requestLogin(final String phone) {
        preference.saveTempPhone(phone);
        Intent intent = new Intent(this, BookingNowActivity.class);
        startActivity(intent);
        finish();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
