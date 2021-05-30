package be.betterplugins.betterpurge.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DoubleMap<K1, K2, V>
{
    private final Map<K1, V> forwardMap;
    private final Map<K2, V> backwardMap;

    private final Map<K1, K2> fKeyMap;
    private final Map<K2, K1> bKeyMap;

    public DoubleMap()
    {
        this.forwardMap = new HashMap<>();
        this.backwardMap = new HashMap<>();

        this.fKeyMap = new HashMap<>();
        this.bKeyMap = new HashMap<>();
    }

    public void put(K1 fKey, K2 bKey, V value)
    {
        this.forwardMap.put(fKey, value);
        this.backwardMap.put(bKey, value);

        this.fKeyMap.put(fKey, bKey);
        this.bKeyMap.put(bKey, fKey);
    }

    public void clear()
    {
        this.forwardMap.clear();
        this.backwardMap.clear();

        this.fKeyMap.clear();
        this.bKeyMap.clear();
    }

    public Set<K1> keySetForward()
    {
        return this.forwardMap.keySet();
    }

    public Set<K2> keySetBackward()
    {
        return this.backwardMap.keySet();
    }

    public V removeForward(K1 fKey)
    {
        K2 bKey = this.fKeyMap.remove(fKey);
        this.bKeyMap.remove(bKey);
        this.backwardMap.remove(bKey);

        return this.forwardMap.remove(fKey);
    }

    public V removeBackward(K2 bKey)
    {
        K1 fKey = this.bKeyMap.remove(bKey);
        this.fKeyMap.remove(fKey);
        this.forwardMap.remove(fKey);

        return this.backwardMap.remove(bKey);
    }

    public boolean containsForward(K1 key)
    {
        return forwardMap.containsKey( key );
    }

    public boolean containsBackward(K2 key)
    {
        return backwardMap.containsKey( key );
    }

    public V getForward(K1 key)
    {
        return forwardMap.get(key);
    }

    public V getBackward(K2 key)
    {
        return backwardMap.get(key);
    }
}
