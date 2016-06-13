package ru.max64.myappstime.loader;

import java.util.List;

import ru.max64.myappstime.model.Period;
import ru.max64.myappstime.model.StatEntry;

public interface StatProvider {

    List<StatEntry> loadStats(Period period);

    List<StatEntry> loadStatsWithLimit(Period period, int maxCount);

}
