package be.betterplugins.betterpurge;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * BetterPurge Plugin
 * @author Thomas Verschoor
 *
 **/
public class BetterPurge extends JavaPlugin
{

    // run this code when plugin should is enabled
    @Override
    public void onEnable()
    {
        // display a plugin enabled message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BetterPurge plugin enabled");

    }

    // run this code when plugin should be disabled
    @Override
    public void onDisable()
    {
        // display a plugin disabled message
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "BetterPurge plugin disabled");

    }



}
