package net.esmithy.deminder;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

/**
 * User: Eric
 * Date: 5/1/13
 * Time: 8:56 PM
 */
public class DeminderService extends Service {
    private static final String DEBUG_TAG = "DeminderService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DeminderTask extends AsyncTask<Object, Void, Void> {
        private static final String DEBUG_TAG = "DeminderService$DeminderTask";

        @Override
        protected Void doInBackground(Object... objects) {
            return null;
        }
    }
}
