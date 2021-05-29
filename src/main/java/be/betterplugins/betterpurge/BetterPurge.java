package be.betterplugins.betterpurge;

import be.dezijwegel.betteryaml.BetterYaml;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

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
        // BetterYaml-config implementation
        YamlConfiguration config = new YamlConfiguration();
        try {
            BetterYaml betterYaml = new BetterYaml("config.yml", this, true);
            config = betterYaml.getYamlConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
