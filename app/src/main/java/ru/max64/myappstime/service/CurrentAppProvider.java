package ru.max64.myappstime.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import ru.max64.myappstime.util.Utils;

public class CurrentAppProvider {

    private ActivityManager am;
    private PackageManager pm;
    private PackageInfo foregroundAppPackageInfo;

    public CurrentAppProvider(Context context) {
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        pm = context.getPackageManager();
    }

    public AppInfo getAppInfo() {
        // The first in the list of RunningTasks is always the foreground task
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Utils.LOG_TAG, String.valueOf(e));
        }
        return new AppInfo(foregroundAppPackageInfo.packageName, isUserApp(foregroundAppPackageInfo.applicationInfo));
    }

    private boolean isUserApp(ApplicationInfo ai) {
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }
}
