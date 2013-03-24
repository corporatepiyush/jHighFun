package org.highfun.util;

import org.highfun.support.CacheObject;
import org.highfun.support.Pair;
import org.highfun.support.TaskInputOutput;
import org.highfun.support.ThreadPoolFactory;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class FunctionUtil {

    private static ExecutorService globalPool = ThreadPoolFactory.getThreadPool();

    static <I, O> List<O> map(List<I> inputList, Converter<I, O> converter) {
        List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.convert(i));
        }
        return outputList;
    }

    static <I, O> Collection<O> map(Collection<I> inputList, Converter<I, O> converter) {
        List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.convert(i));
        }
        return outputList;
    }

    static <I, O> List<O> map(List<I> inputList,
                              final Converter<I, O> converter, int noOfThread) {

        if (noOfThread < 2)
            return map(inputList, converter);

        return mapParallel(inputList, converter,
                noOfThread);
    }

    static <I, O> Collection<O> map(Collection<I> inputList,
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

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final List<TaskInputOutput<I, O>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    for (TaskInputOutput<I, O> taskInputOutput : list2) {
                        if (exception.size() == 0) {
                            try {
                                taskInputOutput.setOutput(converter.convert(taskInputOutput.getInput()));
                            } catch (Throwable e) {
                                exception.add(e);
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
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

        if (exception.size() > 0) {
            throw new RuntimeException(exception.get(0));
        }

        final List<O> outputList = new LinkedList<O>();

        for (TaskInputOutput<I, O> taskInputOutput : outList) {
            outputList.add(taskInputOutput.getOutput());
        }
        return outputList;
    }

    static <T> List<T> filter(List<T> inputList, Predicate<T> predicate) {

        List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (predicate.evaluate(i))
                outputList.add(i);
        }
        return outputList;
    }

    static <T> Set<T> filter(Set<T> inputSet, Predicate<T> predicate) {

        Set<T> outputSet = new HashSet<T>();

        for (T i : inputSet) {
            if (predicate.evaluate(i))
                outputSet.add(i);
        }
        return outputSet;
    }

    static <T> Collection<T> filter(Collection<T> inputList, Predicate<T> predicate) {

        List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (predicate.evaluate(i))
                outputList.add(i);
        }
        return outputList;
    }

    static <T> List<T> filter(List<T> inputList, Predicate<T> predicate,
                              int noOfThread) {

        if (noOfThread < 2)
            return filter(inputList, predicate);

        return (List<T>) filterParallel(inputList,
                predicate, noOfThread, List.class);

    }

    static <T> Set<T> filter(Set<T> inputSet, Predicate<T> predicate,
                             int noOfThread) {

        if (noOfThread < 2)
            return filter(inputSet, predicate);

        return (Set<T>) filterParallel(inputSet,
                predicate, noOfThread, Set.class);


    }

    static <T> Collection<T> filter(Collection<T> inputList, Predicate<T> predicate,
                                    int noOfThread) {

        if (noOfThread < 2)
            return filter(inputList, predicate);

        return filterParallel(inputList,
                predicate, noOfThread, List.class);

    }

    private static <T, DS> Collection<T> filterParallel(Collection<T> inputList,
                                                        final Predicate<T> predicate, int noOfThread, Class<DS> expectedCollection) {
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

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final List<TaskInputOutput<T, Boolean>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    for (TaskInputOutput<T, Boolean> taskInputOutput : list2) {
                        if (exception.size() == 0) {
                            try {
                                taskInputOutput.setOutput(predicate.evaluate(taskInputOutput.getInput()));
                            } catch (Throwable e) {
                                exception.add(e);
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
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


        if (exception.size() > 0) {
            throw new RuntimeException(exception.get(0));
        }

        Collection<T> outputList = null;

        if (expectedCollection.getName().equals("java.util.List")) {
            outputList = new LinkedList<T>();
        } else {
            outputList = new HashSet<T>();
        }

        for (TaskInputOutput<T, Boolean> taskInputOutput : outList) {
            if (taskInputOutput.getOutput() != null && taskInputOutput.getOutput())
                outputList.add(taskInputOutput.getInput());
        }

        return outputList;
    }

    static <ACCUM, EL> ACCUM foldLeft(Collection<EL> list, ACCUM accum,
                                      Accumulator<ACCUM, EL> accumulator) {

        for (EL element : list) {
            accum = accumulator.accumulate(accum, element);
        }

        return accum;
    }

    static <ACCUM, EL> ACCUM foldRight(Collection<EL> list, ACCUM accum,
                                       Accumulator<ACCUM, EL> accumulator) {

        LinkedList<EL> reverselist = new LinkedList<EL>();

        for (EL element : list) {
            reverselist.addFirst(element);
        }

        return foldLeft(reverselist, accum, accumulator);
    }

    static <T> T reduce(Collection<T> list,
                        Accumulator<T, T> accumulator) {
        T current, accum = null;

        Iterator<T> iterator = list.iterator();

        if (iterator.hasNext()) {
            accum = iterator.next();
        }

        while (iterator.hasNext()) {
            current = iterator.next();
            accum = accumulator.accumulate(accum, current);
        }

        return accum;
    }


    static <T> T reduce(Collection<T> inputList, final Accumulator<T, T> accumulator, int noOfThread) {

        final int size = inputList.size();

        if (size < 2)
            return reduce(inputList, accumulator);

        final List<List<T>> taskList = new ArrayList<List<T>>();
        final List<T> outList = new CopyOnWriteArrayList<T>();


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

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final List<T> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {

                    T current, accum = null;

                    Iterator<T> iterator = list2.iterator();

                    if (iterator.hasNext()) {
                        accum = iterator.next();
                    }

                    while (iterator.hasNext()) {
                        current = iterator.next();

                        if (exception.size() == 0) {
                            try {
                                accum = accumulator.accumulate(accum, current);
                            } catch (Throwable e) {
                                exception.add(e);
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
                    }

                    outList.add(accum);
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

        if (exception.size() > 0)
            throw new RuntimeException(exception.get(0));

        return reduce(outList, accumulator);
    }

    public static <T> List<T> sort(List<T> inputList, final Comparator<T> comparator) {

        List<T> outList = new ArrayList<T>(inputList.size());

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        return outList;
    }

    static <T> Set<T> sort(Set<T> inputList, final Comparator<T> comparator) {

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

    static <T> Collection<T> sort(Collection<T> inputList, final Comparator<T> comparator) {

        List<T> outList = new ArrayList<T>(inputList.size());

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        return outList;
    }

    static <T> boolean every(Collection<T> inputList, Predicate<T> predicate) {

        for (T t : inputList) {
            if (!predicate.evaluate(t))
                return false;
        }
        return true;
    }

    static <T> boolean any(Collection<T> inputList, Predicate<T> predicate) {

        for (T t : inputList) {
            if (predicate.evaluate(t))
                return true;
        }
        return false;
    }

    static <T> int count(Collection<T> input, Predicate<T> predicate) {
        int count = 0;
        for (T t : input) {
            if (predicate.evaluate(t))
                count++;
        }
        return count;
    }

    static <T> Collection<Collection<T>> split(Collection<T> input, Predicate<T> predicate) {

        Collection<T> list1 = new LinkedList<T>();
        Collection<T> list2 = new LinkedList<T>();

        Collection<Collection<T>> out = new LinkedList<Collection<T>>();

        for (T t : input) {
            if (predicate.evaluate(t))
                list1.add(t);
            else
                list2.add(t);
        }

        out.add(list1);
        out.add(list1);
        return out;
    }

    static <K, V> void each(Map<K, V> map, KeyValueRecordProcessor<K, V> keyValueRecordProcessor) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keyValueRecordProcessor.process(entry.getKey(), entry.getValue());
        }
    }

    static <T> void each(Collection<T> list, RecordProcessor<T> recordProcessor) {
        for (T item : list) {
            recordProcessor.process(item);
        }
    }

    static <T> void eachWithIndex(Collection<T> list, RecordWithIndexProcessor<T> recordProcessor) {
        int index = 0;
        for (T item : list) {
            recordProcessor.process(item, index++);
        }
    }

    static <T> void each(Collection<T> inputList, final RecordProcessor<T> recordProcessor, int noOfThread) {
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

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final List<T> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {

                    for (T task : list2) {
                        if (exception.size() == 0) {
                            try {
                                recordProcessor.process(task);
                            } catch (Throwable e) {
                                exception.add(e);
                                e.printStackTrace();
                            }
                        } else {
                            break;
                        }
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

        if (exception.size() > 0)
            throw new RuntimeException(exception.get(0));
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
                if (memoizedOutput != null && memoizedOutput.get() != null) {
                    return memoizedOutput.get();
                } else {
                    O output = converter.convert(input);
                    memo.put(iCacheObject, new CacheObject<O>(new SoftReference<O>(output)));
                    return output;
                }
            }
        };

    }

    public static <T> Predicate<T> memoize(final Predicate<T> predicate) {

        final Map<CacheObject<T>, Boolean> memo = new ConcurrentHashMap<CacheObject<T>, Boolean>();
        return new Predicate<T>() {
            public boolean evaluate(T input) {
                CacheObject<T> tCacheObject = new CacheObject<T>(input);
                Boolean memoizedOutput = memo.get(tCacheObject);
                if (memoizedOutput != null) {
                    return memoizedOutput;
                } else {
                    boolean output = predicate.evaluate(input);
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
                if (memoizedOutput != null && memoizedOutput.get() != null) {
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
                if (memoizedOutput != null && memoizedOutput.get() != null) {
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
