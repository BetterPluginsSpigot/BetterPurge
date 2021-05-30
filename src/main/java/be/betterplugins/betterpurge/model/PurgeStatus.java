package be.betterplugins.betterpurge.model;

public class PurgeStatus {

    private PurgeState state;

    public PurgeStatus()
    {
//        this.state = PurgeState.DISABLED;
        this.state = PurgeState.ACTIVE;
    }

    public void setState(PurgeState state)
    {
        this.state = state;
    }

    public PurgeState getState()
    {
        return this.state;
    }
}
