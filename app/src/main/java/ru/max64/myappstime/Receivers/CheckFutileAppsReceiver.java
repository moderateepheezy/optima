package ru.max64.myappstime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import ru.max64.myappstime.Data.NotificationData;
import ru.max64.myappstime.Database.DatabaseHelper;
import ru.max64.myappstime.Notification.NotificationFactory;


/**
 * Created by aditya on 05/08/15.
 */
public class CheckFutileAppsReceiver extends BroadcastReceiver {

    private static final String TAG = "CheckFutileAppsReceiver";
    DatabaseHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Notification Alarm ran");

        dbHelper = DatabaseHelper.getInstance(context);
        Cursor cursor = dbHelper.getAppsNotUsedInTime(4 * 86400 * 1000);

        if(0 != cursor.getCount()) {

            NotificationData notificationData = new NotificationData(NotificationFactory.TYPE_UNUSED_APPS);
            NotificationFactory.getInstance(context).shootNotification(notificationData);
        }
    }
}
