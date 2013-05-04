package net.esmithy.deminder;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Eric
 * Date: 4/24/13
 * Time: 7:34 PM
 */
public class EventExperiments {

    private ContentResolver contentResolver;

    public EventExperiments(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public ArrayList<Long> getCalendarIds() {
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] projection = new String[] {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
        };

        Cursor calendarCursor = contentResolver.query(uri, projection, null, null, null);
        ArrayList<Long> ids = new ArrayList<Long>();
        while (calendarCursor.moveToNext()) {
            String name = calendarCursor.getString(3);
            ids.add(calendarCursor.getLong(0));
        }

        return ids;
    }

    public void doEventsAndInstancesHaveTheSameId() {
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = String.format("(%s = ?)",CalendarContract.Events.TITLE);
        String[] selectionArgs = new String[] { "My birthday" };
        String[] projection = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            if (id == 0) {}
        }

    }


    public void eventsForNext24Hours() {
        Date now = new Date();
        long timeNow = now.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 7); // OK, 7 days not 24 hours
        long timeThroughTomorrow = calendar.getTimeInMillis();

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(eventsUriBuilder, timeNow);
        ContentUris.appendId(eventsUriBuilder, timeThroughTomorrow);
        Uri eventsUri = eventsUriBuilder.build();
        String[] projection = new String[] {
                CalendarContract.Instances._ID,
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.DTSTART,
                CalendarContract.Instances.EVENT_ID
        };
        String selection = String.format("(%s = ?)", CalendarContract.Instances.ALL_DAY);
        String[] selectionArgs = new String[] { "1" };
        Cursor cursor = contentResolver.query(eventsUri, projection, selection, selectionArgs, CalendarContract.Instances.DTSTART + " ASC");
        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            long start = cursor.getLong(2);
            long eventId = cursor.getLong(3);
            Date startDate = new Date(start);

            removeReminders2(eventId, title, startDate);
        }
        cursor.close();
    }

    public void demindAllDayEvents() {
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = String.format("(%s = ?)",CalendarContract.Events.ALL_DAY);
        String[] selectionArgs = new String[] { "1" };
        String[] projection = new String[] {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART
        };
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);
        while (cursor.moveToNext())
        {
            String title = cursor.getString(1);
            long start = cursor.getLong(2);
            Date startDate = new Date(start);
            long id = cursor.getLong(0);

            removeReminders2(id, title, startDate);
        }
        cursor.close();
    }

    /*
    From StackOverflow, but doesn't seem to work.
     */
    private void removeReminders(long id, String title, Date startDate) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.HAS_ALARM, 0);

        int result = contentResolver.update(
                CalendarContract.Events.CONTENT_URI,
                values,
                CalendarContract.Events._ID + " = ?",
                new String[]{Long.toString(id)}
        );

        if (result > 0) {
            // report that we deminded
        }
    }

    private void removeReminders2(long eventId, String title, Date startDate) {
        String[] projection = new String[] {
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.MINUTES
        };

        Cursor cursor = CalendarContract.Reminders.query(contentResolver, eventId, projection);
        while (cursor.moveToNext()) {
            long reminderId = cursor.getLong(0);
            int method = cursor.getInt(1);
            int minutes = cursor.getInt(2);

            // How to delete this reminder?
            Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, reminderId);
            int rows = contentResolver.delete(reminderUri, null, null);
            if (rows > 0) {

            }
        }
        cursor.close();

        // I think this may delete events or instances, not reminders...
//        String selection = String.format("(%s = ?)",CalendarContract.Events._ID);
//        String[] selectionArgs = new String[] { Long.toString(event_id) };
//        int rows = contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, selection, selectionArgs);
//        if (rows > 0) {
//
//        }

        // Maybe something like this? Apparently not, since reminders don't have IDs.
        //contentResolver.delete(ContentUris.withAppendedId(remindersUri, reminderId))

        // Reminders:
        // http://developer.android.com/reference/android/provider/CalendarContract.Reminders.html

        // Maybe a hint here:
        // http://developer.android.com/guide/topics/providers/calendar-provider.html#add-reminders
    }
}
