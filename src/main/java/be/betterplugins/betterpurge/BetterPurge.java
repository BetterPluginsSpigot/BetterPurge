package be.betterplugins.betterpurge;

import be.dezijwegel.betteryaml.BetterYaml;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Optional;

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
        OptionalBetterYaml betterYaml = new OptionalBetterYaml("config.yml", this, true);
        Optional<YamlConfiguration> optionalConfig = betterYaml.getYamlConfiguration();

        // Disable the plugin & prevent further code execution if a config error happens (this should never happen)
        if (!optionalConfig.isPresent())
        {
            Bukkit.getLogger().severe(ChatColor.RED + "BetterPurge cannot enable due to a configuration error, please contact the developer");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        YamlConfiguration config = optionalConfig.get();

        // display a plugin enabled message
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BetterPurge plugin enabled");

        // start a Purge timer
        PurgeTimer purgetimer = new PurgeTimer(config);

        // check the timing
        purgetimer.checkTimings();
    }

    // run this code when plugin should be disabled
    @Override
    public void onDisable()
    {

        // display a plugin disabled message
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "BetterPurge plugin disabled");

    }



}
