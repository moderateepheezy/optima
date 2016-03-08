package ru.max64.myappstime.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ru.max64.myappstime.MainActivity;
import ru.max64.myappstime.R;
import ru.max64.myappstime.db.DBStatUpdater;
import ru.max64.myappstime.receiver.ScreenStatusReceiver;
import ru.max64.myappstime.util.DateTimeUtils;
import ru.max64.myappstime.util.Utils;

public class UsageService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final int COUNT_INTERVAL = 2;      // seconds

    private ScreenStatusReceiver screenReceiver;
    private CurrentAppProvider currentAppProvider;
    private DBStatUpdater statUpdater;
    private TimerTask timerTask;
    private Timer timer;

    private AppInfo currentAppInfo = null;
    private AppInfo previousAppInfo = null;

    private boolean run = false;
    private boolean handled = false;
    private int counter = 0;
    private String previousDate = "";
    private String currentDate = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Utils.LOG_TAG, UsageService.class.getSimpleName() + " created");

        currentAppProvider = new CurrentAppProvider(this);
        screenReceiver = new ScreenStatusReceiver();
        statUpdater = new DBStatUpdater(this);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(screenReceiver, intentFilter);
    }

    private void setAsForeground() {
        final Builder builder = new Builder(this);
        builder.setSmallIcon(R.drawable.time);
        builder.setContentTitle("My Apps Time");
        builder.setTicker("ticker");
        builder.setContentText("Timer is run");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Utils.LOG_TAG, "onStartCommand() in " + UsageService.class.getName());

        if (intent != null) {
            boolean userPresent = intent.getBooleanExtra("user_present", true);

            if (userPresent) {
                run = true;
            } else {
                timer.cancel();

                if (!handled && (previousAppInfo != null) && (previousAppInfo.isUserApp())) {
                    handleAppChange(previousAppInfo.getPackageName(), counter);
                }

                handled = true;
                run = false;
            }
        }

        counter = 0;

        if (run) {
            run = false;
            handled = false;

            setAsForeground();

            previousDate = DateTimeUtils.DATE_FORMAT.format(Calendar.getInstance().getTime());
            Log.d(Utils.LOG_TAG, "previousDate: " + previousDate);
            counter = 0;

            timerTask = new TimerTask() {
                @Override
                public void run() {
                    currentAppInfo = currentAppProvider.getAppInfo();
                    Log.d(Utils.LOG_TAG, currentAppInfo.getPackageName() + " time: " + counter);
                    counter += COUNT_INTERVAL;

                    if ((currentAppInfo != null) && (previousAppInfo != null)
                            && (!currentAppInfo.getPackageName().equals(previousAppInfo.getPackageName()))) {
                        Log.d(Utils.LOG_TAG, "App package changed from " + previousAppInfo.getPackageName() + " to " + currentAppInfo.getPackageName());

                        if ((previousAppInfo != null) && (previousAppInfo.isUserApp())) {
                            handleAppChange(previousAppInfo.getPackageName(), counter);
                        }

                        previousAppInfo = currentAppInfo;
                        counter = 0;
                    }

                    if (previousAppInfo == null) {
                        previousAppInfo = currentAppInfo;
                    }
                }
            };

            timer = new Timer();
            timer.schedule(timerTask, 0, COUNT_INTERVAL * 1000);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Utils.LOG_TAG, UsageService.class.getSimpleName() + " onDestroy()");

        timer.cancel();
        unregisterReceiver(screenReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void handleAppChange(String packageName, int counter) {
        long start = System.currentTimeMillis();

        Log.d(Utils.LOG_TAG, "Work on " + packageName + ", time = " + counter + ", pname = " + packageName);

        Calendar calendar = Calendar.getInstance();
        currentDate = DateTimeUtils.DATE_FORMAT.format(calendar.getTime());
        Log.d(Utils.LOG_TAG, "in handleAppChange(): previousDate = " + previousDate + " and currentDate = " + currentDate);

        if (currentDate.equals(previousDate)) {
            statUpdater.update(counter, packageName, currentDate);
        } else {
            Log.d(Utils.LOG_TAG, "currentDate != previousDate");
            Log.d(Utils.LOG_TAG, "counter: " + counter);

            int secondsAfterMidnight = DateTimeUtils.getSecondsAfterMidnight(calendar);
            Log.d(Utils.LOG_TAG, "secondsAfterMidnight: " + secondsAfterMidnight);

            if (counter > secondsAfterMidnight) {
                int secondsBeforeMidnight = counter - secondsAfterMidnight;
                Log.d(Utils.LOG_TAG, "secondsBeforeMidnight: " + secondsBeforeMidnight);

                statUpdater.update(secondsBeforeMidnight, packageName, previousDate);
                statUpdater.update(secondsAfterMidnight, packageName, currentDate);
            } else {
                statUpdater.update(counter, packageName, currentDate);
            }
        }

        previousDate = currentDate;

        long end = System.currentTimeMillis();
        Log.d(Utils.LOG_TAG, "UsageService.handleAppChange() method time " + (end - start) + " ms");
    }

}
