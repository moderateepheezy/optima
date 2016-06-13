package ru.max64.myappstime.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ru.max64.myappstime.R;
import ru.max64.myappstime.adapter.InstalledListAdapter;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.util.Prefs;
import ru.max64.myappstime.util.SortUtils;

public class InstalledFragment extends Fragment {

    private Context context;
    private InstalledListAdapter adapter;

    private ListView listView;
    private ProgressBar progressBar;
    private Spinner spinnerSort;

    private List<StatEntry> appList, appListWithoutSystem;
    private boolean listLoaded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved) {
        return inflater.inflate(R.layout.fragment_installed, group, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        context = getActivity().getApplicationContext();

        progressBar = (ProgressBar) getView().findViewById(R.id.installed_progress_bar);

        listView = (ListView) getView().findViewById(R.id.installed_list);
        refreshList();
        listViewOnLongClick();

        initSortSpinner();
        initCbIncludeSystem();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_installed, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search_installed);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    String text = newText.toLowerCase(Locale.getDefault());
                    adapter.filter(text);
                    return true;
                }
            });
        }
    }

    private void refreshList() {
        List<StatEntry> emptyList = Collections.emptyList();
        if (adapter != null) {
            adapter.update(emptyList);
        }

        new LoadInstalledTask().execute();
    }

    private void listViewOnLongClick() {
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                StatEntry entry = (StatEntry) adapter.getItem(position);

                List<String> firstOptionsList = Arrays.asList(getResources().getString(R.string.launch), getResources()
                        .getString(R.string.details), getResources().getString(R.string.system_menu), getResources()
                        .getString(R.string.uninstall));

                List<String> optionsList = new ArrayList<>();
                optionsList.addAll(firstOptionsList);

                CharSequence[] options = optionsList.toArray(new CharSequence[optionsList.size()]);

                InstalledDialogFragment chooser = new InstalledDialogFragment(getResources().getString(
                        R.string.choose_action), options, entry);
                chooser.show(getActivity().getSupportFragmentManager(), "chooserFragment");
                return true;
            }
        });
    }

    private void initSortSpinner() {
        final ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.sort_by_string_array,
                R.layout.spinner_sort);
        sortAdapter.setDropDownViewResource(R.layout.spinner_sort_dropdown_item);

        spinnerSort = (Spinner) getView().findViewById(R.id.installed_spinner_sort);
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setPrompt(getResources().getString(R.string.installed_spinner_sort));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int sortType = prefs.getInt(Prefs.SORT_INSTALLED, 0);

        spinnerSort.setSelection(sortType);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                int sortType = prefs.getInt(Prefs.SORT_INSTALLED, 0);
                if (position != sortType) {
                    prefs.edit().putInt(Prefs.SORT_INSTALLED, position).commit();

                    List<StatEntry> entries = adapter.getEntries();
                    List<StatEntry> sorted = SortUtils.sortAppsList(entries, position);
                    adapter.update(sorted);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void initCbIncludeSystem() {
        CheckBox cbIncludeSystem = (CheckBox) getView().findViewById(R.id.installed_cb_include_system);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        cbIncludeSystem.setChecked(prefs.getBoolean(Prefs.INCLUDE_SYSTEM_APPS, false));

        cbIncludeSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().putBoolean(Prefs.INCLUDE_SYSTEM_APPS, isChecked).commit();
                if (listLoaded) {
                    List<StatEntry> entries = null;
                    if (isChecked) {
                        entries = appList;
                    } else {
                        entries = appListWithoutSystem;
                    }

                    SharedPreferences pr = PreferenceManager.getDefaultSharedPreferences(context);
                    int sortType = prefs.getInt(Prefs.SORT_INSTALLED, 0);
                    List<StatEntry> sorted = SortUtils.sortAppsList(entries, sortType);
                    adapter.update(sorted);
                }
            }
        });
    }

    private class LoadInstalledTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listView.setAdapter(null);
            progressBar.setVisibility(View.VISIBLE);
            listLoaded = false;
            getActivity().invalidateOptionsMenu();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadInstalled();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean includeSystemApps = prefs.getBoolean(Prefs.INCLUDE_SYSTEM_APPS, false);
            int sortType = prefs.getInt(Prefs.SORT_INSTALLED, 0);

            SortUtils.sortAppsList(appList, sortType);
            appListWithoutSystem = cleanFromSystem(appList);

            adapter = new InstalledListAdapter(context);

            if (includeSystemApps) {
                adapter.update(appList);
            } else {
                adapter.update(appListWithoutSystem);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            listView.setAdapter(adapter);
            listLoaded = true;
        }

        private void loadInstalled() {
            List<StatEntry> apps = new ArrayList<>();
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packs = packageManager.getInstalledPackages(0);

            for (int i = 0; i < packs.size(); i++) {
                PackageInfo pInfo = packs.get(i);
                ApplicationInfo aInfo = pInfo.applicationInfo;
                StatEntry se = new StatEntry();
                if (!isUserApp(aInfo)) {
                    se.setSystemApp(true);
                } else {
                    se.setSystemApp(false);
                }
                se.setPackageName(packs.get(i).packageName);
                se.setIcon(aInfo.loadIcon(packageManager));
                se.setTitle(aInfo.loadLabel(packageManager).toString());
                se.setInstallDate(pInfo.firstInstallTime);
                se.setLastUpdated(pInfo.lastUpdateTime);

                apps.add(se);
            }

            appList = apps;
        }

        private List<StatEntry> cleanFromSystem(List<StatEntry> listApps) {
            List<StatEntry> list = new ArrayList<>();
            for (StatEntry entry : listApps) {
                if (!entry.isSystemApp()) {
                    list.add(entry);
                }
            }
            return list;
        }

        private boolean isUserApp(ApplicationInfo ai) {
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            return (ai.flags & mask) == 0;
        }
    }

}
