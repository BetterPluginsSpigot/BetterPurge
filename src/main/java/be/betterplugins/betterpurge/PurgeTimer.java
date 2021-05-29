package be.betterplugins.betterpurge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalTime;

import static org.bukkit.Bukkit.getServer;


/**
 * PurgeTimer class
 *
 * Reads in the purge timing settings (out of config.yaml)
 * decides to start or end the purge
 *
 */
public class PurgeTimer
{

    private final YamlConfiguration config;

    /**
     * @param config gives the configuration file as parameter
     **/

    public PurgeTimer(YamlConfiguration config)
    {
        this.config = config;
    }

    /**
     * checks the times
     **/
    public void checkTimings()
    {
        // get the time now
        String timeNow = getTimeNow();

        // get the time of the config file
        String timeConfig = config.getString("start");
        getServer().getConsoleSender().sendMessage("TIME NOW IS: "+timeNow);
        getServer().getConsoleSender().sendMessage("TIME CONFIG IS: "+timeConfig);

        // start of the purge when timings are the same
        if(timeConfig.equals(timeNow))
        {
            String announcementMessage = "This is the Emergency Broadcast System announcing the commencement of the annual purge. At the siren, all emergency services will be suspended for "+config.getInt("duration")+" hours. Your government thanks you for your participation.";

            // send announcement message to all players on server
            for(Player p : Bukkit.getOnlinePlayers()) {

                // send message to the player
                p.sendMessage(ChatColor.RED +announcementMessage);
            }

        }

    }

    /**
     * @return time at moment of function (in String format)
     **/
    public String getTimeNow()
    {
        // get current time in LocalTime format
        LocalTime localTimeNow = LocalTime.now();

        // convert to String, hour and minute format
        String timeNow = localTimeNow.getHour() +""+ localTimeNow.getMinute();

        return timeNow;

    }

}


