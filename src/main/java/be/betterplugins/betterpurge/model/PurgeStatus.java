package be.betterplugins.betterpurge.model;

import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;

public class PurgeStatus {

    private PurgeState state;
    private PurgeTime overriddenStartTime;
    private final PurgeConfiguration purgeConfiguration;

    public PurgeStatus(PurgeConfiguration purgeConfiguration)
    {
        this.state = PurgeState.DISABLED;

        this.purgeConfiguration = purgeConfiguration;
    }

    public PurgeConfiguration getPurgeConfiguration()
    {
        return purgeConfiguration;
    }

    /**
     * Mark the next purge to start now
     */
    public void setStartNow()
    {
        this.overriddenStartTime = new PurgeTime( LocalTime.now() );
        this.state = PurgeState.COUNTDOWN;
    }

    public PurgeTime getNextStartTime()
    {
        return overriddenStartTime == null ? purgeConfiguration.getConfiguredStartTime() : overriddenStartTime;
    }

    /**
     * When setting the state to DISABLE, the overridden start time will be reset
     *
     * @param state the new state
     */
    public void setState(PurgeState state)
    {
        this.state = state;
        if (state == PurgeState.DISABLED)
            this.overriddenStartTime = null;
    }

    public PurgeState getState()
    {
        return this.state;
    }
}
