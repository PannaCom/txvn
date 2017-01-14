
package com.quickcar.thuexe.UI;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Controller.CarTypesAdapter;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.GetAllCarData;
import com.quickcar.thuexe.Utilities.SharePreference;
import com.quickcar.thuexe.Widget.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListVehicleActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.roster,
            R.mipmap.maps
    };
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton buttonFilter, btnBookNow;
    private Context mContext;
    private RelativeLayout mDrawer;
    private OnDataPass dataPasser;
    private OnDataMap dataMap;
    private ImageView imgBack, imgMenu;
    private Spinner categorySpinner , carSizeSpinner, carTypeSpinner,carModelSpinner;
    private ArrayList<String> arrCarModel, arrCarMade, arrCarSize, arrCarType, arrayPrice;
    private LinearLayout layoutSearch;
    private SharePreference preference;
    private String carMade, carName,carSize,carType;
    private RadioGroup radioOrderGroup;
    private RadioButton radioOrderButton;
    private boolean doubleBackToExitPressedOnce = false;
    private RecyclerView lvCarTypes;
    private CarTypesAdapter adapterImg;
    private ProgressDialog dialog;
    private boolean isFiltered = false, isDrawerOpen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list_tab);
        mContext = this;
        preference= new SharePreference(this);
        moveDrawerToTop();
        viewPager                   =   (ViewPager)             findViewById(R.id.viewpager);
        mDrawerLayout               =   (DrawerLayout)          findViewById(R.id.drawer_layout);
        mDrawer                     =   (RelativeLayout)        findViewById(R.id.drawer);
        buttonFilter                =   (FloatingActionButton)  findViewById(R.id.btn_filter);
        btnBookNow                  =   (FloatingActionButton)  findViewById(R.id.btn_book_now);

        categorySpinner             =   (Spinner)               findViewById(R.id.spinner_category);
        carModelSpinner             =   (Spinner)               findViewById(R.id.spinner_car_name);
        carSizeSpinner              =   (Spinner)               findViewById(R.id.spinner_size);
        carTypeSpinner              =   (Spinner)               findViewById(R.id.spinner_type);
        radioOrderGroup             =   (RadioGroup)            findViewById(R.id.radio_order);
        layoutSearch                =   (LinearLayout)          findViewById(R.id.layout_search);
        lvCarTypes                  =   (RecyclerView)          findViewById(R.id.lv_car_type);

        buttonFilter.setOnClickListener(filter_click_listener);
        btnBookNow.setOnClickListener(booking_now_click_listener);
        //Bundle extras = getIntent().getExtras();

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        imgBack = (ImageView) findViewById(R.id.img_back);
        imgMenu = (ImageView) findViewById(R.id.img_menu);
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
                        switch (item.getItemId()){
                            case R.id.switch_user:
                                showDialogSwitchUser();
                                return true;
                            case R.id.share_social:
                                showDialogShareSocial();
                                return true;
                            case R.id.rental_vehice:
                                Intent intent = new Intent(mContext, PassengerListRentalActivity.class);
                                startActivity(intent);
                        }
                        return false;
                    }
                });
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
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
        getDataSearch();
        GetAllCarData carData = new GetAllCarData(this, new GetAllCarData.onDataReceived() {
            @Override
            public void onReceived(ArrayList<String> categories, ArrayList<String> types, ArrayList<String> size) {
                arrCarMade = categories;
                arrCarType = types;
                arrCarMade.add(0,"Tất cả");
                arrCarType.add(0,"Tất cả");
                arrCarSize = new ArrayList<>();
                arrCarSize.add("Tất cả");
                for (String item : size) {
                    if (item.equals("4")){
                        arrCarSize.add(item + " chỗ(giá siêu rẻ, không cốp)");
                    }else  if (item.equals("5")){
                        arrCarSize.add(item + " chỗ(có cốp)");
                    }else
                        arrCarSize.add(item + " chỗ");
                }
                adapterImg = new CarTypesAdapter(mContext, arrCarType, lvCarTypes);
                lvCarTypes.setAdapter(adapterImg);
                lvCarTypes.setHasFixedSize(true);
                //Set RecyclerView type according to intent value
                lvCarTypes.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                adapterImg.setOnItemClickListener(new CarTypesAdapter.onClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Defines.SpinnerSelect.setCarType(arrCarType.get(position));
                        Defines.FilterInfor.setCarType(arrCarType.get(position));
                        Defines.FilterInfor.setCarModel(Defines.SpinnerSelect.getCarModel());
                        Defines.FilterInfor.setCarType(Defines.SpinnerSelect.getCarType());
                        Defines.FilterInfor.setCarMade(Defines.SpinnerSelect.getCarMade());
                        Defines.FilterInfor.setCarModel(Defines.SpinnerSelect.getCarModel());
                        Defines.FilterInfor.setCarSize(Defines.SpinnerSelect.getCarSize());
                        dataPasser.onDataPass();
                        dataMap.OnDataMap();

                    }
                });
                prepareDataSliding();

            }

        });



    }

    /*private void registerToken(String token) {

        RequestParams params;
        params = new RequestParams();
        params.put("tobject", preference.getRole());
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
    }*/
    private void showDialogShareSocial() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ");
        String shareMessage = "Bạn cần thuê xe hay bạn là tài xế/nhà xe/hãng xe có xe riêng, hãy dùng thử ứng dụng thuê xe  trên di động tại http://thuexevn.com";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Chọn phương thức để chia sẻ"));
    }


    private void getDataSearch() {
    //try {
        /*JSONObject carObject = new JSONObject(preference.getKeySearch());
        carMade       = carObject.getString("hangxe");
        carName       = carObject.getString("tenxe");
        carSize       = carObject.getString("socho");
        carType       = carObject.getString("loaixe");*/
        Defines.FilterInfor = new CarInforObject();
        Defines.FilterInfor.setCarSize("Tất cả");
        Defines.FilterInfor.setCarMade("Tất cả");
        Defines.FilterInfor.setCarType("Tất cả");
        Defines.FilterInfor.setCarModel("Tất cả");

        arrCarModel = new ArrayList<>();
        arrCarModel.add("Tất cả");

   /* } catch (JSONException e) {
        e.printStackTrace();
    }*/
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment carList = new CarListFragment();
        Bundle data = new Bundle();//Use bundle to pass data
        data.putString(Defines.CAR_TYPE_FROM_ACTION, carType);//put string, int, etc in bundle with a key value
        data.putString(Defines.CAR_MADE_TO_ACTION, carMade);//put string, int, etc in bundle with a key value
        data.putString(Defines.CAR_SIZE_ACTION, carSize);//put string, int, etc in bundle with a key value
        carList.setArguments(data);//Finally set argument bundle to fragment

        adapter.addFrag(carList, "Danh sách");
        adapter.addFrag(new MapCarActiveFragment(), "Bản đồ");
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
    private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DrawerLayout drawer = (DrawerLayout) inflater.inflate(R.layout.decor, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawer.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        //      drawer.findViewById(R.id.drawer).setPadding((int) Utilites.convertDpToPixel(10, this), 0, (int) Utilites.convertDpToPixel(10, this), Utilites.getStatusBarHeight(this));

        // Make the drawer replace the first child
        decor.addView(drawer);
    }
    public void updateApi(OnDataPass listener) {
        dataPasser = listener;
    }
    public void updateApi(OnDataMap listener) {
        dataMap = listener;
    }
    private void prepareDataSliding() {
        prepareFilterData();
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpen = true;
                prepareFilterData();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    private void prepareFilterData() {
        final ArrayAdapter<String> adapterName = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarModel);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carModelSpinner.setAdapter(adapterName);
        carModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null && arrCarModel.size()>0) {
                    Defines.SpinnerSelect.setCarModel(arrCarModel.get(index));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 0 ; i< arrCarModel.size(); i++){
            if (arrCarModel.get(i).equals(Defines.FilterInfor.getCarModel()))
                carModelSpinner.setSelection(i);
        }
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarMade);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapterCategory);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null) {
                    Defines.SpinnerSelect.setCarMade(arrCarMade.get(index));
                    if (index >0) {
                        requestCarName(arrCarMade.get(index), adapterName);
                        boolean checkname = false;
                        for (int i = 0; i < arrCarModel.size(); i++) {
                            if (arrCarModel.get(i).equals(Defines.FilterInfor.getCarModel())) {
                                checkname = true;
                            }
                        }
                        if (!checkname)
                            carModelSpinner.setSelection(0);
                    }else{
                        if (isDrawerOpen)
                            isDrawerOpen = false;
                        arrCarModel.clear();
                        arrCarModel.add("Tất cả");
                        adapterName.notifyDataSetChanged();
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 0 ; i< arrCarMade.size(); i++){
            if (arrCarMade.get(i).equals(Defines.FilterInfor.getCarMade()))
                categorySpinner.setSelection(i);
        }

        ArrayAdapter<String> adapterSize = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarSize);
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carSizeSpinner.setAdapter(adapterSize);
        carSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null) {
                    Defines.SpinnerSelect.setCarSize(arrCarSize.get(index));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 0 ; i< arrCarSize.size(); i++){
            if (arrCarSize.get(i).equals(Defines.FilterInfor.getCarSize()))
                carSizeSpinner.setSelection(i);
        }
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarType);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carTypeSpinner.setAdapter(adapterType);
        carTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null) {
                    Defines.SpinnerSelect.setCarType(arrCarType.get(index));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 0 ; i< arrCarType.size(); i++){
            if (arrCarType.get(i).equals(Defines.FilterInfor.getCarType()))
                carTypeSpinner.setSelection(i);
        }

        layoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Defines.FilterInfor.setCarModel(Defines.SpinnerSelect.getCarModel());
                Defines.FilterInfor.setCarType(Defines.SpinnerSelect.getCarType());
                Defines.FilterInfor.setCarMade(Defines.SpinnerSelect.getCarMade());
                Defines.FilterInfor.setCarModel(Defines.SpinnerSelect.getCarModel());
                Defines.FilterInfor.setCarSize(Defines.SpinnerSelect.getCarSize());

                /*txtNoResult.setVisibility(View.GONE);
                requestToGetListVehicle(Defines.FilterInfor.getCarType(),Defines.FilterInfor.getCarMade(), Defines.FilterInfor.getCarModel(), Defines.FilterInfor.getCarSize().substring(0,1));
                vehicles = new ArrayList<>();
                adapter = new ActiveCarAdapter(mContext, vehicles);
                vehicleView.setAdapter(adapter);*/
                // get selected radio button from radioGroup
                int selectedId = radioOrderGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioOrderButton = (RadioButton) findViewById(selectedId);

                if (radioOrderButton.getId() == R.id.radio_distance)
                    Defines.FILTER_ORDER = 0;
                if (radioOrderButton.getId() == R.id.radio_price)
                    Defines.FILTER_ORDER = 1;
                dataPasser.onDataPass();
                dataMap.OnDataMap();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                adapterImg.setSelectedPostion(Defines.SpinnerSelect.getCarType());
                adapterImg.notifyDataSetChanged();
                isFiltered = true;
            }
        });
    }

    private void requestCarName(String s, final ArrayAdapter<String> adapterName) {
        if (isFiltered && isDrawerOpen)
        {
            isDrawerOpen = false;
            return;
        }
        arrCarModel.clear();
        arrCarModel.add("Tất cả");
        RequestParams params;
        params = new RequestParams();
        params.put("keyword",s);
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
                        arrCarModel.add(name);
                    }
                    adapterName.notifyDataSetChanged();
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
    private View.OnClickListener filter_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            //buttonFilter.setImageResource(R.drawable.filter);
        }
    };

    private View.OnClickListener booking_now_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (preference.getName().equals("")) {
                Intent intent = new Intent(mContext, GetInforPassengerActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(mContext, BookingNowActivity.class);
                startActivity(intent);
            }
        }
    };
    // Container Activity must implement this interface
    public interface OnDataPass {
        public void onDataPass();
    }
    public interface OnDataMap {
        public void OnDataMap();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(Gravity.START) ) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            else {
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
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            finish();
        }

        return super.onKeyDown(keyCode, event);
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
                    if (dataMap != null)
                        dataMap.OnDataMap();
                    if (dataPasser != null)
                        dataPasser.onDataPass();
                    locate.dismiss();
                    //Toast.makeText(mContext, mlongitude+","+mlatitude,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            if (dataMap != null)
                dataMap.OnDataMap();
            if (dataPasser != null)
                dataPasser.onDataPass();
            //Toast.makeText(mContext, gps.getLongitude()+","+gps.getLatitude(),Toast.LENGTH_SHORT).show();
            locate.dismiss();
        }

    }
}
