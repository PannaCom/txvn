package com.quickcar.thuexe.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharePreference {

    private Context activity;
    private String STATUS = "status";
    private String NAME  = "name";
    private String PHONE = "phone";
    private String LOGIN = "login";
    private String LICENSE = "license";
    private String REGISTER_TOKEN = "register token";
    private String CAR_INFOR = "car info";
    private String KEY_SEARCH = "search";
    private String ACTIVE = "active";
    private String DATE_EXPIRE = "date expire";
    private String DRIVER_ID = "driver id";
    private String OWNER_NAME = "owner name";
    private String TYPE = "type";
    private String ROLE = "role";
    private String TOKEN = "token";
    private String DATEACTIVE = "date active";
    // constructor
    public SharePreference(Context activity) {
        this.activity = activity;
    }
    public void saveStatus (int token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STATUS, token);
        editor.apply();
    }
    public int getStatus() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getInt(STATUS, 0);
    }

    public void saveName(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(NAME, name);
        editor.apply();
    }
    public String getName() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(NAME, "");
    }

    public void savePhone(String phone) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE, phone);
        editor.apply();
    }
    public String getPhone() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(PHONE, "");
    }
    public void saveLogin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(LOGIN, true);
        editor.apply();
    }
    public void clearLogin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(LOGIN, false);
        editor.apply();
    }
    public boolean getLogin() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean(LOGIN,false);
    }

    public void saveLicense(String license) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LICENSE, license);
        editor.apply();
    }
    public String getLicense() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(LICENSE,"");
    }


    public void saveRegisterToken(boolean register) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(REGISTER_TOKEN, register);
        editor.apply();
    }
    public boolean getRegisterToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean(REGISTER_TOKEN,false);
    }
    public void saveCarInfor(String place) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CAR_INFOR, place);
        editor.apply();
    }
    public String getCarInfor() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(CAR_INFOR,"");
    }
    public void saveKeySearch(String place) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_SEARCH, place);
        editor.apply();
    }
    public String getKeySearch() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(KEY_SEARCH,"");
    }
    public void saveOwnerName(String name) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(OWNER_NAME, name);
        editor.apply();
    }
    public String getOwnerName() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(OWNER_NAME,"");
    }

    public void saveType(String type) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TYPE, type);
        editor.apply();
    }
    public String getType() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(TYPE,"");
    }

    public void saveActive(boolean active) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ACTIVE, active);
        editor.apply();
    }
    public boolean getActive() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getBoolean(ACTIVE, false);
    }

    public void saveDayExpire(int count) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(DATE_EXPIRE, count);
        editor.apply();
    }
    public int getDayExpire() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getInt(DATE_EXPIRE,0);
    }
    public void saveDriverId(int id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(DRIVER_ID, id);
        editor.apply();
    }
    public int getDriverId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getInt(DRIVER_ID,0);
    }
    public void saveRole(int id) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(ROLE, id);
        editor.apply();
    }
    public int getRole() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getInt(ROLE,0);
    }
    public void saveToken(String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }
    public String getToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(TOKEN,"");
    }

    public void saveDateActive(String date) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(DATEACTIVE, date);
        editor.apply();
    }
    public String getDateActive() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        return sp.getString(DATEACTIVE,"");
    }

}
