package be.betterplugins.betterpurge.model;

public class PurgeStatus {

    private PurgeState state;
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
     * Update the purge's state. Has an effect on the whole plugin!!
     *
     * @param state the new state
     */
    public void setState(PurgeState state)
    {
        this.state = state;
    }

    public PurgeState getState()
    {
        return this.state;
    }
}
