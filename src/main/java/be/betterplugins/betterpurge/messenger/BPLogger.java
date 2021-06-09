package be.betterplugins.betterpurge.messenger;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class BPLogger {

    private Level logLevel;

    /**
     * Different logging levels that we use:
     * During development: ALL
     * During beta testing: FINE
     * During live release: INFO
     *
     * @param logLevel the minimum logging level for messages to be shown
     */
    public BPLogger(Level logLevel)
    {
        this.logLevel = logLevel;
    }

    /**
     * Modify the log level
     * During development: ALL
     * During beta testing: FINE
     * During live release: INFO
     *
     * @param newLevel the new level of the logger
     */
    public void setLogLevel(Level newLevel)
    {
        this.logLevel = newLevel;
    }

    public Level getLogLevel()
    {
        return logLevel;
    }

    /**
     * Pick the correct logging level:
     * SEVERE: Something terribly is wrong, something broke badly or the plugin cannot enable
     * WARNING: An unexpected error happened but the plugin still functions
     * INFO: The server owner should be notified but nothing is wrong
     * CONFIG: Hardware information that is useful during debugging (config file info may use this as well)
     * FINE: Should NOT be displayed on live servers but may be useful for beta testers to track down issues (such as missing language options)
     * FINER: Should ONLY be displayed during development, with a higher priority than FINEST
     * FINEST:Should ONLY be displayed during development, but even then it has a low significance (such as common events)
     *
     * @param level the minimum logging level for messages to be shown
     * @param message the message you want to log
     */
    public void log(Level level, String message)
    {
        // Should BetterMarket log this message?
        if (level.intValue() >= logLevel.intValue() && logLevel != Level.OFF)
        {
            message = "[BetterPurge] " + message;
            if (Bukkit.getLogger().isLoggable(level))
                Bukkit.getLogger().log(level, message);
            else
                Bukkit.getLogger().log(Level.INFO, "[" + level.toString() + "] " + message);
        }
    }
}
