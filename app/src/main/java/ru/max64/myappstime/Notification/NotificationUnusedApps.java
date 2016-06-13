package ru.max64.myappstime.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import ru.max64.myappstime.Data.NotificationData;
import ru.max64.myappstime.MainActivity;
import ru.max64.myappstime.R;
import ru.max64.myappstime.Utilities.Constants;


/**
 * Created by aditya on 27/07/15.
 */
public class NotificationUnusedApps extends UsageNotification {

    private Context mContext;

    private final String TITLE = "Usage";
    private final String TEXT = "Unused apps detected. Uninstall these to reclaim memory";

    private NotificationUnusedApps() {

    }

    public NotificationUnusedApps(NotificationData data, Context con) {
        this.mContext = con;
    }

    @Override
    public NotificationCompat.Builder getNotificationBuilder() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.sym_def_app_icon)
                        .setContentTitle(TITLE)
                        .setContentText(TEXT);

        Intent i = new Intent(mContext, MainActivity.class);
        i.putExtra(Constants.INTENT_KEY, Constants.NOTIFICATION_FUTILE_APPS);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);

        return mBuilder;
    }
}
