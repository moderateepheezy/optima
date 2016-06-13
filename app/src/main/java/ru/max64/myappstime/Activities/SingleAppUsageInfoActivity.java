package ru.max64.myappstime.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import ru.max64.myappstime.Fragmentx.SingleAppUsageInfoFragment;
import ru.max64.myappstime.R;
import ru.max64.myappstime.Utilities.Constants;


public class SingleAppUsageInfoActivity extends ToolBarBaseActivity {

    private final String FRAG_TAG = "frag_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_app_usage_info);

        String packageName = getIntent().getStringExtra(Constants.PCK_NAME);
        String appLabel = getIntent().getStringExtra(Constants.APP_LABEL);

        initialiseToolbar(appLabel);

        FragmentManager manager = getSupportFragmentManager();
        if(null == manager.findFragmentByTag(FRAG_TAG)) {

            SingleAppUsageInfoFragment fragment = SingleAppUsageInfoFragment.newInstance(packageName);
            manager.beginTransaction().add(R.id.ll_container, fragment, FRAG_TAG).commit();
        }
    }
}
