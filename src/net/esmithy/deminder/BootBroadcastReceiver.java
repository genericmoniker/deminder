package net.esmithy.deminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver that listens for BOOT_COMPLETED so that it can reschedule periodic alarms.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = BootBroadcastReceiver.class.getName();

    private AlarmBroadcastReceiver mAlarmReceiver = new AlarmBroadcastReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Boot completed; scheduling deminder service alarm.");
        mAlarmReceiver.setAlarm(context);
    }
}
