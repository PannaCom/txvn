package com.quickcar.thuexe.UI;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;

public class ActiveAccountActivity extends AppCompatActivity {
    private SharePreference preference;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_account);
        preference = new SharePreference(this);
        mContext = this;
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.active_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText edtCode = (EditText) dialog.findViewById(R.id.edt_code);
        TextView txtRegister = (TextView) dialog.findViewById(R.id.txt_register);



        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NewVehicleActivity.class);
                preference.saveActive(false);
                preference.clearLogin();
                startActivity(intent);;
                finish();
            }
        });
        Button btnSet = (Button) dialog.findViewById(R.id.btn_active);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_resend);
        btnSet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sendActive(edtCode.getText().toString());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                reSend();
            }
        });
        dialog.show();
    }

    private void reSend() {
        RequestParams params;
        params = new RequestParams();
        params.put("idtaixe", preference.getDriverId());

        Log.i("params deleteDelivery", params.toString());

        BaseService.getHttpClient().post(Defines.URL_RESEND, params, new AsyncHttpResponseHandler() {

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
                    Toast.makeText(mContext, "Mã kích hoạt sẽ gửi tới tin nhắn của bạn", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(mContext, "Không tạo được mã kích hoạt", Toast.LENGTH_SHORT).show();

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

    private void sendActive(String code) {
        RequestParams params;
        params = new RequestParams();
        params.put("idtaixe", preference.getDriverId());
        params.put("code",  code);

        Log.i("params deleteDelivery", params.toString());

        BaseService.getHttpClient().post(Defines.URL_ACTIVE, params, new AsyncHttpResponseHandler() {

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
                    Toast.makeText(mContext, "Tài khoản kích hoạt thành công", Toast.LENGTH_SHORT).show();
                    //preference.saveActive(true);
                    Intent intent = new Intent(mContext, ListPassengerActivity.class);
                    startActivity(intent);;
                    finish();
                }else
                    Toast.makeText(mContext, "Tài khoản kích hoạt thất bại", Toast.LENGTH_SHORT).show();

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
}