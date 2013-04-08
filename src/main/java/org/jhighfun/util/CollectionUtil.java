package org.jhighfun.util;

import java.util.*;

public class CollectionUtil {
    public static <T> List<T> List(T... args) {
        final List<T> argsList = new LinkedList<T>();
        for (T arg : args) {
            argsList.add(arg);
        }
        return argsList;
    }

    public static List List(List... listArgs) {
        final List flattenList = new LinkedList();
        for (List list : listArgs) {
            for (Object obj : list) {
                flattenList.add(obj);
            }
        }
        return flattenList;
    }

    public static <T> Set<T> Set(T... args) {
        final Set<T> set = new HashSet<T>();
        for (T arg : args) {
            set.add(arg);
        }
        return set;
    }

    public static Set Set(Set... setArgs) {
        final Set flattenSet = new HashSet();
        for (Set list : setArgs) {
            for (Object obj : list) {
                flattenSet.add(obj);
            }
        }
        return flattenSet;
    }

    public static <K, V> Map<K, V> Map(Entry<K, V>... entries) {
        final Map<K, V> map = new HashMap<K, V>();
        for (Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static Map Map(Map first, Map... maps) {
        final Map flattenMap = new HashMap();
        flattenMap.putAll(first);
        for (Map map : maps) {
            flattenMap.putAll(map);
        }
        return flattenMap;
    }

    public static <K, V> Entry<K, V> Entry(K key, V value) {
        return new Entry<K, V>(key, value);
    }
}