package com.myapp.bipul.traceyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MOBOTICS on 27-10-2017.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent broadCastIntent) {

        // BOOT_COMPLETED” start Service
        if (broadCastIntent.getAction().equals(ACTION)) {


            Intent intent1 = new Intent(context, Geo.class);
            context.startService(intent1);

            Intent intent2 = new Intent(context, AddService.class);
            context.startService(intent2);


        }
    }
}
