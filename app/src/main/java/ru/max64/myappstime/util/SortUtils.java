package ru.max64.myappstime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.max64.myappstime.model.StatEntry;

public class SortUtils {

    private SortUtils() {};

    public static List<StatEntry> sortAppsList(List<StatEntry> listApps, int sortType) {
        List<StatEntry> result = new ArrayList<>(listApps);

        switch (sortType) {
            case 0: // By name asc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        return lhs.getTitle().compareTo(rhs.getTitle());
                    }
                });
                break;

            case 1: // By name desc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        return rhs.getTitle().compareTo(lhs.getTitle());
                    }
                });
                break;

            case 2: // By install date asc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        if (lhs.getInstallDate() > rhs.getInstallDate())
                            return 1;
                        if (lhs.getInstallDate() < rhs.getInstallDate())
                            return -1;
                        return 0;
                    }
                });
                break;

            case 3: // By install date desc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        if (lhs.getInstallDate() > rhs.getInstallDate())
                            return -1;
                        if (lhs.getInstallDate() < rhs.getInstallDate())
                            return 1;
                        return 0;
                    }
                });
                break;

            case 4: // By last updated asc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        if (lhs.getLastUpdated() > rhs.getLastUpdated())
                            return 1;
                        if (lhs.getLastUpdated() < rhs.getLastUpdated())
                            return -1;
                        return 0;
                    }
                });
                break;

            case 5: // By last updated desc
                Collections.sort(result, new Comparator<StatEntry>() {
                    @Override
                    public int compare(StatEntry lhs, StatEntry rhs) {
                        if (lhs.getLastUpdated() > rhs.getLastUpdated())
                            return -1;
                        if (lhs.getLastUpdated() < rhs.getLastUpdated())
                            return 1;
                        return 0;
                    }
                });
                break;

            default:
                break;
        }

        return result;
    }

}
