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

public class TopListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<StatEntry> entries = new ArrayList<>();

    public TopListAdapter(Context context, List<StatEntry> entries) {
        this.context = context;
        this.entries = entries;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_top, parent, false);
        }

        StatEntry se = getStatEntry(position);

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.top_icon);
        ivIcon.setImageDrawable(se.getIcon());

        TextView tvTitle = (TextView) convertView.findViewById(R.id.top_name);
        tvTitle.setText(se.getTitle());

        TextView tvTime = (TextView) convertView.findViewById(R.id.top_time);
        if (se.getTitle().equals("---")) {
            tvTime.setText("---");
        } else {
            String timeText = context.getResources().getString(R.string.top_time)
                    + ": "
                    + DateTimeUtils.secondsToTime(Integer.toString(se.getTime()), context);
            tvTime.setText(timeText);
        }

        return convertView;
    }

    public StatEntry getStatEntry(int position) {
        return ((StatEntry) getItem(position));
    }

}