package com.quickcar.thuexe.UI;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.quickcar.thuexe.Controller.PlaceArrayAdapter;
import com.quickcar.thuexe.R;
import com.quickcar.thuexe.Utilities.SharePreference;

import java.util.ArrayList;
import java.util.List;

public class BookingNowActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,BookingFormFragment.DataPassListener, BookingFormFragment.OnDataResult {
    private static final String LOG_TAG = "PassengerBookingActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private OnConnected connected;
    private OnDataMap onMap;
    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView btnBack, btnList;
    private int[] tabIcons = {
            R.mipmap.form,
            R.mipmap.maps
    };
    private SharePreference preference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_now);
        mContext = this;
        preference = new SharePreference(this);
        initComponents();
    }


    private void initComponents() {

        viewPager       =   (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        // init google api
        mGoogleApiClient = new GoogleApiClient.Builder(BookingNowActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);

        btnBack = (ImageView)   findViewById(R.id.img_back);
        btnList = (ImageView)   findViewById(R.id.img_option);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preference.getRole() == 1){
                    Intent intent = new Intent(mContext, ListPassengerActivity.class);
                    startActivity(intent);
                    finish();
                }else
                    finish();
            }
        });
        if (preference.getRole() == 1){
            btnList.setVisibility(View.GONE);
        }else {
            btnList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.passenger_menu, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.list_hire:
                                    Intent intent = new Intent(mContext, ListPassengerHireActivity.class);
                                    startActivity(intent);
                                    return true;
                            }
                            return false;
                        }
                    });
                }
            });
        }
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment carList = new BookingFormFragment();


        adapter.addFrag(carList, "Biểu mẫu");
        adapter.addFrag(new MapBookingFragment(), "Bản đồ");
        viewPager.setAdapter(adapter);
    }
    public void updateApi(OnConnected listener) {
        connected = listener;
    }
    public void updateMap( OnDataMap listener) {
        onMap = listener;
    }



    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        //Log.i(LOG_TAG, "Google Places API connected.");
        if (connected != null)
            connected.onConnected(mGoogleApiClient, mPlaceArrayFromAdapter, mPlaceToArrayAdapter);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayFromAdapter.setGoogleApiClient(null);
        mPlaceToArrayAdapter.setGoogleApiClient(null);
        //Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void passData(String location, LatLng data) {
        if (onMap != null)
            onMap.OnDataMap(location, data);
    }

    @Override
    public void onResult(String result) {
        if (preference.getRole() == 1){
            Intent intent = new Intent(mContext, ListPassengerActivity.class);
            startActivity(intent);
            finish();
        }else
            finish();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (preference.getRole() == 1) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent intent = new Intent(mContext, ListPassengerActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public interface OnConnected {
        public void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo);
    }
    public interface OnDataMap {
        public void OnDataMap(String location, LatLng latLng);
    }
}
