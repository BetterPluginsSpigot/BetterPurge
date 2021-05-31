package be.betterplugins.betterpurge;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import be.betterplugins.betterpurge.model.PurgeTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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

    private final PurgeStatus purgeStatus;
    private final PurgeConfiguration purgeConfig;
    private final Messenger messenger;

    /**
     * @param config gives the configuration file as parameter
     **/
    public PurgeTimer(PurgeStatus purgeStatus, PurgeConfiguration config, Messenger messenger)
    {
        this.purgeConfig = config;
        this.messenger = messenger;
        this.purgeStatus = purgeStatus;
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
                // Check if the purge should enable
                if ( purgeConfig.isDayEnabled( day ) && timeNow.isInRange(purgeStart, purgeEnd))
                {
                    String announcementMessage = ChatColor.RED + "This is the Emergency Broadcast System announcing the commencement of the annual purge. At the siren, all emergency services will be suspended for <duration> minutes. Your government thanks you for your participation.";
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(
                            players,
                            announcementMessage,
                            new MsgEntry("<duration>", purgeConfig.getDuration())
                    );
                }
                break;
            case COUNTDOWN:
                // Start counting down with a separate runnable
                break;
            case ACTIVE:
                // Check if the purge should be disabled
                break;
        }
    }

}


