package net.esmithy.deminder;

import android.app.Activity;
import android.os.Bundle;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AlarmBroadcastReceiver alarmReceiver = new AlarmBroadcastReceiver();
        //alarmReceiver.setAlarmNow(this);
        alarmReceiver.setAlarm(this);
    }
}

