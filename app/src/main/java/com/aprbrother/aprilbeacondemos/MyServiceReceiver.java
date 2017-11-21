package com.aprbrother.aprilbeacondemos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hp on 2017-09-07.
 */

public class MyServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            context.startService(new Intent(context,background.class));
    }
}
