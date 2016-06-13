package ru.max64.myappstime;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class Process_activty extends AppCompatActivity {
    Switch sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activty);

        initViews();
        //super.onBackPressed();
    }

    public void initViews () {
        sync=(Switch) findViewById(R.id.sync_switch);
        if (ContentResolver.getMasterSyncAutomatically()){
            killAllBackgroudProcess();
            sync.setChecked(true);
        }else{
            sync.setChecked(false);
        }

        sync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ContentResolver.getMasterSyncAutomatically()){
                    ContentResolver.setMasterSyncAutomatically(false);
                }else {
                    ContentResolver.setMasterSyncAutomatically(true);
                    killAllBackgroudProcess();
                }
            }
        });

    }


    public void killAllBackgroudProcess(){
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1)continue;
            if(packageInfo.packageName.equals("ru.max64.myappstime")) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
    }
}
