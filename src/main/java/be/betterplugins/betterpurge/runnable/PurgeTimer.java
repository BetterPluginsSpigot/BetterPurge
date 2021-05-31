package be.betterplugins.betterpurge.runnable;

import be.betterplugins.betterpurge.listener.ContainerListener;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import be.betterplugins.betterpurge.model.PurgeTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;


/**
 * PurgeTimer class
 *
 * Reads in the purge timing settings (out of config.yaml)
 * decides to start or end the purge
 *
 */
public class PurgeTimer extends BukkitRunnable
{

    private final JavaPlugin plugin;
    private final PurgeStatus purgeStatus;
    private final PurgeConfiguration purgeConfig;
    private final Messenger messenger;
    private final ContainerListener containerListener;

    /**
     * @param config gives the configuration file as parameter
     **/
    public PurgeTimer(PurgeStatus purgeStatus, PurgeConfiguration config, ContainerListener containerListener, Messenger messenger, JavaPlugin plugin)
    {
        this.purgeConfig = config;
        this.messenger = messenger;
        this.purgeStatus = purgeStatus;
        this.plugin = plugin;
        this.containerListener = containerListener;
    }

    /**
     * Handles the purge state based on time
     */
    @Override
    public void run()
    {
        // Get the current time
        LocalDateTime localDateTime = LocalDateTime.now();
        PurgeTime timeNow = new PurgeTime( localDateTime.toLocalTime() );
        DayOfWeek day = localDateTime.getDayOfWeek();

        // Get the purge starting time
        PurgeTime purgeStart = purgeConfig.getStartTime();
        PurgeTime purgeEnd = purgeStart.addMinutes( purgeConfig.getDuration() );

        // get the time of the config file
        getServer().getConsoleSender().sendMessage("TIME NOW IS: "+timeNow);
        getServer().getConsoleSender().sendMessage("PURGE START IS: "+ purgeStart );
        getServer().getConsoleSender().sendMessage("PURGE END IS: "+ purgeEnd );

        // Enable the purge when in the time slot
        PurgeState state = purgeStatus.getState();
        switch (state)
        {
            case DISABLED:
            case COUNTDOWN:
                // Check if the purge should enable
                if (purgeConfig.isDayEnabled( day ))
                {
                    if (timeNow.isInRange(purgeStart, purgeEnd))
                    {
                        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                        messenger.sendMessage(
                                players,
                                "purge_start",
                                new MsgEntry("<duration>", purgeConfig.getDuration())
                        );

                        purgeStatus.setState( PurgeState.ACTIVE );
                        PurgeStopper purgeStopper = new PurgeStopper(purgeStatus, containerListener, messenger);
                        purgeStopper.runTaskLater(plugin, purgeConfig.getDuration() * 1200);    // x1200 (from ticks to minutes)
                    }
                    else if (timeNow.isInRange( purgeStart.subtractMinutes(5), purgeStart ))
                    {
                        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                        messenger.sendMessage(
                                players,
                                "purge_countdown",
                                new MsgEntry("<duration>", purgeStart.compareTo( timeNow ))
                        );
                    }
                }
                break;
            case ACTIVE:
                // Disabling the purge is handled from PurgeStopper
                break;
        }
    }

}


