package ru.bp.barcodeto1c;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;
        import android.widget.Toast;

/**
 * Created by slade on 06.03.2018.
 */

public class BarcodeReceiver extends BroadcastReceiver {
    private static final String TAG = "BarcodeReceiver";
    private static final String BARCODE_PARAM = "decode_rslt";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive " + intent.getAction());

        String barcode = intent.getStringExtra(BARCODE_PARAM);

        if (BarcodeService.isActive) {
            Log.v(TAG, "BARCODE " + barcode);
            Intent intentFor1C = new Intent("com.google.android.c2dm.intent.RECEIVE");
            intentFor1C.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intentFor1C.putExtra("text", "1");
            intentFor1C.putExtra("data", barcode);
            context.sendBroadcast(intentFor1C);
            Log.v(TAG, "Send Intent for 1C with action 'com.google.android.c2dm.intent.RECEIVE' and extra 'data={barcode}'");
        } else {
            Log.v(TAG, "Barcode Service is not active");
            //Toast.makeText(context, "To start, turn on the BarcodeService", Toast.LENGTH_LONG).show();
        }
    }
}