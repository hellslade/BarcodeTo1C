package ru.bp.barcodeto1c;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String COMMAND = "Command";
    private static final String COMMAND_START = "START";
    private static final String COMMAND_STOP = "STOP";
    private static final String MESSAGE = "Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String toastMessage = "";
        Intent intent = getIntent();
        if (intent.hasExtra(COMMAND)) {
            String command = intent.getStringExtra(COMMAND);

            if (command.equalsIgnoreCase(COMMAND_START)) {
                // get command to start service
                this.startService(new Intent(this, BarcodeService.class));
            } else if (command.equalsIgnoreCase(COMMAND_STOP)) {
                // get command to stop service
                this.stopService(new Intent(this, BarcodeService.class));
            }
            if (intent.hasExtra(MESSAGE)) {
                toastMessage = intent.getStringExtra(MESSAGE);
                if (!toastMessage.isEmpty()) {
                    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            }

            }
        }
        // No need to show activity, it's must created for initialize receivers and service...
        finish();
    }
}
