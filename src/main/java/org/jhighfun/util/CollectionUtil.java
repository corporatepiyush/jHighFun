package org.jhighfun.util;

import org.jhighfun.util.batch.AbstractIterator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Reusable factory methods to generate, merge, flatten various useful data structures
 * like List, Map, Tuples etc.
 *
 * @author Piyush Katariya
 */

public final class CollectionUtil {
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

    public static List List(Iterable... listArgs) {
        final List flattenList = new LinkedList();
        for (Iterable collection : listArgs) {
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

    public static Set Set(Iterable... setArgs) {
        final Set flattenSet = new HashSet();
        for (Iterable collection : setArgs) {
            for (Object obj : collection) {
                flattenSet.add(obj);
            }
        }
        return flattenSet;
    }

    public static <K, V> Map<K, V> Map(Pair<K, V>... entries) {
        final Map<K, V> map = new HashMap<K, V>();
        for (Pair<K, V> pair : entries) {
            map.put(pair.getKey(), pair.getValue());
        }
        return map;
    }

    public static <K, V> Map<K, V> SafeMap(Pair<K, V>... entries) {
        final Map<K, V> map = new ConcurrentHashMap<K, V>();
        for (Pair<K, V> pair : entries) {
            map.put(pair.getKey(), pair.getValue());
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

    public static <K, V> Pair<K, V> Entry(K key, V value) {
        return new Pair<K, V>(key, value);
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

    public static List<Integer> NumberRange(int from, int to, int step) {
        if (step < 1)
            throw new IllegalArgumentException("'step' should be a number greater than ZERO.");
        List<Integer> range = new LinkedList<Integer>();
        if (from > to) {
            for (int i = from; i >= to; i = i - step) {
                range.add(i);
            }
        } else {
            for (int i = from; i <= to; i = i + step) {
                range.add(i);
            }
        }

        return range;
    }


    public static List<Integer> NumberRange(int from, int to) {
        return NumberRange(from, to, 1);
    }


    public static Iterable<Integer> LazyRange(final int from, final int to, final int step) {
        if (step < 1)
            throw new IllegalArgumentException("'step' should be a number greater than ZERO.");
        Iterable<Integer> range = null;
        if (from > to) {
            range = new DynamicIterable<Integer>(new AbstractIterator<Integer>() {

                private int i = from;

                public boolean hasNext() {
                    return i >= to;
                }

                public Integer next() {

                    if (hasNext()) {
                        try {
                            return i;
                        } finally {
                            i = i - step;
                        }
                    } else {
                        throw new NoSuchElementException();
                    }
                }

            });
        } else {
            range = new DynamicIterable<Integer>(new AbstractIterator<Integer>() {

                private int i = from;

                public boolean hasNext() {
                    return i <= to;
                }

                public Integer next() {

                    if (hasNext()) {
                        try {
                            return i;
                        } finally {
                            i = i + step;
                        }
                    } else {
                        throw new NoSuchElementException();
                    }
                }

            });
        }

        return range;
    }

    public static Iterable<Integer> LazyRange(int from, int to) {
        return LazyRange(from, to, 1);
    }
}