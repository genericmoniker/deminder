package net.esmithy.deminder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class for manipulating event reminders.
 *
 * User: Eric
 * Date: 5/4/13
 * Time: 2:19 PM
 */
public class ReminderManager {
    private Context mContext;

    public ReminderManager(Context context) {
        this.mContext = context;
    }

    /**
     * Removes reminders for "all day" events within a number of days in the future.
     *
     * @param daysToSearch number of days into the future to search.
     * @return list of events with modified reminders.
     */
    public ArrayList<Event> demindAllDayEvents(int daysToSearch) {
        ArrayList<Event> results = new ArrayList<Event>();
        ArrayList<EventInstance> instances = searchForEventInstances(daysToSearch);
        for (EventInstance instance : instances) {
            int remindersDeleted = deleteRemindersForEventInstance(instance);
            if (remindersDeleted > 0) {
                results.add(new Event(instance.title, instance.start));
            }
        }

        return results;
    }

    /**
     * Searches for "all day" event instances within a number of days in the future.
     *
     * @param daysToSearch number of days into the future to search.
     * @return found event instances.
     */
    private ArrayList<EventInstance> searchForEventInstances(int daysToSearch) {
        ArrayList<EventInstance> results = new ArrayList<EventInstance>();
        Uri searchUri = getEventInstancesSearchUri(daysToSearch);
        String[] projection = new String[] {
                CalendarContract.Instances._ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DTSTART,
                CalendarContract.Instances.EVENT_ID
        };
        String selection = String.format("(%s = ?)", CalendarContract.Instances.ALL_DAY);
        String[] selectionArgs = new String[] { "1" };
        String sort = CalendarContract.Instances.DTSTART + " ASC";
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(searchUri, projection, selection, selectionArgs, sort);
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            long start = cursor.getLong(2);
            long eventId = cursor.getLong(3);
            results.add(new EventInstance(id, title, start, eventId));
        }
        cursor.close();
        return results;
    }

    /**
     * Deletes all the event reminders for an event instance.
     *
     * @param instance event instance for which to delete reminders.
     * @return the number of reminders deleted.
     */
    private int deleteRemindersForEventInstance(EventInstance instance) {
        int remindersDeleted = 0;
        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<Reminder> reminders = searchForReminders(instance.eventId);
        for (Reminder reminder : reminders) {
            Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminder.id);
            int rows = resolver.delete(reminderUri, null, null);
            remindersDeleted += rows;
        }
        return remindersDeleted;
    }

    /**
     * Finds reminders for a given event.
     *
     * @param eventId id of the event for which to find reminders.
     * @return list of found reminders.
     */
    private ArrayList<Reminder> searchForReminders(long eventId) {
        ArrayList<Reminder> results = new ArrayList<Reminder>();
        String[] projection = new String[] {
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.MINUTES
        };

        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = CalendarContract.Reminders.query(resolver, eventId, projection);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            int method = cursor.getInt(1);
            int minutes = cursor.getInt(2);

            results.add(new Reminder(id, method, minutes));
        }
        cursor.close();

        return results;
    }

    /**
     * Gets a URI to search for event instances within a time range.
     *
     * @param daysToSearch days from now to search.
     * @return URI suitable for querying the event instances table.
     */
    private Uri getEventInstancesSearchUri(int daysToSearch) {
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        Calendar now = Calendar.getInstance();
        ContentUris.appendId(builder, now.getTimeInMillis());
        now.add(Calendar.DATE, daysToSearch);
        ContentUris.appendId(builder, now.getTimeInMillis());
        return builder.build();
    }
}
