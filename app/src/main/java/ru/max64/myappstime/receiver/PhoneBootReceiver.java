package ru.max64.myappstime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.max64.myappstime.service.UsageService;
import ru.max64.myappstime.util.Utils;

public class PhoneBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Utils.LOG_TAG, "received android.intent.action.BOOT_COMPLETED in "
                + PhoneBootReceiver.class.getSimpleName());

        Intent startServiceIntent = new Intent(context, UsageService.class);
        context.startService(startServiceIntent);
    }
}