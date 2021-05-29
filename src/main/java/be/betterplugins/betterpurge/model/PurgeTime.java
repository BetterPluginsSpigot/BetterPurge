package be.betterplugins.betterpurge.model;

import java.time.LocalTime;

public class PurgeTime implements Comparable<PurgeTime> {

    private final int hour;
    private final int minute;

    public PurgeTime(int hour, int minute)
    {
        this.hour = hour;
        this.minute = minute;
    }

    public PurgeTime(LocalTime time)
    {
        this.hour = time.getHour();
        this.minute = time.getMinute();
    }

    public PurgeTime(String serialisedTime)
    {
        String[] components = serialisedTime.split(":");
        if (components.length < 2)
            throw new IllegalArgumentException("The string '" + serialisedTime + "' cannot be parsed into a HH:MM format");

        hour = Integer.parseInt( components[0] );
        minute = Integer.parseInt( components[1] );
    }

    /**
     * PurgeTime fields are immutable. This is solved with the builder pattern by returning a new object whenever this function is called
     * The original object is NOT modified!!
     *
     * @param minutes the amount of minutes to be added
     * @return a new PurgeTime object that has been advanced `minutes` minutes
     */
    public PurgeTime addMinutes( int minutes )
    {
        int addedMinutes = getMinute() + minutes;
        int newHours = getHour() + addedMinutes / 60;
        int newMinutes = addedMinutes % 60;

        return new PurgeTime(newHours, newMinutes);
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    @Override
    public String toString()
    {
        return hour + ":" + minute;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof PurgeTime))
            return false;

        PurgeTime otherTime = (PurgeTime) obj;
        return getHour() == otherTime.getHour() && getMinute() == otherTime.getMinute();
    }

    @Override
    public int compareTo(PurgeTime o)
    {
        if (getHour() == o.getHour())
        {
            if (getMinute() == o.getMinute())
                return 0;
            else
                return getMinute() > o.getMinute() ? 1 : -1;
        }
        else
        {
            return getHour() > o.getHour() ? 1 : -1;
        }
    }
}
