package ru.max64.myappstime;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ru.max64.myappstime.util.DateTimeUtils;
import ru.max64.myappstime.util.Utils;

public class DetailsActivity extends Activity {

    private PackageInfo packageInfo;

    private ImageView ivIcon;
    private TextView tvTitle, tvPackage, tvSourceDir, tvDataDir, tvInstalledDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ivIcon = (ImageView) findViewById(R.id.details_image);
        tvTitle = (TextView) findViewById(R.id.details_title);
        tvPackage = (TextView) findViewById(R.id.details_packagename);
        tvSourceDir = (TextView) findViewById(R.id.details_sourcedir);
        tvDataDir = (TextView) findViewById(R.id.details_datadir);
        tvInstalledDate = (TextView) findViewById(R.id.details_install_date);

        Intent intent = getIntent();
        String packageName = intent.getStringExtra(Utils.INTENT_PACKAGE_NAME);

        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ApplicationInfo appInfo = packageInfo.applicationInfo;

        ivIcon.setImageDrawable(appInfo.loadIcon(packageManager));
        tvTitle.setText(appInfo.loadLabel(packageManager));
        tvPackage.setText(packageInfo.packageName);
        tvSourceDir.setText(appInfo.sourceDir);
        tvDataDir.setText(appInfo.dataDir);
        tvInstalledDate.setText(DateTimeUtils.longToDate(packageInfo.firstInstallTime, getApplicationContext()));
    }

}
