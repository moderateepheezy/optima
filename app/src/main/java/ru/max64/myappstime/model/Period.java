package ru.max64.myappstime.model;

import android.util.Log;

import ru.max64.myappstime.util.Utils;

public enum Period {

    DAY, YESTERDAY, WEEK, YEAR;

    public int asInt() {
        switch(this) {
            case DAY: return 1;
            case YESTERDAY: return 2;
            case WEEK: return 3;
            case YEAR: return 4;
            default:
                Log.e(Utils.LOG_TAG, "Unsupported period: " + this);
                throw new RuntimeException("Unsupported period: " + this);
        }
    }

    public static Period fromInt(int x) {
        switch(x) {
            case 1: return DAY;
            case 2: return YESTERDAY;
            case 3: return WEEK;
            case 4: return YEAR;
            default:
                Log.e(Utils.LOG_TAG, "Unsupported period: " + x);
                throw new RuntimeException("Unsupported period: " + x);
        }
    }
}
