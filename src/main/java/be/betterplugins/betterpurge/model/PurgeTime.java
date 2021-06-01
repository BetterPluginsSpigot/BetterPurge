package be.betterplugins.betterpurge.model;

import java.time.LocalTime;

public class PurgeTime implements Comparable<PurgeTime> {

    private final boolean isNextDay;
    private final int hour;
    private final int minute;

    public PurgeTime(int hour, int minute)
    {
        this(hour, minute, false);
    }

    public PurgeTime(int hour, int minute, boolean isNextDay)
    {
        this.isNextDay = isNextDay;

        this.hour = hour;
        this.minute = minute;
    }

    public PurgeTime(LocalTime time)
    {
        this.isNextDay = false;

        this.hour = time.getHour();
        this.minute = time.getMinute();
    }

    public PurgeTime(String serialisedTime)
    {
        this.isNextDay = false;

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
        assert minutes >= 0;

        int addedMinutes = getMinute() + minutes;
        int newHours = getHour() + addedMinutes / 60;
        int newMinutes = addedMinutes % 60;

        boolean isNextDay = newHours / 24 > 0;
        newHours = newHours % 24;

        return new PurgeTime(newHours, newMinutes, isNextDay);
    }

    public PurgeTime subtractMinutes( int minutes )
    {
        assert minutes < 60 && minutes >= 0;

        int newMinutes = getMinute() - minutes;
        int newHours;
        if (newMinutes < 0)
        {
            newHours = getHour() - 1;
            newMinutes += 60;
        }
        else
        {
            newHours = getHour();
        }

        return new PurgeTime(newHours, newMinutes);
    }

    public boolean isInRange(PurgeTime start, PurgeTime stop)
    {
        return this.compareTo( start ) >= 0 && this.compareTo( stop ) <= 0;
    }

    public boolean isNextDay()
    {
        return isNextDay;
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
        int thisIsNextDayValue = this.isNextDay() ? 1 : 0;
        int oIsNextDayValue = o.isNextDay() ? 1 : 0;

        int dayValue = thisIsNextDayValue - oIsNextDayValue;
        return (dayValue * 1140) + 60 * ( getHour() - o.getHour() ) + getMinute() - o.getMinute();
    }

}
