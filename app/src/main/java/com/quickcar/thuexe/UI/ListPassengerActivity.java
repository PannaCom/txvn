package com.quickcar.thuexe.UI;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Utilities.Utilites;
import com.quickcar.thuexe.Widget.GPSTracker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListPassengerActivity extends AppCompatActivity {

    private SharePreference preference;
    private ImageView imgMenu, btnBack;
    private Context mContext;
    private ProgressDialog dialog;
    private OnDataMap dataMap;
    private OnDataPass dataPasser;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean doubleBackToExitPressedOnce = false;
    private int[] tabIcons = {
            R.mipmap.roster,
            R.mipmap.maps

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passenger);
        preference = new SharePreference(this);
        mContext = this;
        imgMenu     = (ImageView)   findViewById(R.id.img_menu);
        btnBack     =  (ImageView)  findViewById(R.id.img_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doubleBackToExitPressedOnce) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext, getResources().getString(R.string.notice_close_app), Toast.LENGTH_SHORT).show();
                    doubleBackToExitPressedOnce = true;
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;
                        }
                    }, 2000);
                }
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ListPassengerActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.intro_menu_owner, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit_user:
                                Intent intent = new Intent(ListPassengerActivity.this, EditOwnerActivity.class);
                                startActivity(intent);
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
        if (!preference.getRegisterToken()) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            registerToken(preference.getToken());
        }
        viewPager                   =   (ViewPager)             findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListPassengerBookingFragment(), "Hành khách");
        adapter.addFrag(new ListCarAroundFragment(), "Xe hoạt động");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



    private void registerToken(String token) {

        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        params.put("regid",  token);
        params.put("os", 1);
        Log.i("params deleteDelivery", params.toString());
        BaseService.getHttpClient().post(Defines.URL_REGISTER_TOKEN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result == 1)
                    preference.saveRegisterToken(true);
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
    private void showDialogShareSocial() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ");
        String shareMessage = "Bạn cần thuê xe hay bạn là tài xế/nhà xe/hãng xe có xe riêng, hãy dùng thử ứng dụng thuê xe  trên di động tại http://thuexevn.com";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Chọn phương thức để chia sẻ"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GPSTracker gps = new GPSTracker(mContext);
        if (!gps.canGetLocation()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Thông báo");  // GPS not found
            builder.setMessage("Chức năng này cần lấy vị trí hiện tại của bạn.Bạn có muốn bật định vị?"); // Want to enable?
            builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }
        final ProgressDialog locate = new ProgressDialog(mContext);
        locate.setIndeterminate(true);
        locate.setCancelable(false);
        locate.setMessage("Đang lấy vị trí...");
        locate.show();
        if (gps.getLongitude() == 0 && gps.getLatitude() == 0) {
            gps.getLocationCoodinate(new GPSTracker.LocateListener() {
                @Override
                public void onLocate(double mlongitude, double mlatitude) {
                    if (dataMap != null)
                        dataMap.OnDataMap();
                    if (dataPasser != null)
                        dataPasser.onDataPass();
                    locate.dismiss();
                    //Toast.makeText(mContext, mlongitude+","+mlatitude,Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (dataMap != null)
                dataMap.OnDataMap();
            if (dataPasser != null)
                dataPasser.onDataPass();
            //Toast.makeText(mContext, gps.getLongitude()+","+gps.getLatitude(),Toast.LENGTH_SHORT).show();
            locate.dismiss();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (dataMap != null)
                dataMap.OnDataMap();
            if (dataPasser != null)
                dataPasser.onDataPass();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro_menu_owner, menu);
        return true;
    }
    public void updateApi(OnDataMap listener) {
        dataMap = listener;
    }
    public void updateApi(OnDataPass listener) {
        dataPasser = listener;
    }
    public interface OnDataMap {
        public void OnDataMap();
    }
    public interface OnDataPass {
        public void onDataPass();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(mContext, getResources().getString(R.string.notice_close_app), Toast.LENGTH_SHORT).show();
                this.doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }



 /*private boolean checkExpireToken() {
        DateTime lastDay = new DateTime(preference.getDateActive());
        DateTime now = new DateTime();

        int days = Days.daysBetween(lastDay.withTimeAtStartOfDay(), now.withTimeAtStartOfDay()).getDays();

        if (days > preference.getDayExpire()){
            return true;
        }
        return false;
    }

    private void showDialogExpire() {
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.renew_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        final EditText edtCode = (EditText) dialog.findViewById(R.id.edt_code);


        final Button btnActive = (Button) dialog.findViewById(R.id.btn_active);
        btnActive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (!Utilites.isOnline(mContext)){
                    Toast.makeText(mContext,"Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edtCode.getText().toString().equals(""))
                {
                    Toast.makeText(mContext,"Bạn chưa nhập mã", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendActive(edtCode.getText().toString(), btnActive, dialog);
            }
        });
        dialog.show();
    }

    private void sendActive(String code, final Button btnActive, final Dialog dialog ) {
        btnActive.setEnabled(false);
        btnActive.setClickable(false);
        RequestParams params;
        params = new RequestParams();
        params.put("phone", preference.getPhone());
        params.put("code",  code);

        Log.i("params deleteDelivery", params.toString());

        BaseService.getHttpClient().post(Defines.URL_RENEW, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                //parseJsonResult(new String(responseBody));
                int result = Integer.valueOf(new String(responseBody));
                if (result > 0) {
                    preference.saveDayExpire(result);
                    preference.saveDateActive(new DateTime().toString());
                    removeAllMarker();
                    getCurrentLocation();
                    getCarAround();
                    Toast.makeText(mContext, "Tài khoản gia hạn thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else
                    Toast.makeText(mContext, "Tài khoản gia hạn thất bại", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnActive.setEnabled(true);
                        btnActive.setClickable(true);
                    }
                },2000);

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
*/

}

