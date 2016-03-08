package ru.max64.myappstime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "myappstime.db";

    public static final String DB_TABLE_STATS = "table_stats";
    private static final int DB_VERSION = 1;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME = "pname";
    public static final String COLUMN_TIME = "time";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DB_TABLE_STATS + " (" + COLUMN_ID
                + " integer primary key autoincrement," + COLUMN_DATE
                + " text," + COLUMN_NAME + " text," + COLUMN_TIME + " integer"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
