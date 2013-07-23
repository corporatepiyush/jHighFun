package org.jhighfun.util;

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

    public static <T> List<T> FlattenList(Iterable<T>... listArgs) {
        final List<T> flattenList = new LinkedList<T>();
        for (Iterable<T> collection : listArgs) {
            for (T t : collection) {
                flattenList.add(t);
            }
        }
        return flattenList;
    }


    public static <T> List<T> FlattenSafeList(Iterable<T>... listArgs) {
        final List<T> flattenList = new CopyOnWriteArrayList<T>();
        for (Iterable<T> collection : listArgs) {
            for (T obj : collection) {
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

    public static <T> Set<T> FlattenSet(Iterable<T>... setArgs) {
        final Set<T> flattenSet = new HashSet<T>();
        for (Iterable<T> collection : setArgs) {
            for (T obj : collection) {
                flattenSet.add(obj);
            }
        }
        return flattenSet;
    }

    public static <K, V> Map<K, V> Map(Pair<K, V>... entries) {
        final Map<K, V> map = new LinkedHashMap<K, V>();
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

    public static <K, V> Map<K, V> FlattenMap(Map<K, V> first, Map<K, V>... maps) {
        final Map<K, V> flattenMap = new HashMap<K, V>();
        flattenMap.putAll(first);
        for (Map<K, V> map : maps) {
            flattenMap.putAll(map);
        }
        return flattenMap;
    }


    public static <K, V> Map<K, V> FlattenSafeMap(Map<K, V> first, Map<K, V>... maps) {
        final Map<K, V> flattenMap = new ConcurrentHashMap<K, V>();
        flattenMap.putAll(first);
        for (Map<K, V> map : maps) {
            flattenMap.putAll(map);
        }
        return flattenMap;
    }

    public static <K, V> Pair<K, V> Entry(K key, V value) {
        return new Pair<K, V>(key, value);
    }

    public static <F> Tuple1<F> tuple(F first) {
        return new Tuple1<F>(first);
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

    public static <F, S, T, FO, FI, SI> Tuple6<F, S, T, FO, FI, SI> tuple(F first, S second, T third, FO fourth, FI fifth, SI six) {
        return new Tuple6<F, S, T, FO, FI, SI>(first, second, third, fourth, fifth, six);
    }

    public static List<Integer> IntRange(int from, int to, int step) {
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


    public static List<Integer> IntRange(int from, int to) {
        return IntRange(from, to, 1);
    }


    public static Iterable<Long> LazyLongRange(final long from, final long to, final long step) {
        if (step < 1)
            throw new IllegalArgumentException("'step' should be a number greater than ZERO.");
        Iterable<Long> range = null;
        if (from > to) {
            range = new DynamicIterable<Long>(new Iterator<Long>() {

                private long i = from;

                public boolean hasNext() {
                    return i >= to;
                }

                public Long next() {

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

                public void remove() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

            });
        } else {
            range = new DynamicIterable<Long>(new Iterator<Long>() {

                private long i = from;

                public boolean hasNext() {
                    return i <= to;
                }

                public Long next() {

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

                public void remove() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

            });
        }

        return range;
    }

    public static Iterable<Long> LazyLongRange(long from, long to) {
        return LazyLongRange(from, to, 1);
    }


    public static Iterable<Integer> LazyIntRange(final int from, final int to, final int step) {
        if (step < 1)
            throw new IllegalArgumentException("'step' should be a number greater than ZERO.");
        Iterable<Integer> range;
        if (from > to) {
            range = new DynamicIterable<Integer>(new Iterator<Integer>() {

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

                public void remove() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

            });
        } else {
            range = new DynamicIterable<Integer>(new Iterator<Integer>() {

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

                public void remove() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

            });
        }

        return range;
    }

    public static Iterable<Integer> LazyIntRange(int from, int to) {
        return LazyIntRange(from, to, 1);
    }
}