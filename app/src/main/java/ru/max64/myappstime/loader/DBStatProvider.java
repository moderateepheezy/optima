package ru.max64.myappstime.loader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.max64.myappstime.db.DBHelper;
import ru.max64.myappstime.model.Period;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.DateTimeUtils;
import ru.max64.myappstime.util.Utils;

public class DBStatProvider implements StatProvider {

    private static final int MAX_COUNT_LIMIT = 500;

    private Context context;
    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    public DBStatProvider(Context context) {
        this.context = context;
    }

    @Override
    public List<StatEntry> loadStats(Period period) {
        return loadStatsWithLimit(period, 0);
    }

    @Override
    public List<StatEntry> loadStatsWithLimit(Period period, int maxCount) {
        List<StatEntry> stats;
        List<String> packagesToClean = new ArrayList<>();
        String limit = null;

        if (maxCount != 0 && maxCount < MAX_COUNT_LIMIT) {
            stats = new ArrayList<>(maxCount);
            limit = String.valueOf(maxCount);
        } else {
            stats = new ArrayList<>();
        }

        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateTimeUtils.getCurrentDate(calendar);

        String[] columns = new String[] { DBHelper.COLUMN_NAME, "SUM(" + DBHelper.COLUMN_TIME + ") as time_sum" };
        String selection = null;
        String[] selectionArgs = null;

        switch (period) {
            case DAY:
                selection = DBHelper.COLUMN_DATE + " = ?";
                selectionArgs = new String[] { currentDate };
                break;
            case YESTERDAY:
                selection = DBHelper.COLUMN_DATE + " = ?";
                selectionArgs = new String[] { DateTimeUtils.getYesterdayDateString(calendar) };
                break;
            case WEEK:
                selection = DBHelper.COLUMN_DATE + " > ?";
                selectionArgs = new String[] { DateTimeUtils.getWeekDateString(calendar) };
                break;
            case YEAR:
                selection = DBHelper.COLUMN_DATE + " > ?";
                selectionArgs = new String[] { DateTimeUtils.getYearDateString(calendar) };
                break;
            default:
                Log.d(Utils.LOG_TAG, "Error: default block in switch(period) in DBStatProvider.loadStats()");
        }

        String groupBy = DBHelper.COLUMN_NAME;
        String orderBy = "time_sum DESC";

        Cursor cursor = db.query(DBHelper.DB_TABLE_STATS, columns, selection, selectionArgs, groupBy, null,
                orderBy, limit);
        PackageManager packageManager = context.getPackageManager();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String packageName = cursor.getString(0);
                int time = cursor.getInt(1);

                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                    StatEntry se = new StatEntry();
                    se.setPackageName(packageName);
                    se.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                    se.setTitle(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                    se.setInstallDate(packageInfo.firstInstallTime);
                    se.setTime(time);
                    stats.add(se);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.d(Utils.LOG_TAG, e.toString());
                    packagesToClean.add(packageName);
                }
            }
            cursor.close(); // TODO move to finally block?
        }

        db.close();
        dbHelper.close();

        if (!packagesToClean.isEmpty()) {
            cleanDB(packagesToClean);
        }

        return stats;
    }

    public static List<StatEntry> loadStatsForApps(Period period, List<String> packageNames) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private void cleanDB(List<String> packageNames) {
        new CleanDBTask().execute(packageNames);
    }

    private class CleanDBTask extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(@SuppressWarnings("unchecked") List<String>... params) {
            Log.d(Utils.LOG_TAG, "Clean DB started, apps count to remove: " + params[0].size());
            removeFromDB(params[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        private void removeFromDB(List<String> packageNames) {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

            for (String packageName : packageNames) {
                String whereClause = DBHelper.COLUMN_NAME + " = ?";
                String[] whereArgs = new String[] { packageName };
                int rowsDeleted = db.delete(DBHelper.DB_TABLE_STATS, whereClause, whereArgs);
                Log.d(Utils.LOG_TAG, "package: " + packageName + ", rows deleted: " + rowsDeleted);
            }

            db.close();
            dbHelper.close();
        }

    }

}
