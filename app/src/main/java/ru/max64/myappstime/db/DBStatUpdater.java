package ru.max64.myappstime.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.max64.myappstime.util.Utils;

public class DBStatUpdater {

    private Context context;

    public DBStatUpdater(Context context) {
        this.context = context;
    }

    public void update(int counter, String packageName, String date) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String queryDB = "SELECT * FROM " + DBHelper.DB_TABLE_STATS + " WHERE date = ? AND pname = ?";
        Cursor cursor = db.rawQuery(queryDB, new String[]{date, packageName});

        // adding entry with such package name and date
        if (cursor.getCount() == 0) {
            Log.d(Utils.LOG_TAG, "getCount = 0 for date: " + date + " and package: " + packageName);

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_DATE, date);
            cv.put(DBHelper.COLUMN_NAME, packageName);
            cv.put(DBHelper.COLUMN_TIME, counter);

            db.insert(DBHelper.DB_TABLE_STATS, null, cv);
            Log.d(Utils.LOG_TAG, "inserted DB row, date = " + date + " , package = " + packageName + " time = " + counter);
        } else {
            // entry with such package and date exists - update
            Log.d(Utils.LOG_TAG, "getCount not 0 for date: " + date + " and package: " + packageName);

            int oldTime = 0;
            if (cursor.moveToFirst()) {
                oldTime = cursor.getInt(3);
                Log.d(Utils.LOG_TAG, "old time for date: " + date + " and package: " + packageName + " was " + oldTime);
            }

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_NAME, packageName);
            cv.put(DBHelper.COLUMN_TIME, oldTime + counter);

            String where = DBHelper.COLUMN_NAME + " = ?" + " AND " + DBHelper.COLUMN_DATE + " = ?";

            int updCount = db.update(DBHelper.DB_TABLE_STATS, cv, where, new String[]{packageName, date});
            Log.d(Utils.LOG_TAG, "rows updated: " + updCount);
            Log.d(Utils.LOG_TAG, "updated DB row, date: " + date + " and package:" + packageName
                    + " from " + oldTime + " to " + (oldTime + counter));
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }
}
