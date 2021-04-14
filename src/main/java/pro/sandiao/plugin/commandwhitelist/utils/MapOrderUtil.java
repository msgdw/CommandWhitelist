package pro.sandiao.plugin.commandwhitelist.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class MapOrderUtil<K, V> {

    private LinkedList<InnerOrder<MapEntry<K, V>>> list = new LinkedList<>();

    public void add(int order, K key, V value) {
        MapEntry<K, V> entry = new MapEntry<>(key, value);
        InnerOrder<MapEntry<K, V>> innerOrder = new InnerOrder<>(order, entry);

        boolean insert = false;
        for (int i = 0; i < list.size(); i++) {
            if (order < list.get(i).order) {
                list.add(i, innerOrder);
                insert = true;
                break;
            }
        }

        if (!insert)
            list.add(innerOrder);
    }

    public Map<K, V> buildMap() {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        list.forEach(e -> map.put(e.object.key, e.object.value));
        return map;
    }

    static private class MapEntry<K, V> {
        private K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    static private class InnerOrder<T> {
        private int order;
        private T object;

        public InnerOrder(int order, T t) {
            this.order = order;
            this.object = t;
        }
    }
}
