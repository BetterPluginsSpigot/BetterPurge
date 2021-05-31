package be.betterplugins.betterpurge.model;

import org.bukkit.configuration.file.YamlConfiguration;

public class PurgeConfiguration {

    private final PurgeTime startTime;
    private final int duration;

    private final boolean handleContainers;
    private final boolean overwriteSafezonePvp;
    private final boolean handlePVP;

    public PurgeConfiguration(YamlConfiguration config)
    {
        String startTime = config.getString("start");
        int duration = config.getInt("duration");

        assert startTime != null;
        this.startTime = new PurgeTime( startTime );
        this.duration = duration;

        this.handleContainers = config.getBoolean("enable_chests");
        this.overwriteSafezonePvp = config.getBoolean("overwrite_safezone");
        this.handlePVP = config.getBoolean("enable_pvp");
    }

    public PurgeTime getStartTime()
    {
        return startTime;
    }

    public int getDuration()
    {
        return duration;
    }

    public boolean shouldOverwriteSafezonePvp()
    {
        return overwriteSafezonePvp;
    }

    public boolean shouldHandleContainers()
    {
        return handleContainers;
    }

    public boolean shouldHandlePVP()
    {
        return handlePVP;
    }
}
