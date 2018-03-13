package ru.bp.barcodeto1c;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by slade on 06.03.2018.
 */

public class BarcodeService extends Service {
    public static boolean isActive = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isActive = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start service intent catch here. Starting work now, while stopSelf() don't called
        return Service.START_STICKY; // Restart service after system killed it
    }

    @Override
    public void onDestroy() {
        isActive = false;
        super.onDestroy();
    }
}
