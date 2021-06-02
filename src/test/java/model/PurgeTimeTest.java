package model;

import be.betterplugins.betterpurge.model.PurgeTime;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurgeTimeTest {

    @Test
    public void testConstructor()
    {
        LocalTime timeMock = mock(LocalTime.class);
        when(timeMock.getHour()).thenReturn(9);
        when(timeMock.getMinute()).thenReturn(14);
        PurgeTime localTimeTime = new PurgeTime(timeMock);
        assert localTimeTime.getHour() == 9;
        assert localTimeTime.getMinute() == 14;

        PurgeTime purgeTime = new PurgeTime("04:37");
        assert purgeTime.getHour() == 4;
        assert purgeTime.getMinute() == 37;
    }

    @Test
    public void testAddTime()
    {
        PurgeTime purgeTime = new PurgeTime(9, 24);
        PurgeTime result = purgeTime.addMinutes( 15 );
        assert result.getHour() == 9;
        assert result.getMinute() == 39;
    }

    @Test
    public void testAddTimeOverflow()
    {
        PurgeTime purgeTime = new PurgeTime(9, 55);
        PurgeTime result = purgeTime.addMinutes( 15 );
        assert result.getHour() == 10;
        assert result.getMinute() == 10;
    }

    @Test
    public void testDayOverflow()
    {
        PurgeTime start = new PurgeTime(23, 55, false);
        PurgeTime end = start.addMinutes(10);

        assert end.getHour() == 0;
        assert end.getMinute() == 5;
        assert end.isNextDay();

        assert end.compareTo(start) > 0;
    }
}
