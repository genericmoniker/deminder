package net.esmithy.deminder;

import java.util.Date;

/**
 * User: Eric
 * Date: 5/4/13
 * Time: 2:20 PM
 */
public class Event {
    public String title;
    public long start;

    public Event(String title, long start) {
        this.title = title;
        this.start = start;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
