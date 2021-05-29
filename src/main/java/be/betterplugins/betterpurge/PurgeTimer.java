package be.betterplugins.betterpurge;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import be.betterplugins.betterpurge.model.PurgeTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
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
    private final PurgeConfiguration purgeConfiguration;
    private final Messenger messenger;

    /**
     * @param config gives the configuration file as parameter
     **/
    public PurgeTimer(PurgeStatus purgeStatus, PurgeConfiguration config, Messenger messenger)
    {
        this.purgeConfiguration = config;
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
        PurgeTime timeNow = new PurgeTime( LocalTime.now() );

        // Get the purge starting time
        PurgeTime purgeStart = purgeConfiguration.getStartTime();
        PurgeTime purgeEnd = purgeStart.addMinutes( purgeConfiguration.getDuration() );

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
                if (timeNow.compareTo( purgeStart ) >= 0 && timeNow.compareTo( purgeEnd ) <= 0)
                {
                    String announcementMessage = ChatColor.RED + "This is the Emergency Broadcast System announcing the commencement of the annual purge. At the siren, all emergency services will be suspended for <duration> minutes. Your government thanks you for your participation.";
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    messenger.sendMessage(
                            players,
                            announcementMessage,
                            new MsgEntry("<duration>", purgeConfiguration.getDuration())
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


