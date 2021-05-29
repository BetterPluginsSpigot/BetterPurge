package be.betterplugins.betterpurge.model;

import org.bukkit.configuration.file.YamlConfiguration;

public class PurgeConfiguration {

    private final PurgeTime startTime;
    private final int duration;

    public PurgeConfiguration(YamlConfiguration config)
    {
        String startTime = config.getString("start");
        int duration = config.getInt("duration");

        assert startTime != null;
        this.startTime = new PurgeTime( startTime );
        this.duration = duration;
    }

    public PurgeConfiguration(PurgeTime startTime, int duration)
    {
        assert duration > 0;

        this.startTime = startTime;
        this.duration = duration;
    }

    public PurgeTime getStartTime()
    {
        return startTime;
    }

    public int getDuration()
    {
        return duration;
    }
}
