package ru.max64.myappstime.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ru.max64.myappstime.R;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.DateTimeUtils;

public class StatsListAdapter extends BaseAdapter {

    String line;
    String packagename;

    private Context context;
    private LayoutInflater inflater;
    private List<StatEntry> objects = new ArrayList<StatEntry>();

    public StatsListAdapter(Context context, List<StatEntry> StatsEntries) {
        this.context = context;
        this.objects = StatsEntries;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_stats, parent, false);
        }

        StatEntry se = getStatEntry(position);
        packagename = se.getPackageName();
        ((ImageView) convertView.findViewById(R.id.stats_icon)).setImageDrawable(se.getIcon());

        TextView tvTitle = (TextView) convertView.findViewById(R.id.stats_title);
        tvTitle.setText(se.getTitle());

        String time = Integer.toString(se.getTime());
        TextView tvTime = (TextView) convertView.findViewById(R.id.stats_time);
        tvTime.setText(DateTimeUtils.secondsToTime(time, context));
        TextView cpu = (TextView) convertView.findViewById(R.id.cpu);
        cpu.setText(getCPUUsage(setPidNo()));

        return convertView;
    }

    public StatEntry getStatEntry(int position) {
        return ((StatEntry) getItem(position));
    }

    public int setPidNo(){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> pids = am.getRunningAppProcesses();
        int processid = 0;
        for (int i = 0; i < pids.size(); i++) {
            ActivityManager.RunningAppProcessInfo info = pids.get(i);
            if (info.processName.equalsIgnoreCase(packagename)) {
                processid = info.pid;
            }
        }

        return processid;
    }

    public String getCPUUsage(int pid) {
        Process p;
        try {
            String[] cmd = {
                    "sh",
                    "-c",
                    "top -m 1000 -d 1 -n 1 | grep \"" + pid + "\" "};
            p = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            line = reader.readLine();
            // line contains the process info
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

}
