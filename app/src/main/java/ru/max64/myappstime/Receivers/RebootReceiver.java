package ru.max64.myappstime.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.max64.myappstime.Service.CheckForegroundAppService;


/**
 * Created by aditya on 16/07/15.
 */
public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, CheckForegroundAppService.class);
        context.startService(serviceIntent);
    }
}
