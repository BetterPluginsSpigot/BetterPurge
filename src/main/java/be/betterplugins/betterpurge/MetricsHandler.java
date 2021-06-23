package be.betterplugins.betterpurge;


import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MetricsHandler
{

    public MetricsHandler(final JavaPlugin plugin, @Nullable final YamlConfiguration configuration)
    {
        Metrics metrics = new Metrics(plugin, 11800);

        if (configuration == null)
            return;

        // Language drilldown pie
        String langSetting = configuration.getString("lang");
        final String lang = langSetting == null ? "en-US" : langSetting;
        metrics.addCustomChart(
            new DrilldownPie("language", () ->
                {
                    Map<String, Map<String, Integer>> map = new HashMap<>();
                    Map<String, Integer> entry = new HashMap<>();

                    final String main = lang.equalsIgnoreCase("en-US") ? "en-US" : "other";

                    entry.put(lang, 1);
                    map.put(main, entry);
                    return map;
                }
            )
        );

        // Purge duration pie
        metrics.addCustomChart(new SimplePie("purge_duration", () -> "" + configuration.getInt("duration")));

        // Enable chests pie
        metrics.addCustomChart(new SimplePie("enable_chests", () -> configuration.getBoolean("enable_chests") ? "Yes" : "No"));

        // Handle PVP pie
        metrics.addCustomChart(new SimplePie("enable_pvp", () -> configuration.getBoolean("handle_pvp") ? "Yes" : "No"));

        // Overwrite safezones pie
        metrics.addCustomChart(new SimplePie("overwrite_safezones", () -> configuration.getBoolean("overwrite_safezone") ? "Yes" : "No"));
    }

}
