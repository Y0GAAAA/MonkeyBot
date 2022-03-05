package commands.invocation;

import java.util.ArrayList;
import java.util.HashMap;

public class MultipleValuesHashMap<K, V> {

    private final HashMap<K, ArrayList<V>> internalHashMap = new HashMap<>();

    public MultipleValuesHashMap(K[] keys) {
        for (K key : keys) {
            internalHashMap.put(key, new ArrayList<>());
        }
    }

    public synchronized boolean add(K key, V value) {
        return internalHashMap.get(key)
                              .add(value);
    }

    public synchronized boolean remove(K key, V value) {
        return internalHashMap.get(key)
                              .remove(value);
    }

    public synchronized boolean contains(K key, V value) {
        return internalHashMap.get(key)
                              .contains(value);
    }

    public synchronized boolean anyFor(K key) {
        return !internalHashMap.get(key).isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (K key : internalHashMap.keySet()) {
            builder.append("Key : ");
            builder.append(key.toString());
            builder.append('\n');
            for (V value : internalHashMap.get(key)) {
                builder.append("\tV : ");
                builder.append(value.toString());
                builder.append('\n');
            }
        }
        
        return builder.toString();
    }

}