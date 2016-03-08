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
import java.util.Locale;

import ru.max64.myappstime.R;
import ru.max64.myappstime.model.StatEntry;

public class InstalledListAdapter extends BaseAdapter {

    private Context context;
    private List<StatEntry> entries;
    private List<StatEntry> entriesAll;

    public InstalledListAdapter(Context context) {
        this.context = context;
        entries = new ArrayList<StatEntry>();
        entriesAll = new ArrayList<StatEntry>();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_installed, parent, false);
        }

        StatEntry se = (StatEntry) getItem(position);

        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.installed_icon);
        ivIcon.setImageDrawable(se.getIcon());

        TextView title = (TextView) convertView.findViewById(R.id.installed_title);
        title.setText(se.getTitle());

        return convertView;
    }

    public List<StatEntry> getEntries() {
        return entries;
    }

    public void update(List<StatEntry> newlist) {
        entries.clear();
        entries.addAll(newlist);
        entriesAll.addAll(entries);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        entries.clear();

        if (charText.length() == 0) {
            entries.addAll(entriesAll);
        } else {
            for (StatEntry entry : entriesAll) {
                if (entry.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    entries.add(entry);
                }
            }
        }

        notifyDataSetChanged();
    }

}
