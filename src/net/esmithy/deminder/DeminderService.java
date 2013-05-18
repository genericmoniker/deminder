package net.esmithy.deminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * User: Eric
 * Date: 5/1/13
 * Time: 8:56 PM
 */
public class DeminderService extends Service {
    private static final String LOG_TAG = DeminderService.class.getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Service started");
        DeminderTask task = new DeminderTask();
        task.execute(this);
        return Service.START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Background task for deminding events.
     *
     * The generic types on AsyncTask are params, progress and result.
     */
    private class DeminderTask extends AsyncTask<Service, Void, ArrayList<Event>> {
        private final String LOG_TAG = DeminderTask.class.getName();
        private final int NOTIFY_ID = 1;
        private Service mService;

        @Override
        protected ArrayList<Event> doInBackground(Service... services) {
            Log.d(LOG_TAG, "Running demind in the background.");
            mService = services[0];
            ReminderManager reminderManager = new ReminderManager(mService);
            return reminderManager.demindAllDayEvents(2);
        }

        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            if (!events.isEmpty()) {
                for (Event event : events) {
                    Log.i(LOG_TAG, "Deminded event: " + event);
                    notifyEventChanged(event);
                }
            }
            else {
                Log.i(LOG_TAG, "No events found to demind.");
            }
            mService.stopSelf();
        }

        private void notifyEventChanged(Event event) {
            Notification.Builder builder = new Notification.Builder(mService)
                            .setSmallIcon(R.drawable.ic_demind)
                            .setContentTitle("Event deminded")
                            .setContentText(event.toString());
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
    }
}
