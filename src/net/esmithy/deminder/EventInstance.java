package net.esmithy.deminder;

import java.util.Date;

/**
 * User: Eric
 * Date: 5/4/13
 * Time: 2:50 PM
 */
public class EventInstance {
    public long id;
    public String title;
    public long start;
    public long eventId;

    public EventInstance(long id, String title, long start, long eventId) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
