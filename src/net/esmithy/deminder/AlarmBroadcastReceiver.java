package net.esmithy.deminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Broadcast receiver that listens for alarms to invoke the deminder service.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = AlarmBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Alarm received; requesting deminder service.");
        Intent deminder = new Intent(context, DeminderService.class);
        context.startService(deminder);
    }

    /**
     * Schedules a repeating alarm to invoke the deminder service.
     *
     * This will first remove any currently scheduled alarms so there won't be duplicates.
     *
     * @param context context to use when setting the alarm.
     */
    public void setAlarm(Context context) {
        // Schedule around 10:00 PM (22:00) local time.
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 19);
        time.set(Calendar.MINUTE, 28);
        time.set(Calendar.SECOND, 0);
        setRepeatingAlarm(context, time);
    }

    private void setRepeatingAlarm(Context context, Calendar time) {
        logAlarmTime(time);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // May be able to use setInexactRepeating, but at least for testing, that doesn't work
        // very well. Maybe it would wake up eventually? The docs say "it might not occur for
        // almost a full interval after that time". That's not really testable with INTERVAL_DAY.
        alarms.setRepeating(
                AlarmManager.RTC_WAKEUP,
                time.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    private void logAlarmTime(Calendar time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String timeString = sdf.format(time.getTime());
        Log.d(LOG_TAG, "Setting alarm for " + timeString);
    }
}
