package ru.max64.myappstime.loader;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.max64.myappstime.model.Period;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.Utils;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class NativeStatProvider implements StatProvider {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
    private static final List<String> SYSTEM_PACKAGES = Arrays.asList(
            "android",
            "com.android.launcher3",
            "com.android.vending",
            "com.android.packageinstaller",
            "com.android.settings",
            "com.android.systemui",
            "com.google.android.gms",
            "com.android.contacts",
            "com.android.gallery3d"
    );

    private Context context;

    public NativeStatProvider(Context context) {
        this.context = context;
    }

    public List<StatEntry> loadStats(Period period) {
        return loadStatsWithLimit(period, 0);
    }

    @SuppressWarnings("ResourceType")
    public List<StatEntry> loadStatsWithLimit(Period period, int maxCount) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");

        long from = getTimeFrom(period);
        long till = getTimeTill(period);
        int intervalType = getIntervalType(period);

        Log.d(Utils.LOG_TAG, "from " + DATE_FORMAT.format(new Date(from)) + " to " + DATE_FORMAT.format(new Date(till)));

        List<UsageStats> stats = usm.queryUsageStats(intervalType, from, till);
        removeSystemApps(stats);

        List<StatEntry> entries = createStatsEntries(stats);

        if (maxCount > 0 && maxCount <= entries.size()) {
            return entries.subList(0, maxCount);
        }

        return entries;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void removeSystemApps(List<UsageStats> stats) {
        Iterator<UsageStats> it = stats.iterator();

        while (it.hasNext()) {
            UsageStats next = it.next();
            if (SYSTEM_PACKAGES.contains(next.getPackageName())) {
                it.remove();
            }
        }
    }

    private long getTimeFrom(Period period) {
        Calendar from = Calendar.getInstance();

        from.set(Calendar.HOUR_OF_DAY, 0);
        from.set(Calendar.MINUTE, 0);
        from.set(Calendar.SECOND, 0);
        from.set(Calendar.MILLISECOND, 0);

        switch (period) {
            case DAY:
                break;
            case YESTERDAY:
                from.add(Calendar.DATE, -1);
                break;
            case WEEK:
                from.add(Calendar.DATE, -7);
                break;
            case YEAR:
                from.add(Calendar.YEAR, -1);
                break;
        }

        return from.getTimeInMillis();
    }

    private long getTimeTill(Period period) {
        Calendar till = Calendar.getInstance();

        switch (period) {
            case DAY:
                break;
            case YESTERDAY:
                till.set(Calendar.HOUR_OF_DAY, 0);
                till.set(Calendar.MINUTE, 0);
                till.set(Calendar.SECOND, 0);
                till.set(Calendar.MILLISECOND, 0);
                break;
            case WEEK:
                break;
            case YEAR:
                break;
        }

        return till.getTimeInMillis();
    }

    private int getIntervalType(Period period) {
        int intervalType = 0;

        switch (period) {
            case DAY:
                intervalType = UsageStatsManager.INTERVAL_DAILY;
                break;
            case YESTERDAY:
                intervalType = UsageStatsManager.INTERVAL_DAILY;
                break;
            case WEEK:
                intervalType = UsageStatsManager.INTERVAL_WEEKLY;
                break;
            case YEAR:
                intervalType = UsageStatsManager.INTERVAL_YEARLY;
                break;
        }

        return intervalType;
    }

    private List<StatEntry> createStatsEntries(List<UsageStats> stats) {
        List<StatEntry> entries = new ArrayList<>(stats.size());
        PackageManager pm = context.getPackageManager();

        for (UsageStats stat : stats) {
            try {
                Log.d(Utils.LOG_TAG, stat.getPackageName());

                String packageName = stat.getPackageName();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);

                StatEntry se = new StatEntry();
                se.setPackageName(packageName);
                se.setIcon(pi.applicationInfo.loadIcon(pm));
                se.setTitle(pi.applicationInfo.loadLabel(pm).toString());
                se.setInstallDate(pi.firstInstallTime);

                Log.d(Utils.LOG_TAG, "TotalTimeInForeground: " + String.valueOf(stat.getTotalTimeInForeground()));
                se.setTime((int) stat.getTotalTimeInForeground() / 1000);

                entries.add(se);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(Utils.LOG_TAG, e.toString());
            }
        }

        Collections.sort(entries, new Comparator<StatEntry>() {
            @Override
            public int compare(StatEntry fe, StatEntry se) {
                return se.getTime() - fe.getTime();
            }
        });

        return entries;
    }

}
