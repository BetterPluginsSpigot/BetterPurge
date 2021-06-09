package be.betterplugins.betterpurge.model;

import be.betterplugins.betterpurge.listener.ContainerListener;
import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.runnable.CountdownRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PurgeHandler
{

    private final PurgeStatus purgeStatus;
    private final ContainerListener containerListener;
    private final PurgeConfiguration purgeConfig;
    private final JavaPlugin plugin;
    private final Messenger messenger;
    private final BPLogger logger;

    private CountdownRunnable startCounter;
    private CountdownRunnable stopCounter;

    public PurgeHandler(PurgeStatus purgeStatus, ContainerListener containerListener, PurgeConfiguration purgeConfig, Messenger messenger, BPLogger logger, JavaPlugin plugin)
    {
        this.purgeStatus = purgeStatus;
        this.containerListener = containerListener;
        this.purgeConfig = purgeConfig;
        this.plugin = plugin;
        this.messenger = messenger;
        this.logger = logger;
    }

    public PurgeStatus getPurgeStatus()
    {
        return purgeStatus;
    }

    /**
     * Start the purge and end it at the duration from the settings
     */
    public void startPurge()
    {
        this.startPurge(0);
    }

    /**
     * Start the purge and automatically end it after a given amount of minutes
     *
     * @param duration the duration in minutes
     */
    public void startPurge(int duration)
    {
        if (purgeStatus.getState() == PurgeState.ACTIVE)
        {
            logger.log(Level.FINE, "Tried enabling the purge while it was already active");
            return;
        }

        purgeStatus.setState( PurgeState.COUNTDOWN );

        logger.log(Level.FINEST,"Enabling the purge...");

        int purgeDuration = duration > 0 ? duration : purgeConfig.getDuration();

        startCounter = new CountdownRunnable(
                10,
                (int count) -> {
                    logger.log(Level.FINEST, "Countdown: " + count);
                    // SHOW 10 second countdown
                    String message = messenger.composeMessage(
                            "seconds_countdown",
                            new MsgEntry("<duration>", count)
                    );
                    if (!message.equals(""))
                        for (Player player : Bukkit.getOnlinePlayers())
                            player.sendTitle("", message, 5, 10, 5);
                },
                (int count) -> {
                    logger.log(Level.FINEST, "Countdown done: " + count);
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(
                            players,
                            "purge_start",
                            new MsgEntry("<duration>", purgeDuration)
                    );
                    containerListener.closeAll();
                    purgeStatus.setState( PurgeState.ACTIVE );
                    this.stopPurge( purgeDuration );
                }
        );
        startCounter.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * Stop the purge at once
     */
    public boolean stopPurge()
    {
        if (purgeStatus.getState() == PurgeState.DISABLED)
        {
            logger.log(Level.FINE, "Tried disabling the purge while it was already disabled");
            return false;
        }

        logger.log(Level.FINEST,"Disabling the purge");

        cancelCounter(startCounter);
        cancelCounter(stopCounter);

        // Close all opened purge inventories
        this.containerListener.closeAll();
        // Notify all players
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        messenger.sendMessage(players, "purge_ended");
        // Update the purge's state
        purgeStatus.setState(PurgeState.DISABLED);
        return true;
    }


    /**
     * Stop the purge after a given delay in minutes
     *
     * @param minutes the amount of minutes until the purge should disable
     */
    public void stopPurge(int minutes)
    {
        cancelCounter(stopCounter);

        stopCounter = new CountdownRunnable(
            minutes,
            (int remainingMinutes) ->
            {
                if (remainingMinutes <= 5)
                {
                    logger.log(Level.FINEST,"Almost disabling the purge, but not yet");
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(
                            players,
                            "purge_end_countdown",
                            new MsgEntry("<duration>", remainingMinutes)
                    );
                }
                else
                {
                    logger.log(Level.FINEST,"The purge has more than 5 minutes remaining");
                }
            },
            (int zero) ->
            {
                logger.log(Level.FINEST,"Disabling purge because the countdown reached zero");
                this.stopPurge();
            }
        );
        stopCounter.runTaskTimer(plugin, 0L, 1200L);
    }


    private void cancelCounter(CountdownRunnable counter)
    {
        if (counter != null)
        {
            try
            {
                counter.cancel();
            }
            catch (IllegalStateException ignored) {}
        }
    }
}
