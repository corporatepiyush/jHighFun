package org.highfun.util;

import org.highfun.support.CacheObject;
import org.highfun.support.Pair;
import org.highfun.support.TaskInputOutput;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.*;

public class FunctionUtil {

    private static final ExecutorService globalPool = new ThreadPoolExecutor(1, 100000, 5, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                globalPool.shutdownNow();
            }
        });
    }

    public static <I, O> List<O> map(List<I> inputList, Converter<I, O> converter) {
        List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.convert(i));
        }
        return outputList;
    }

    public static <I, O> Collection<O> map(Collection<I> inputList, Converter<I, O> converter) {
        List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.convert(i));
        }
        return outputList;
    }

    public static <I, O> List<O> map(List<I> inputList,
                                     final Converter<I, O> converter, int noOfThread) {

        if (noOfThread < 2)
            return map(inputList, converter);

        return mapParallel(inputList, converter,
                noOfThread);
    }

    public static <I, O> Collection<O> map(Collection<I> inputList,
                                           final Converter<I, O> converter, int noOfThread) {

        if (noOfThread < 2)
            return map(inputList, converter);

        return mapParallel(inputList, converter,
                noOfThread);
    }

    private static <I, O> List<O> mapParallel(Collection<I> inputList,
                                              final Converter<I, O> converter, int noOfThread) {
        final int size = inputList.size();
        final List<List<TaskInputOutput<I, O>>> taskList = new ArrayList<List<TaskInputOutput<I, O>>>();

        final List<TaskInputOutput<I, O>> outList = new ArrayList<TaskInputOutput<I, O>>();

        if (noOfThread > size)
            noOfThread = size;

        for (int i = 0; i < noOfThread; i++) {
            taskList.add(new LinkedList<TaskInputOutput<I, O>>());
        }

        int index = 0;
        TaskInputOutput<I, O> task = null;
        for (I i : inputList) {
            task = new TaskInputOutput<I, O>(i);
            outList.add(task);
            taskList.get(index % noOfThread).add(task);
            index++;
        }

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        int i = 0;
        for (final List<TaskInputOutput<I, O>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    try {
                        for (TaskInputOutput<I, O> taskInputOutput : list2) {
                            taskInputOutput.setOutput(converter.convert(taskInputOutput.getInput()));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        for (i = 1; i < noOfThread; i++) {
            futures[i] = globalPool.submit(threads[i]);
        }

        threads[0].run();

        for (i = 1; i < noOfThread; i++) {
            try {
                futures[i].get();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        final List<O> outputList = new LinkedList<O>();

        for (TaskInputOutput<I, O> taskInputOutput : outList) {
            outputList.add(taskInputOutput.getOutput());
        }
        return outputList;
    }

    public static <T> List<T> filter(List<T> inputList, Condition<T> condition) {

        List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (condition.evaluate(i))
                outputList.add(i);
        }
        return outputList;
    }

    public static <T> Set<T> filter(Set<T> inputSet, Condition<T> condition) {

        Set<T> outputSet = new HashSet<T>();

        for (T i : inputSet) {
            if (condition.evaluate(i))
                outputSet.add(i);
        }
        return outputSet;
    }

    public static <T> Collection<T> filter(Collection<T> inputList, Condition<T> condition) {

        List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (condition.evaluate(i))
                outputList.add(i);
        }
        return outputList;
    }

    public static <T> List<T> filter(List<T> inputList, Condition<T> condition,
                                     int noOfThread) {

        if (noOfThread < 2)
            return filter(inputList, condition);

        return (List<T>) filterParallel(inputList,
                condition, noOfThread, List.class);

    }

    public static <T> Set<T> filter(Set<T> inputSet, Condition<T> condition,
                                    int noOfThread) {

        if (noOfThread < 2)
            return filter(inputSet, condition);

        return (Set<T>) filterParallel(inputSet,
                condition, noOfThread, Set.class);


    }

    public static <T> Collection<T> filter(Collection<T> inputList, Condition<T> condition,
                                           int noOfThread) {

        if (noOfThread < 2)
            return filter(inputList, condition);

        return filterParallel(inputList,
                condition, noOfThread, List.class);

    }

    private static <T, DS> Collection<T> filterParallel(Collection<T> inputList,
                                                        final Condition<T> condition, int noOfThread, Class<DS> expectedCollection) {
        final int size = inputList.size();
        final List<List<TaskInputOutput<T, Boolean>>> taskList = new ArrayList<List<TaskInputOutput<T, Boolean>>>();
        final List<TaskInputOutput<T, Boolean>> outList = new LinkedList<TaskInputOutput<T, Boolean>>();

        if (noOfThread > size)
            noOfThread = size;

        for (int i = 0; i < noOfThread; i++) {
            taskList.add(new LinkedList<TaskInputOutput<T, Boolean>>());
        }

        int index = 0;
        TaskInputOutput<T, Boolean> task = null;
        for (T input : inputList) {
            task = new TaskInputOutput<T, Boolean>(input);
            outList.add(task);
            taskList.get(index % noOfThread).add(task);
            index++;
        }

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        int i = 0;
        for (final List<TaskInputOutput<T, Boolean>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    try {
                        for (TaskInputOutput<T, Boolean> taskInputOutput : list2) {
                            taskInputOutput.setOutput(condition.evaluate(taskInputOutput.getInput()));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        for (i = 1; i < noOfThread; i++) {
            futures[i] = globalPool.submit(threads[i]);
        }

        threads[0].run();

        for (i = 1; i < noOfThread; i++) {
            try {
                futures[i].get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        Collection<T> outputList = null;

        if (expectedCollection.getName().equals("java.util.List")) {
            outputList = new LinkedList<T>();
        } else {
            outputList = new HashSet<T>();
        }

        for (TaskInputOutput<T, Boolean> taskInputOutput : outList) {
            if (taskInputOutput.getOutput())
                outputList.add(taskInputOutput.getInput());
        }

        return outputList;
    }

    public static <ACCUM, EL> ACCUM foldLeft(Collection<EL> list, ACCUM accum,
                                             Accumulator<ACCUM, EL> accumulator) {

        for (EL element : list) {
            accum = accumulator.accumulate(accum, element);
        }

        return accum;
    }

    public static <ACCUM, EL> ACCUM foldRight(Collection<EL> list, ACCUM accum,
                                              Accumulator<ACCUM, EL> accumulator) {

        LinkedList<EL> reverselist = new LinkedList<EL>();

        for (EL element : list) {
            reverselist.addFirst(element);
        }

        return foldLeft(reverselist, accum, accumulator);
    }

    public static <ACCUM, EL> ACCUM reduce(Collection<EL> list, ACCUM accum,
                                           Accumulator<ACCUM, EL> accumulator) {
        return foldLeft(list, accum, accumulator);
    }

    public static <T> List<T> sort(List<T> inputList, final Comparator<T> comparator) {

        List<T> outList = new ArrayList<T>(inputList.size());

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        return outList;
    }

    public static <T> Set<T> sort(Set<T> inputList, final Comparator<T> comparator) {

        List<T> outList = new ArrayList<T>(inputList.size());

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        Set<T> outSet = new HashSet<T>(inputList.size());

        for (T element : outList) {
            outSet.add(element);
        }

        return outSet;
    }

    public static <T> Collection<T> sort(Collection<T> inputList, final Comparator<T> comparator) {

        List<T> outList = new ArrayList<T>(inputList.size());

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        return outList;
    }

    public static <T> boolean every(Collection<T> inputList, Condition<T> condition) {

        for (T t : inputList) {
            if (!condition.evaluate(t))
                return false;
        }
        return true;
    }

    public static <T> boolean any(Collection<T> inputList, Condition<T> condition) {

        for (T t : inputList) {
            if (condition.evaluate(t))
                return true;
        }
        return false;
    }

    public static <T> int count(Collection<T> input, Condition<T> condition) {
        int count = 0;
        for (T t : input) {
            if (condition.evaluate(t))
                count++;
        }
        return count;
    }

    public static <T> Collection<Collection<T>> split(Collection<T> input, Condition<T> condition) {

        Collection<T> list1 = new LinkedList<T>();
        Collection<T> list2 = new LinkedList<T>();

        Collection<Collection<T>> out = new LinkedList<Collection<T>>();

        for (T t : input) {
            if (condition.evaluate(t))
                list1.add(t);
            else
                list2.add(t);
        }

        out.add(list1);
        out.add(list1);
        return out;
    }

    public static <K, V> void each(Map<K, V> map, KeyValueRecord<K, V> keyValueRecord) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keyValueRecord.process(entry.getKey(), entry.getValue());
        }
    }

    public static <T> void each(Collection<T> list, ItemRecord<T> itemRecord) {
        for (T item : list) {
            itemRecord.process(item);
        }
    }

    public static <T> void each(Collection<T> inputList, final ItemRecord<T> itemRecord, int noOfThread) {
        final int size = inputList.size();
        final List<List<T>> taskList = new ArrayList<List<T>>();

        if (noOfThread > size)
            noOfThread = size;

        for (int i = 0; i < noOfThread; i++) {
            taskList.add(new LinkedList<T>());
        }

        int index = 0;
        for (T input : inputList) {
            taskList.get(index % noOfThread).add(input);
            index++;
        }

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        int i = 0;
        for (final List<T> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    try {
                        for (T task : list2) {
                            itemRecord.process(task);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }

        for (i = 1; i < noOfThread; i++) {
            futures[i] = globalPool.submit(threads[i]);
        }

        threads[0].run();

        for (i = 1; i < noOfThread; i++) {
            try {
                futures[i].get();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public static <I> FunctionChain<I> chain(Collection<I> collection) {
        return new FunctionChain<I>(collection);
    }

    public static <I, O> CurriedFunction<I, O> curry(Function<I, O> function, List<I> fixedInputs) {
        return new CurriedFunction<I, O>(function, fixedInputs);
    }

    public static <I, O> CurriedFunction<I, O> curry(Function<I, O> function, I... fixedInputs) {
        return new CurriedFunction<I, O>(function, Arrays.asList(fixedInputs));
    }


    public static <I, O> Converter<I, O> memoize(final Converter<I, O> converter) {

        final Map<CacheObject<I>, CacheObject<O>> memo = new ConcurrentHashMap<CacheObject<I>, CacheObject<O>>();
        return new Converter<I, O>() {
            public O convert(I input) {
                CacheObject<I> iCacheObject = new CacheObject<I>(input);
                CacheObject<O> memoizedOutput = memo.get(iCacheObject);
                if (memoizedOutput != null) {
                    return memoizedOutput.get();
                } else {
                    O output = converter.convert(input);
                    memo.put(iCacheObject, new CacheObject<O>(new SoftReference<O>(output)));
                    return output;
                }
            }
        };

    }

    public static <T> Condition<T> memoize(final Condition<T> condition) {

        final Map<CacheObject<T>, Boolean> memo = new ConcurrentHashMap<CacheObject<T>, Boolean>();
        return new Condition<T>() {
            public boolean evaluate(T input) {
                CacheObject<T> tCacheObject = new CacheObject<T>(input);
                Boolean memoizedOutput = memo.get(tCacheObject);
                if (memoizedOutput != null) {
                    return memoizedOutput;
                } else {
                    boolean output = condition.evaluate(input);
                    memo.put(tCacheObject, output);
                    return output;
                }
            }
        };
    }

    public static <I, O> Function<I, O> memoize(final Function<I, O> function) {
        final Map<CacheObject<List<I>>, CacheObject<O>> memo = new ConcurrentHashMap<CacheObject<List<I>>, CacheObject<O>>();
        return new Function<I, O>() {
            public O apply(List<I> input) {
                CacheObject<List<I>> listCacheObject = new CacheObject<List<I>>(input);
                CacheObject<O> memoizedOutput = memo.get(listCacheObject);
                if (memoizedOutput != null) {
                    return memoizedOutput.get();
                } else {
                    O output = function.apply(input);
                    memo.put(listCacheObject, new CacheObject<O>(output));
                    return output;
                }
            }
        };
    }

    public static <ACCUM, EL> Accumulator<ACCUM, EL> memoize(final Accumulator<ACCUM, EL> accumulator) {

        final Map<CacheObject<Pair<ACCUM, EL>>, CacheObject<ACCUM>> memo = new ConcurrentHashMap<CacheObject<Pair<ACCUM, EL>>, CacheObject<ACCUM>>();
        return new Accumulator<ACCUM, EL>() {
            public ACCUM accumulate(ACCUM accum, EL el) {
                CacheObject<Pair<ACCUM, EL>> pairCacheObject = new CacheObject<Pair<ACCUM, EL>>(new Pair<ACCUM, EL>(accum, el));
                CacheObject<ACCUM> memoizedOutput = memo.get(pairCacheObject);
                if (memoizedOutput != null) {
                    return memoizedOutput.get();
                } else {
                    ACCUM output = accumulator.accumulate(accum, el);
                    memo.put(pairCacheObject, new CacheObject<ACCUM>(output));
                    return output;
                }
            }
        };

    }

}
