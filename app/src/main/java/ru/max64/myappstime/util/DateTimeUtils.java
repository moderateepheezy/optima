package ru.max64.myappstime.util;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.max64.myappstime.R;

public class DateTimeUtils {

    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    @SuppressLint("SimpleDateFormat") // not for show human purpose
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private DateTimeUtils() {}

    public static String secondsToTime(String input, Context context) {
        String result = null;
        int seconds = Integer.parseInt(input);

        if (seconds < 60) {  // less than minute
            result = "< 1 " + context.getResources().getString(R.string.minutes);
        }
        else if ((seconds >= 60) && (seconds < 3600)) {  // from minute to hour
            int mins = seconds / 60;
            result = String.valueOf(mins) + " " + context.getResources().getString(R.string.minutes);
        }
        else if (seconds >= 3600) {  // more than hour
            int hours = seconds / 3600;
            int mins = (seconds % 3600) / 60;
            if (mins == 0) {
                result = String.valueOf(hours) + " " + context.getResources().getString(R.string.hours);
            } else {
                result = String.valueOf(hours) + " " + context.getResources().getString(R.string.hours)
                        + " " + String.valueOf(mins) + " " + context.getResources().getString(R.string.minutes);
            }
        }
        return result;
    }

    public static String longToDate(long millis, Context context) {
        Date date = new Date(millis);
        DateFormat newDateFormat = android.text.format.DateFormat.getDateFormat(context);
        return newDateFormat.format(date);
    }

    public static String getCurrentDate(Calendar calendar)  {
        return DATE_FORMAT.format(calendar.getTime());
    }

    public static String getYesterdayDateString(Calendar calendar) {
        calendar.add(Calendar.DATE, -1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    public static String getWeekDateString(Calendar calendar) {
        calendar.add(Calendar.DATE, -7);
        return DATE_FORMAT.format(calendar.getTime());
    }

    public static String getYearDateString(Calendar calendar) {
        calendar.add(Calendar.YEAR, -1);
        return DATE_FORMAT.format(calendar.getTime());
    }

    public static int getSecondsAfterMidnight(Calendar calendar) {
        long now = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long passed = now - calendar.getTimeInMillis();
        return (int) (passed / 1000);
    }

}
