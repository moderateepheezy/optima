package ru.max64.myappstime.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.max64.myappstime.Fragmentx.AppStatisticsFragment;
import ru.max64.myappstime.Fragmentx.AppsFragment;
import ru.max64.myappstime.Fragmentx.MemoryUsageFragment;
import ru.max64.myappstime.Fragmentx.PhoneFactFragment;
import ru.max64.myappstime.Fragmentx.UsagePatternFragment;
import ru.max64.myappstime.R;
import ru.max64.myappstime.fragment.InstalledFragment;
import ru.max64.myappstime.fragment.StatsFragment;
import ru.max64.myappstime.fragment.TopFragment;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter
    implements ViewPager.OnPageChangeListener {

    private static final int FRAGMENT_POSITION_TOP = 0;
    private static final int FRAGMENT_POSITION_STATS = 1;
    private static final int FRAGMENT_POSITION_INSTALLED = 2;
    private static final int FRAGMENT_ALL_W = 3;
    private static final int FRAGMENT_APP_USAGE = 4;
    private static final int FRAGMENT_SHOW_MOST_USED = 5;
    private static final int FRAGMENT_SHOW_APP_NOT_USED = 6;
    private static final int FRAGMENT_SHOW_MEMORY_USAGE = 7;

    private static final int PAGE_COUNT = 8;

    private Context context;
    private Map<Integer, Fragment> tabs = new HashMap<>();
    private List<String> tabTitles = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        tabTitles.add(context.getResources().getString(R.string.tab_top));
        tabTitles.add(context.getResources().getString(R.string.tab_stats));
        tabTitles.add(context.getResources().getString(R.string.tab_installed));
        tabTitles.add("CPU USAGE");
        tabTitles.add("APP USAGE");
        tabTitles.add("MUST USED APP");
        tabTitles.add("APP NOT USED");
        tabTitles.add("MEMORY USAGE");

        tabs.put(FRAGMENT_POSITION_TOP, new TopFragment());
        tabs.put(FRAGMENT_POSITION_STATS, new StatsFragment());
        tabs.put(FRAGMENT_POSITION_INSTALLED, new InstalledFragment());
        tabs.put(FRAGMENT_APP_USAGE, new AppStatisticsFragment());
        tabs.put(FRAGMENT_SHOW_MOST_USED, new UsagePatternFragment());
        tabs.put(FRAGMENT_SHOW_APP_NOT_USED, new UsagePatternFragment());
        tabs.put(FRAGMENT_SHOW_MEMORY_USAGE, new MemoryUsageFragment());
        tabs.put(FRAGMENT_ALL_W, new AppsFragment());
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}