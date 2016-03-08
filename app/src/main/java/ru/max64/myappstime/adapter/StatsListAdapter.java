package ru.max64.myappstime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.max64.myappstime.R;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.DateTimeUtils;

public class StatsListAdapter extends BaseAdapter {

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
        ((ImageView) convertView.findViewById(R.id.stats_icon)).setImageDrawable(se.getIcon());

        TextView tvTitle = (TextView) convertView.findViewById(R.id.stats_title);
        tvTitle.setText(se.getTitle());

        String time = Integer.toString(se.getTime());
        TextView tvTime = (TextView) convertView.findViewById(R.id.stats_time);
        tvTime.setText(DateTimeUtils.secondsToTime(time, context));

        return convertView;
    }

    public StatEntry getStatEntry(int position) {
        return ((StatEntry) getItem(position));
    }

}
