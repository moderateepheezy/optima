package ru.max64.myappstime.fragment;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import ru.max64.myappstime.R;
import ru.max64.myappstime.adapter.StatsListAdapter;
import ru.max64.myappstime.loader.DBStatProvider;
import ru.max64.myappstime.loader.NativeStatProvider;
import ru.max64.myappstime.loader.StatProvider;
import ru.max64.myappstime.model.Period;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.Prefs;
import ru.max64.myappstime.util.Utils;

public class StatsFragment extends Fragment {

    private Context context;
    private LoadStatsTask loadTask;
    private StatProvider loader;

    private TextView tvPeriod;
    private ListView listView;
    private List<StatEntry> statList;
    private ProgressBar progressBar;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_stats, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        switch (item.getItemId()) {
            case R.id.choose_submenu_day:
                prefs.edit().putInt(Prefs.STATS_PERIOD, Period.DAY.asInt()).commit();
                refreshList();
                return true;

            case R.id.choose_submenu_yesterday:
                prefs.edit().putInt(Prefs.STATS_PERIOD, Period.YESTERDAY.asInt()).commit();

                refreshList();
                return true;

            case R.id.choose_submenu_week:
                prefs.edit().putInt(Prefs.STATS_PERIOD, Period.WEEK.asInt()).commit();
                refreshList();
                return true;

            case R.id.choose_submenu_year:
                prefs.edit().putInt(Prefs.STATS_PERIOD, Period.YEAR.asInt()).commit();
                refreshList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
        return inflater.inflate(R.layout.fragment_stats, group, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            loader = new DBStatProvider(context);
        } else {
            loader = new NativeStatProvider(context);
        }

        progressBar = (ProgressBar) getView().findViewById(R.id.stats_progress_bar);
        tvPeriod = (TextView) getView().findViewById(R.id.stats_tv_period);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Period period = Period.fromInt(prefs.getInt(Prefs.STATS_PERIOD, 1));
        setTextMark(period);

        listView = (ListView) getView().findViewById(R.id.stats_list);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoadTask();
    }

    private void setTextMark(Period period) {
        switch (period) {
            case DAY:
                tvPeriod.setText(getResources().getString(R.string.stats_day));
                break;
            case YESTERDAY:
                tvPeriod.setText(getResources().getString(R.string.stats_yesterday));
                break;
            case WEEK:
                tvPeriod.setText(getResources().getString(R.string.stats_week));
                break;
            case YEAR:
                tvPeriod.setText(getResources().getString(R.string.stats_year));
                break;
        }
    }

    private void refreshList() {
        Log.d(Utils.LOG_TAG, "Stats - refreshList()");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !permissionGranted()) {
            return;
        }

        runLoadTask();
    }

    private void runLoadTask() {
        stopLoadTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int period = prefs.getInt(Prefs.STATS_PERIOD, 1);

        loadTask = new LoadStatsTask();
        loadTask.execute(Period.fromInt(period));
    }

    private void stopLoadTask() {
        if (loadTask != null && loadTask.getStatus() == AsyncTask.Status.RUNNING) {
            loadTask.cancel(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean permissionGranted() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean granted = prefs.getBoolean(Prefs.STATS_PERMISSION_GRANTED, false);

        if (granted) {
            return true;
        }

        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_ALLOWED) {
            prefs.edit().putBoolean(Prefs.STATS_PERMISSION_GRANTED, true).commit();
            return true;
        } else {
            openDialog();
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getResources().getString(R.string.enable_usage_permission),
                        Toast.LENGTH_LONG).show();
            }
        });

        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private class LoadStatsTask extends AsyncTask<Period, Void, Void> {

        StatsListAdapter adapter;
        Period period;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView.setAdapter(null);
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(Period... params) {
            period = params[0];
            statList = loader.loadStats(period);

            adapter = new StatsListAdapter(context, statList);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(adapter);
            setTextMark(period);
        }

    }
}
