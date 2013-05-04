package net.esmithy.deminder;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ReminderManager reminderManager = new ReminderManager(this);
        ArrayList<Event> modifiedEvents = reminderManager.demindAllDayEvents(7);
    }
}

