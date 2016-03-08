package ru.max64.myappstime;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import ru.max64.myappstime.adapter.MyFragmentPagerAdapter;
import ru.max64.myappstime.fragment.InstalledDialogFragment;
import ru.max64.myappstime.model.StatEntry;
import ru.max64.myappstime.receiver.PhoneBootReceiver;
import ru.max64.myappstime.service.UsageService;
import ru.max64.myappstime.util.Utils;
import ru.max64.myappstime.view.SlidingTabLayout;

public class MainActivity extends AppCompatActivity implements InstalledDialogFragment.ChooserFragmentInterface {

    private static final int OFFSCREEN_PAGE_LIMIT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar_orange));
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        viewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // disable PhoneBootReceiver on Lollipop+
            ComponentName component = new ComponentName(this, PhoneBootReceiver.class);
            getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(Utils.LOG_TAG, "MainActivity.onStart()");

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(Utils.LOG_TAG, "pre-Lollipop");
            startService(new Intent(this, UsageService.class));
        }
    }

    @Override
    public void onChooserFragmentResult(int choice, Object dataObject) {
        StatEntry entry = (StatEntry)dataObject;
        String packageName = entry.getPackageName();

        switch (choice) {
            case Utils.DIALOG_LAUNCH:
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.installed_cannot_be_launched), Toast.LENGTH_SHORT).show();
                }
                break;

            case Utils.DIALOG_DETAILS:
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra(Utils.INTENT_PACKAGE_NAME, packageName);
                startActivity(intent);
                break;

            case Utils.DIALOG_SYSTEM_MENU:
                openSystemMenu(packageName);
                break;

            case Utils.DIALOG_UNINSTALL:
                Uri packageURI = Uri.parse("package:" + packageName);
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                startActivity(uninstallIntent);
                break;

            default:
                break;
        }
    }

    private void openSystemMenu(String packageName) {
        try {
            // Open the specific App Info page:
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Open the generic Apps page:
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            startActivity(intent);
        }
    }

}
