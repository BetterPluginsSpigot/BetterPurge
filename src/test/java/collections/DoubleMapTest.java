package collections;

import be.betterplugins.betterpurge.collections.DoubleMap;
import org.junit.jupiter.api.Test;

public class DoubleMapTest {

    @Test
    public void testCustomMap()
    {
        DoubleMap<String, String, Integer> map = new DoubleMap<>();

        // No false positives when using contains
        assert !map.containsForward("test");
        assert !map.containsBackward("test");

        String forwardKey = "testF";
        String backwardKey = "testB";

        // Add an element into the map
        map.put(forwardKey, backwardKey, 12);

        // Check that the forward key can only be used in forward, and backward only in backward
        assert map.containsForward(forwardKey);
        assert !map.containsForward(backwardKey);
        assert map.containsBackward(backwardKey);
        assert !map.containsBackward(forwardKey);

        // Remove the entry by its forward key and check if the internal workings are adjusted correctly
        assert map.removeForward(forwardKey) == 12;
        assert !map.containsForward(forwardKey);
        assert !map.containsBackward(backwardKey);

        // Add an element into the map
        String forwardKey2 = "testF2";
        String backwardKey2 = "testB2";
        map.put(forwardKey2, backwardKey2, 24);

        // Make sure the entry is added
        assert map.containsForward(forwardKey2);
        assert map.containsBackward(backwardKey2);

        // Remove the entry by its backward key
        assert map.removeBackward(backwardKey2) == 24;

        // Check if forward entry is deleted as well
        assert !map.containsForward(forwardKey2);
        assert !map.containsBackward(backwardKey2);
    }

}
