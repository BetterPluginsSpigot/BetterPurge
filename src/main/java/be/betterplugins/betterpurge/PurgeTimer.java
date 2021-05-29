package be.betterplugins.betterpurge;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

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

    /*
    public Dateexample()
    {

        SimpleDateFormat objSDF = new SimpleDateFormat("dd-mm-yyyy");
        Date dt_1 = objSDF.parse("20-08-1981");
        Date dt_2 = objSDF.parse("12-10-2012");

        System.out.println("Date1 : " + objSDF.format(dt_1));
        System.out.println("Date2 : " + objSDF.format(dt_2));

        if (dt_1.compareTo(dt_2) > 0) {
            System.out.println("Date 1 occurs after Date 2");
        } // compareTo method returns the value greater than 0 if this Date is after the Date argument.
        else if (dt_1.compareTo(dt_2) < 0) {
            System.out.println("Date 1 occurs before Date 2");
        } // compareTo method returns the value less than 0 if this Date is before the Date argument;
        else if (dt_1.compareTo(dt_2) == 0) {
            System.out.println("Both are same dates");
        } // compareTo method returns the value 0 if the argument Date is equal to the second Date;
        else {
            System.out.println("You seem to be a time traveller !!");
        }

    }

     */



}


