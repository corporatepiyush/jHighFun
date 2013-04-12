package org.jhighfun.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionUtil {
    public static <T> List<T> List(T... args) {
        final List<T> argsList = new LinkedList<T>();
        for (T arg : args) {
            argsList.add(arg);
        }
        return argsList;
    }

    public static <T> List<T> SafeList(T... args) {
        final List<T> argsList = new CopyOnWriteArrayList<T>();
        for (T arg : args) {
            argsList.add(arg);
        }
        return argsList;
    }

    public static List List(Collection... listArgs) {
        final List flattenList = new LinkedList();
        for (Collection collection : listArgs) {
            for (Object obj : collection) {
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

    public static Set Set(Collection... setArgs) {
        final Set flattenSet = new HashSet();
        for (Collection collection : setArgs) {
            for (Object obj : collection) {
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

    public static <K, V> Map<K, V> SafeMap(Entry<K, V>... entries) {
        final Map<K, V> map = new ConcurrentHashMap<K, V>();
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

    public static <F, S> Tuple2<F, S> tuple(F first, S second) {
        return new Tuple2<F, S>(first, second);
    }

    public static <F, S, T> Tuple3<F, S, T> tuple(F first, S second, T third) {
        return new Tuple3<F, S, T>(first, second, third);
    }

    public static <F, S, T, FO> Tuple4<F, S, T, FO> tuple(F first, S second, T third, FO fourth) {
        return new Tuple4<F, S, T, FO>(first, second, third, fourth);
    }

    public static <F, S, T, FO, FI> Tuple5<F, S, T, FO, FI> tuple(F first, S second, T third, FO fourth, FI fifth) {
        return new Tuple5<F, S, T, FO, FI>(first, second, third, fourth, fifth);
    }

}