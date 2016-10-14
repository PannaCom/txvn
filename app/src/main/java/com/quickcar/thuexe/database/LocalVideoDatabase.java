package com.quickcar.thuexe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalVideoDatabase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "db_passenger_name";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_UIID = "uiid";
    public static final String COLUMN_NOTE = "note";
    public static final String COLUMN_CONFIRM = "confirm";
    public static final String COLUMN_DRIVER  = "driverId";
    private static final String DATABASE_NAME = "passenger.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_NAME + "("
            + COLUMN_ID         + " integer primary key autoincrement, "
            + COLUMN_NAME       + " text not null , "
            + COLUMN_PHONE      +" text not null , "
            + COLUMN_UIID       + " text not null , "
            + COLUMN_NOTE       + " text not null , "
            + COLUMN_CONFIRM    + " integer , "
            + COLUMN_DRIVER     + " integer );";

    public LocalVideoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LocalVideoDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}