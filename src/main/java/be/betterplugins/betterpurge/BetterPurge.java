package be.betterplugins.betterpurge;

import be.betterplugins.betterpurge.listener.ContainerListener;
import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeStatus;
import be.dezijwegel.betteryaml.OptionalBetterYaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Level;

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

        BPLogger logger = new BPLogger(Level.ALL);

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

        PurgeStatus purgeStatus = new PurgeStatus();
        PurgeConfiguration purgeConfig = new PurgeConfiguration(config);
        Messenger messenger = new Messenger(new HashMap<>(), logger, true);

        // start a Purge timer
        PurgeTimer purgetimer = new PurgeTimer(purgeStatus, purgeConfig, messenger);

        // run every mochnute
        purgetimer.runTaskTimer(this, 0L, 1200L);

        ContainerListener containerListener = new ContainerListener(purgeStatus, messenger, logger);
        Bukkit.getServer().getPluginManager().registerEvents(containerListener, this );
    }

    // run this code when plugin should be disabled
    @Override
    public void onDisable()
    {

    }



}
