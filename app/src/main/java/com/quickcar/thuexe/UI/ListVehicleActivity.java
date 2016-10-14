
package com.quickcar.thuexe.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.quickcar.thuexe.Models.CarInforObject;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.BaseService;
import com.quickcar.thuexe.Utilities.Defines;
import com.quickcar.thuexe.Utilities.SharePreference;

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
    private FloatingActionButton buttonFilter;
    private Context mContext;
    private RelativeLayout mDrawer;
    private OnDataPass dataPasser;
    private OnDataMap dataMap;
    private ImageView imgBack;
    private Spinner categorySpinner , carSizeSpinner, carTypeSpinner;
    private AutoCompleteTextView txtcarName;
    private ArrayList<String> arrCarModel, arrCarMade, arrCarSize, arrCarType;
    private LinearLayout layoutSearch;
    private SharePreference preference;
    private String carMade, carName,carSize,carType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list_tab);
        mContext = this;
        preference= new SharePreference(this);
        moveDrawerToTop();
        getDataSearch();
        viewPager                   =   (ViewPager)             findViewById(R.id.viewpager);
        mDrawerLayout               =   (DrawerLayout)          findViewById(R.id.drawer_layout);
        mDrawer                     =   (RelativeLayout)        findViewById(R.id.drawer);
        buttonFilter                =   (FloatingActionButton)  findViewById(R.id.btn_filter);

        categorySpinner             =   (Spinner)               findViewById(R.id.spinner_category);
        txtcarName                  =   (AutoCompleteTextView)  findViewById(R.id.spinner_name);
        carSizeSpinner              =   (Spinner)               findViewById(R.id.spinner_size);
        carTypeSpinner              =   (Spinner)               findViewById(R.id.spinner_type);
        layoutSearch                =   (LinearLayout)          findViewById(R.id.layout_search);

        buttonFilter.setOnClickListener(filter_click_listener);
        Bundle extras = getIntent().getExtras();

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        prepareDataSliding();
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        txtcarName.setText(Defines.FilterInfor.getCarModel());
        txtcarName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requestCarName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getDataSearch() {
        try {
            JSONObject carObject = new JSONObject(preference.getKeySearch());
            carMade       = carObject.getString("hangxe");
            carName       = carObject.getString("tenxe");
            carSize       = carObject.getString("socho");
            carType       = carObject.getString("loaixe");
            Defines.FilterInfor = new CarInforObject();
            Defines.FilterInfor.setCarSize(carSize);
            Defines.FilterInfor.setCarMade(carMade);
            Defines.FilterInfor.setCarType(carType);
            Defines.FilterInfor.setCarModel(carName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                prepareFilterData();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    private void fillFilterData(){

        /// car made
        arrCarMade = new ArrayList<>();
        arrCarMade.add("Tất cả");
        for (int i = 0; i < Defines.CarMade.length; i++)
            arrCarMade.add(Defines.CarMade[i]);

        // car size

        arrCarSize = new ArrayList<>();
        arrCarSize.add("Tất cả");
        for (int i = 0 ; i< getResources().getStringArray(R.array.size_array).length; i++)
            arrCarSize.add(getResources().getStringArray(R.array.size_array)[i]);

        // car type

        arrCarType = new ArrayList<>();
        arrCarType.add("Tất cả");
        for (int i = 0 ; i< getResources().getStringArray(R.array.type_array).length; i++)
            arrCarType.add(getResources().getStringArray(R.array.type_array)[i]);

    }
    private void prepareFilterData() {

        fillFilterData();
        //arrCarModel = new ArrayList<>();


       /* final ArrayAdapter<String> adapterName = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarModel);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carNameSpinner.setAdapter(adapterName);
        carNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null && arrCarModel.size()>0) {
                    Defines.SpinnerSelect.setCarModel(arrCarModel.get(index));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, R.layout.custom_spiner,arrCarMade);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapterCategory);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                if (Defines.FilterInfor != null) {
                    Defines.SpinnerSelect.setCarMade(arrCarMade.get(index));
                    /*if (index >0) {
                        arrCarModel.clear();
                        arrCarModel.add("Tất cả");
                        for (int i = 0; i < Defines.category[index-1].length; i++)
                            arrCarModel.add(Defines.category[index-1][i]);
                        adapterName.notifyDataSetChanged();
                        boolean checkname = false;
                        for (int i = 0; i < arrCarModel.size(); i++) {
                            if (arrCarModel.get(i).equals(Defines.FilterInfor.getCarModel())) {
                                carNameSpinner.setSelection(i);
                                checkname = true;
                            }
                        }
                        if (!checkname)
                            carNameSpinner.setSelection(0);
                    }else{
                        arrCarModel.clear();
                        arrCarModel.add("Tất cả");
                        adapterName.notifyDataSetChanged();
                    }*/

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
                Defines.SpinnerSelect.setCarModel(txtcarName.getText().toString());
                Defines.FilterInfor.setCarType(Defines.SpinnerSelect.getCarType());
                Defines.FilterInfor.setCarMade(Defines.SpinnerSelect.getCarMade());
                Defines.FilterInfor.setCarModel(Defines.SpinnerSelect.getCarModel());
                Defines.FilterInfor.setCarSize(Defines.SpinnerSelect.getCarSize());

                /*txtNoResult.setVisibility(View.GONE);
                requestToGetListVehicle(Defines.FilterInfor.getCarType(),Defines.FilterInfor.getCarMade(), Defines.FilterInfor.getCarModel(), Defines.FilterInfor.getCarSize().substring(0,1));
                vehicles = new ArrayList<>();
                adapter = new ActiveCarAdapter(mContext, vehicles);
                vehicleView.setAdapter(adapter);*/

                dataPasser.onDataPass();
                dataMap.OnDataMap();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    private void requestCarName(String s) {
        arrCarModel = new ArrayList<>();
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
                        arrCarModel.add(name);
                    }
                    ArrayAdapter<String> adapterProvinceFrom = new ArrayAdapter<>(mContext,android.R.layout.simple_list_item_1, arrCarModel);
                    txtcarName.setAdapter(adapterProvinceFrom);
                    txtcarName.setThreshold(1);
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
    // Container Activity must implement this interface
    public interface OnDataPass {
        public void onDataPass();
    }
    public interface OnDataMap {
        public void OnDataMap();
    }
}