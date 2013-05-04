package net.esmithy.deminder;

/**
 * User: Eric
 * Date: 5/4/13
 * Time: 3:05 PM
 */
public class Reminder {
    public long id;
    public int method;
    public int minutes;

    public Reminder(long id, int method, int minutes) {
        this.id = id;
        this.method = method;
        this.minutes = minutes;
    }
}
