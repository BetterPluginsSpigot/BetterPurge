package model;

import be.betterplugins.betterpurge.model.PurgeConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurgeConfigurationTest
{

    @Test
    public void testConfigConstructor()
    {
        YamlConfiguration config = mock(YamlConfiguration.class);
        when(config.getString("start")).thenReturn("15:38");
        when(config.getInt("duration")).thenReturn(10);

        PurgeConfiguration purgeConfiguration = new PurgeConfiguration(config);
        assert purgeConfiguration.getDuration() == 10;
        assert purgeConfiguration.getStartTime().getHour() == 15;
        assert purgeConfiguration.getStartTime().getMinute() == 38;
    }

}
