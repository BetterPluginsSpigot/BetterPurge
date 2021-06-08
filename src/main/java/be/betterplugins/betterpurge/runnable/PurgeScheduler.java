package be.betterplugins.betterpurge.runnable;

import be.betterplugins.betterpurge.listener.ContainerListener;
import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import be.betterplugins.betterpurge.model.PurgeTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * PurgeTimer class
 *
 * Reads in the purge timing settings (out of config.yaml)
 * decides to start or end the purge
 *
 */
public class PurgeScheduler extends BukkitRunnable
{

    private final PurgeStatus purgeStatus;
    private final PurgeConfiguration purgeConfig;
    private final Messenger messenger;
    private final ContainerListener containerListener;
    private final BPLogger logger;
    private final JavaPlugin plugin;

    /**
     * @param config gives the configuration file as parameter
     **/
    public PurgeScheduler(PurgeStatus purgeStatus, PurgeConfiguration config, ContainerListener containerListener, Messenger messenger, BPLogger logger, JavaPlugin plugin)
    {
        this.purgeConfig = config;
        this.messenger = messenger;
        this.purgeStatus = purgeStatus;
        this.containerListener = containerListener;
        this.logger = logger;
        this.plugin = plugin;
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
        PurgeTime purgeStart = purgeStatus.getNextStartTime();
        PurgeTime purgeEnd = purgeStart.addMinutes( purgeConfig.getDuration() );

        // get the time of the config file
        logger.log(Level.FINER,"TIME NOW IS: "          + timeNow);
        logger.log(Level.FINER,"PURGE START IS: "       + purgeStart );
        logger.log(Level.FINER,"PURGE END IS: "         + purgeEnd );
        logger.log(Level.FINER,"Current purge state: "  + purgeStatus.getState() );
        logger.log(Level.FINER,"Is here a purge today: "+ purgeConfig.isDayEnabled( day ) );


        // Enable the purge when in the time slot
        PurgeState state = purgeStatus.getState();
        switch (state)
        {
            case DISABLED:
                // Check if a countdown should start
                PurgeTime startCountdown = purgeStart.subtractMinutes(5);
                PurgeTime stopCountdown = purgeStart.subtractMinutes(1);
                if (purgeConfig.isDayEnabled( day ) && timeNow.isInRange(startCountdown, stopCountdown ))
                {
                    logger.log(Level.FINEST,"Minute countdown");
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(
                            players,
                            "purge_start_countdown",
                            new MsgEntry("<duration>", purgeStart.compareTo( timeNow ))
                    );

                    if (timeNow.compareTo(stopCountdown) >= 0)
                    {
                        logger.log(Level.FINEST,"Starting countdown");
                        this.purgeStatus.setState( PurgeState.COUNTDOWN );
                    }
                }
                else
                {
                    logger.log(Level.FINEST,"No need to count down. Past start time? " + (timeNow.compareTo(startCountdown) >= 0) + ". Before end time? " + (timeNow.compareTo(stopCountdown) <= 0));
                }
                break;
            case COUNTDOWN:
                // Check if the purge should enable
                if (purgeConfig.isDayEnabled( day ) && timeNow.isInRange(purgeStart, purgeEnd))
                {
                    logger.log(Level.FINEST,"Enabling the purge...");

                    new CountdownRunnable(
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
                                        new MsgEntry("<duration>", purgeConfig.getDuration())
                                );
                                purgeStatus.setState( PurgeState.ACTIVE );
                            }
                    ).runTaskTimer(plugin, 0L, 20L);
                }
                else
                {
                    logger.log(Level.FINE,"The purge should not enable? This should never happen");
                }
                break;
            case ACTIVE:

                // Set the current time to the next day if it is sooner than the starting time
                if (timeNow.compareTo( purgeStart ) < 0)
                    timeNow = new PurgeTime(timeNow.getHour(), timeNow.getMinute(), true);

                int remainingMinutes = purgeEnd.compareTo( timeNow );
                if ( remainingMinutes <= 0 )
                {
                    logger.log(Level.FINEST,"Disabling the purge");

                    // Disabling the purge is handled from PurgeStopper
                    this.containerListener.closeAll();
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(players, "purge_disabled");
                    purgeStatus.setState(PurgeState.DISABLED);
                }
                else if ( remainingMinutes <= 5 )
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
                break;
        }
    }

}


