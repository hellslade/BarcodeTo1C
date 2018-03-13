package ru.bp.barcodeto1c;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by slade on 06.03.2018.
 */

public class BootBroadcast extends BroadcastReceiver {
    private static final String TAG = "BootBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive " + intent.getAction());
        // Should it started on BOOT?
        //context.startService(new Intent(context, BarcodeService.class));
    }
}