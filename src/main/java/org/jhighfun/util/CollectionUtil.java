package org.jhighfun.util;

import org.jhighfun.internal.Pair;

import java.util.*;

public class CollectionUtil {
    public static <T> List<T> List(T... args) {
        List<T> argsList = new LinkedList<T>();
        for (T arg : args) {
            argsList.add(arg);
        }
        return argsList;
    }

    public static List List(List... listArgs) {
        List flattenList = new LinkedList();
        for (List list : listArgs) {
            for (Object obj : list) {
                flattenList.add(obj);
            }
        }
        return flattenList;
    }

    public static <T> Set<T> Set(T... args) {
        Set<T> set = new HashSet<T>();
        for (T arg : args) {
            set.add(arg);
        }
        return set;
    }

    public static Set Set(Set... setArgs) {
        Set flattenSet = new HashSet();
        for (Set list : setArgs) {
            for (Object obj : list) {
                flattenSet.add(obj);
            }
        }
        return flattenSet;
    }

    public static <K, V> Map<K, V> Map(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<K, V>();
        for (Pair<K, V> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    public static Map Map(Map first, Map... maps) {
        Map flattenMap = new HashMap();
        flattenMap.putAll(first);
        for (Map map : maps) {
            flattenMap.putAll(map);
        }
        return flattenMap;
    }

    public static <K, V> Pair<K, V> Entry(K key, V value) {
        return new Pair<K, V>(key, value);
    }
}