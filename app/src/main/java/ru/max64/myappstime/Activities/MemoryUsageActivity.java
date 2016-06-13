package ru.max64.myappstime.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import ru.max64.myappstime.Fragmentx.MemoryUsageFragment;
import ru.max64.myappstime.R;


public class MemoryUsageActivity extends ToolBarBaseActivity {

    private final String FRAG_TAG = "memory_usage_frag_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_usage);

        FragmentManager managerFragment = getSupportFragmentManager();
        if(managerFragment.findFragmentByTag(FRAG_TAG) == null) {
            managerFragment.beginTransaction().
                    add(R.id.rl_container, new MemoryUsageFragment(), FRAG_TAG).commit();
        }

        initialiseToolbar("Memory Usage");
    }
}
