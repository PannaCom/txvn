package com.quickcar.thuexe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import com.quickcar.thuexe.Models.PassengerModel;


public class LocalVideoDataSource {
    // Database fields
    private SQLiteDatabase database;
    private LocalVideoDatabase dbHelper;
    public LocalVideoDataSource(Context context) {

        dbHelper = new LocalVideoDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createPassenger(PassengerModel passenger) {
        open();
        ContentValues values = new ContentValues();
        values.put(LocalVideoDatabase.COLUMN_NAME, passenger.getName());
        values.put(LocalVideoDatabase.COLUMN_PHONE, passenger.getPhone());
        values.put(LocalVideoDatabase.COLUMN_UIID, passenger.getUiid());
        values.put(LocalVideoDatabase.COLUMN_NOTE, passenger.getNote());
        if (passenger.isConfirm())
            values.put(LocalVideoDatabase.COLUMN_CONFIRM, 1);
        else
            values.put(LocalVideoDatabase.COLUMN_CONFIRM, 0);
        values.put(LocalVideoDatabase.COLUMN_DRIVER, passenger.getDriverId());
        database.insert(LocalVideoDatabase.TABLE_NAME, null,values);
        close();
    }
    public ArrayList<PassengerModel> getAllPassenger(){
        open();
        ArrayList<PassengerModel> passengerRaw = new ArrayList<PassengerModel>();
        Cursor res =  database.rawQuery( "select * from "+LocalVideoDatabase.TABLE_NAME, null );
        if (res.moveToFirst()) {
            do {
                PassengerModel passenger = new PassengerModel();
                passenger.setName(res.getString(1));
                passenger.setPhone((res.getString(2)));
                passenger.setUiid((res.getString(3)));
                passenger.setNote((res.getString(4)));
                if (res.getInt(5)==1)
                    passenger.setConfirm(true);
                else
                    passenger.setConfirm(false);
                passenger.setDriverId(res.getInt(6));
                passengerRaw.add(passenger);
            } while (res.moveToNext());
        }
        res.close();
        close();
        return passengerRaw;
    }
    public void deletePassenger(String uiid) {
        open();
        database.delete(LocalVideoDatabase.TABLE_NAME, LocalVideoDatabase.COLUMN_UIID + " = ?",
                new String[] { uiid });
        close();
    }
    public int updatePassenger(String uiid) {
        open();
        ContentValues values = new ContentValues();
        values.put(LocalVideoDatabase.COLUMN_CONFIRM, 1);
        // updating row
        int db = database.update(LocalVideoDatabase.TABLE_NAME, values, LocalVideoDatabase.COLUMN_UIID + " = ? ",new String[]{uiid});
        close();
        return db;
    }
}