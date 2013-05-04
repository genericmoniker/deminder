package net.esmithy.deminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * User: Eric
 * Date: 5/1/13
 * Time: 8:53 PM
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String DEBUG_TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Recurring alarm; requesting deminder service.");
        Intent downloader = new Intent(context, DeminderService.class);
        downloader.setData(Uri.parse("http://feeds.feedburner.com/MobileTuts?format=xml"));
        context.startService(downloader);
    }
}
