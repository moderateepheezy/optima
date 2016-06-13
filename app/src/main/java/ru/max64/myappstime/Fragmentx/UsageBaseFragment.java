package ru.max64.myappstime.Fragmentx;

import android.content.Intent;
import android.support.v4.app.Fragment;

import ru.max64.myappstime.Activities.SingleAppUsageInfoActivity;
import ru.max64.myappstime.Utilities.Constants;


/**
 * Created by aditya on 27/07/15.
 */
public class UsageBaseFragment extends Fragment {

    void startSingleAppInfoActivity(String pck, String label) {

        Intent i = new Intent(getActivity(), SingleAppUsageInfoActivity.class);
        i.putExtra(Constants.PCK_NAME, pck);
        i.putExtra(Constants.APP_LABEL, label);
        startActivity(i);
    }
}
