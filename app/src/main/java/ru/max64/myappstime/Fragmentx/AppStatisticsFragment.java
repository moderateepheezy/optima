package ru.max64.myappstime.Fragmentx;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ru.max64.myappstime.Data.AppUsageFrequencyTableItem;
import ru.max64.myappstime.Database.DatabaseHelper;
import ru.max64.myappstime.R;
import ru.max64.myappstime.Utilities.UsageApplication;


public class AppStatisticsFragment extends UsageBaseFragment {

    private PieChart pieChart;
    private TextView tvInfo;
    private DatabaseHelper dbHelper;

    public AppStatisticsFragment() {}

    public static AppStatisticsFragment newInstance() {
        AppStatisticsFragment fragment = new AppStatisticsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_statstics, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = DatabaseHelper.getInstance(getActivity());
        tvInfo = (TextView) getActivity().findViewById(R.id.tv_info_text);
        pieChart = (PieChart) getActivity().findViewById(R.id.pie_chart);
        populatePieChart();

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                try {
                    handleEntryClick(e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void onNothingSelected() {}
        });
    }


    private void handleEntryClick(Entry e) {

        HashMap<String, String> map = (HashMap<String, String>) e.getData();

        int mapSize = map.size();
        if(mapSize == 0) {
            // Something went wrong, return
            return;
        } else if(mapSize == 1){

            String key = "";
            String value = "";
            for (Map.Entry<String,String> entry : map.entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
            }

            startSingleAppInfoActivity(key, value);
        } else {
            showListOfApps(map);
        }
    }


    private void showListOfApps(HashMap<String, String> map) {

        final ArrayList<String> packageName = new ArrayList<String>();
        final String appLabelsArr[] = new String[map.size()];

        int i=0;
        for (Map.Entry<String,String> entry : map.entrySet()) {
            String pck = entry.getKey();
            String label = entry.getValue();

            packageName.add(pck);

            if(TextUtils.isEmpty(label)) {
                appLabelsArr[i] = pck;
            } else {
                appLabelsArr[i] = label;
            }

            i++;
        }

        new MaterialDialog.Builder(getActivity())
                .title("Other Apps")
                .items(appLabelsArr)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        startSingleAppInfoActivity(packageName.get(which), appLabelsArr[which]);
                        return true;
                    }
                })
                .show();
    }


    private void populatePieChart() {

        ArrayList<PieChartDataItem> data = new ArrayList<PieChartDataItem>();
        Cursor cursor = dbHelper.getAppUsageCursor();

        ArrayList<AppUsageFrequencyTableItem> dataList = UsageApplication.getAppUsageFrequencyTableData(cursor);

        for(int i=0; i<dataList.size(); i++) {

            AppUsageFrequencyTableItem row = dataList.get(i);

            PieChartDataItem item = new PieChartDataItem();
            item.label = row.getLabel();
            item.packageName = row.getPackageName();
            item.totalTime = row.getTotalUseTime();

            data.add(item);
        }

        manageData(data);
    }


    private void manageData(ArrayList<PieChartDataItem> data) {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        HashMap<String, String> mapOthers = new HashMap<>();

        Collections.sort(data, new Comparator<PieChartDataItem>() {
            @Override
            public int compare(PieChartDataItem lhs, PieChartDataItem rhs) {

                if(lhs.totalTime > rhs.totalTime) {
                    return -1;
                } else if(lhs.totalTime < rhs.totalTime){
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        float tt = 0.0f;

        for(int i=0; i<data.size(); i++) {

            PieChartDataItem unit = data.get(i);

            String str;
            if(!TextUtils.isEmpty(unit.label)) {
                str = unit.label;
            } else if(!TextUtils.isEmpty(unit.packageName)) {
                str = unit.packageName;
            } else {
                str = "";
            }

            if(i >= 4) {
                tt += unit.totalTime;
                mapOthers.put(unit.packageName, unit.label);
            } else {
                labels.add(str);

                // Adding App name and package name to entry for later retrieval
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(unit.packageName, unit.label);
                entries.add(new Entry((float) unit.totalTime, i, map));
            }
        }

        if(0.0 != tt) {

            labels.add("Others");
            entries.add(new Entry(tt, 4, mapOthers));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData pieData = new PieData(labels, pieDataSet);
        pieChart.setData(pieData);

        pieChart.setDescription("Values in Percentage");
        pieChart.setDescriptionTextSize(UsageApplication.pxFromDp(getActivity(), 5));
        pieChart.setDescriptionColor(getActivity().getResources().getColor(R.color.app_theme_blue));

        pieChart.setUsePercentValues(true);
        pieChart.setDrawCenterText(true);

        pieChart.getLegend().setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        // Showing how much time user spent in apps
        long totalPhoneUsage = dbHelper.getTotalPhoneUsageInSeconds();
        if(0 == totalPhoneUsage) {
            tvInfo.setText("No Data");
        } else {
            tvInfo.setText("Total time spent in all apps combined : \n" +
                    UsageApplication.getFormattedUsageTime(totalPhoneUsage));
        }
    }


    private class PieChartDataItem {

        String label;
        String packageName;
        double totalTime;
    }
}
