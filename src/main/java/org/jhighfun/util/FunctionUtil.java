package org.jhighfun.util;

import org.jhighfun.internal.*;
import org.jhighfun.util.matcher.WhenChecker;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.jhighfun.util.CollectionUtil.List;
import static org.jhighfun.util.CollectionUtil.Set;

/**
 * Set of reusable utility methods to present finer interfaces to do a given job in
 * concurrent and lean fashion.
 * <p/>
 * 1. Higher Order Function's with concurrency support to mimic the best practices from functional programming
 * 2. Memoization(caching/optimizing the method calls) to implement smart executable function units.
 *
 * @author Piyush Katariya
 */

public final class FunctionUtil {

    private static ExecutorService highPriorityTaskThreadPool = ThreadPoolFactory.getHighPriorityTaskThreadPool();
    private static ExecutorService mediumPriorityAsyncTaskThreadPool = ThreadPoolFactory.getMediumPriorityAsyncTaskThreadPool();
    private static ExecutorService lowPriorityAsyncTaskThreadPool = ThreadPoolFactory.getLowPriorityAsyncTaskThreadPool();

    private static final Lock globalLock = new ReentrantLock(true);
    private static final Lock registerOperation = new ReentrantLock(true);
    private static final ConcurrentHashMap<Operation, Lock> operationLockMap = new ConcurrentHashMap<Operation, Lock>(15, 0.9f, 32);

    private static Map<ExecutionThrottler, ExecutorService> throttlerPoolMap = new ConcurrentHashMap<ExecutionThrottler, ExecutorService>(15, 0.9f, 32);

    public static <I, O> List<O> map(List<I> inputList, Function<I, O> converter) {
        final List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.apply(i));
        }
        return outputList;
    }

    public static <I, O> Collection<O> map(Collection<I> inputList, Function<I, O> converter) {
        final List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.apply(i));
        }
        return outputList;
    }

    public static <I, O> Iterable<O> map(Iterable<I> inputList, Function<I, O> converter) {
        final List<O> outputList = new LinkedList<O>();

        for (I i : inputList) {
            outputList.add(converter.apply(i));
        }
        return outputList;
    }

    public static <I, O> List<O> map(List<I> inputList,
                                     final Function<I, O> converter, WorkDivisionStrategy workDivisionStrategy) {

        List<TaskInputOutput<I, O>> inputOutputs = map(inputList, new Function<I, TaskInputOutput<I, O>>() {
            public TaskInputOutput<I, O> apply(I arg) {
                return new TaskInputOutput<I, O>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<I, O>>> dividedList = workDivisionStrategy.divide(inputOutputs);


        if (dividedList.size() < 2)
            return map(inputList, converter);

        mapParallel(dividedList, converter);
        return map(inputOutputs, new Function<TaskInputOutput<I, O>, O>() {
            public O apply(TaskInputOutput<I, O> task) {
                return task.getOutput();
            }
        });
    }

    public static <I, O> Collection<O> map(Collection<I> inputList,
                                           final Function<I, O> converter, WorkDivisionStrategy workDivisionStrategy) {

        Collection<TaskInputOutput<I, O>> inputOutputs = map(inputList, new Function<I, TaskInputOutput<I, O>>() {
            public TaskInputOutput<I, O> apply(I arg) {
                return new TaskInputOutput<I, O>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<I, O>>> dividedList = workDivisionStrategy.divide(inputOutputs);


        if (dividedList.size() < 2)
            return map(inputList, converter);

        mapParallel(dividedList, converter);
        return map(inputOutputs, new Function<TaskInputOutput<I, O>, O>() {
            public O apply(TaskInputOutput<I, O> task) {
                return task.getOutput();
            }
        });
    }

    public static <I, O> Iterable<O> map(Iterable<I> inputList, Function<I, O> converter, WorkDivisionStrategy workDivisionStrategy) {
        Iterable<TaskInputOutput<I, O>> inputOutputs = map(inputList, new Function<I, TaskInputOutput<I, O>>() {
            public TaskInputOutput<I, O> apply(I arg) {
                return new TaskInputOutput<I, O>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<I, O>>> dividedList = workDivisionStrategy.divide(inputOutputs);


        if (dividedList.size() < 2)
            return map(inputList, converter);

        mapParallel(dividedList, converter);
        return map(inputOutputs, new Function<TaskInputOutput<I, O>, O>() {
            public O apply(TaskInputOutput<I, O> task) {
                return task.getOutput();
            }
        });
    }

    private static <I, O> void mapParallel(Collection<Collection<TaskInputOutput<I, O>>> taskList,
                                           final Function<I, O> converter) {
        final int noOfThread = taskList.size();

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final Collection<TaskInputOutput<I, O>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    int loop = 1;
                    for (TaskInputOutput<I, O> taskInputOutput : list2) {
                        if (exception.size() == 0) {
                            try {
                                taskInputOutput.setOutput(converter.apply(taskInputOutput.getInput()));
                                if (loop++ == Constants.MAX_LOOP_BEFORE_YIELD) {
                                    Thread.yield();
                                    loop = 1;
                                }
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
            futures[i] = highPriorityTaskThreadPool.submit(threads[i]);
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

    }

    public static <T> List<T> filter(List<T> inputList, Function<T, Boolean> predicate) {

        final List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (predicate.apply(i))
                outputList.add(i);
        }
        return outputList;
    }

    public static <T> Set<T> filter(Set<T> inputSet, Function<T, Boolean> predicate) {

        final Set<T> outputSet = new HashSet<T>();

        for (T i : inputSet) {
            if (predicate.apply(i))
                outputSet.add(i);
        }
        return outputSet;
    }

    public static <T> Collection<T> filter(Collection<T> inputList, Function<T, Boolean> predicate) {

        final List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (predicate.apply(i))
                outputList.add(i);
        }
        return outputList;
    }

    public static <T> Iterable<T> filter(Iterable<T> inputList, Function<T, Boolean> predicate) {

        final List<T> outputList = new LinkedList<T>();

        for (T i : inputList) {
            if (predicate.apply(i))
                outputList.add(i);
        }
        return outputList;
    }

    public static <T> List<T> filter(List<T> inputList, Function<T, Boolean> predicate,
                                     WorkDivisionStrategy workDivisionStrategy) {

        List<TaskInputOutput<T, Boolean>> inputOutputs = map(inputList, new Function<T, TaskInputOutput<T, Boolean>>() {
            public TaskInputOutput<T, Boolean> apply(T arg) {
                return new TaskInputOutput<T, Boolean>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<T, Boolean>>> collectionList = workDivisionStrategy.divide(inputOutputs);

        if (collectionList.size() < 2)
            return filter(inputList, predicate);

        filterParallel(collectionList, predicate, List.class);
        return chain(inputOutputs)
                .filter(new Function<TaskInputOutput<T, Boolean>, Boolean>() {
                    public Boolean apply(TaskInputOutput<T, Boolean> task) {
                        return task.getOutput();
                    }
                }).map(new Function<TaskInputOutput<T, Boolean>, T>() {
                    public T apply(TaskInputOutput<T, Boolean> arg) {
                        return arg.getInput();
                    }
                }).extract();

    }

    public static <T> Set<T> filter(Set<T> inputSet, Function<T, Boolean> predicate,
                                    WorkDivisionStrategy workDivisionStrategy) {

        List<TaskInputOutput<T, Boolean>> inputOutputs = (List<TaskInputOutput<T, Boolean>>) map(inputSet, new Function<T, TaskInputOutput<T, Boolean>>() {
            public TaskInputOutput<T, Boolean> apply(T arg) {
                return new TaskInputOutput<T, Boolean>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<T, Boolean>>> collectionList = workDivisionStrategy.divide(inputOutputs);

        if (collectionList.size() < 2)
            return filter(inputSet, predicate);

        filterParallel(collectionList, predicate, List.class);
        return chain(inputOutputs)
                .filter(new Function<TaskInputOutput<T, Boolean>, Boolean>() {
                    public Boolean apply(TaskInputOutput<T, Boolean> task) {
                        return task.getOutput();
                    }
                }).foldLeft((Set<T>) Set(), new Accumulator<Set<T>, TaskInputOutput<T, Boolean>>() {
                    public Set<T> accumulate(Set<T> accumulator, TaskInputOutput<T, Boolean> task) {
                        accumulator.add(task.getInput());
                        return accumulator;
                    }
                }).extract();

    }

    public static <T> Collection<T> filter(Collection<T> inputList, Function<T, Boolean> predicate,
                                           WorkDivisionStrategy workDivisionStrategy) {

        List<TaskInputOutput<T, Boolean>> inputOutputs = (List<TaskInputOutput<T, Boolean>>) map(inputList, new Function<T, TaskInputOutput<T, Boolean>>() {
            public TaskInputOutput<T, Boolean> apply(T arg) {
                return new TaskInputOutput<T, Boolean>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<T, Boolean>>> collectionList = workDivisionStrategy.divide(inputOutputs);

        if (collectionList.size() < 2)
            return filter(inputList, predicate);

        filterParallel(collectionList, predicate, List.class);
        return chain(inputOutputs)
                .filter(new Function<TaskInputOutput<T, Boolean>, Boolean>() {
                    public Boolean apply(TaskInputOutput<T, Boolean> task) {
                        return task.getOutput();
                    }
                }).map(new Function<TaskInputOutput<T, Boolean>, T>() {
                    public T apply(TaskInputOutput<T, Boolean> arg) {
                        return arg.getInput();
                    }
                }).extract();

    }

    public static <T> Iterable<T> filter(Iterable<T> inputList, Function<T, Boolean> predicate,
                                         WorkDivisionStrategy workDivisionStrategy) {

        Iterable<TaskInputOutput<T, Boolean>> inputOutputs = map(inputList, new Function<T, TaskInputOutput<T, Boolean>>() {
            public TaskInputOutput<T, Boolean> apply(T arg) {
                return new TaskInputOutput<T, Boolean>(arg);
            }
        });
        Collection<Collection<TaskInputOutput<T, Boolean>>> collectionList = workDivisionStrategy.divide(inputOutputs);

        if (collectionList.size() < 2)
            return filter(inputList, predicate);

        filterParallel(collectionList, predicate, List.class);
        Iterable<TaskInputOutput<T, Boolean>> filter = filter(inputOutputs, new Function<TaskInputOutput<T, Boolean>, Boolean>() {
            public Boolean apply(TaskInputOutput<T, Boolean> task) {
                return task.getOutput();
            }
        });

        return map(filter, new Function<TaskInputOutput<T, Boolean>, T>() {
            public T apply(TaskInputOutput<T, Boolean> arg) {
                return arg.getInput();
            }
        });

    }

    private static <T, DS> void filterParallel(Collection<Collection<TaskInputOutput<T, Boolean>>> taskList,
                                               final Function<T, Boolean> predicate, Class<DS> expectedCollection) {
        final int noOfThread = taskList.size();
        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];
        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final Collection<TaskInputOutput<T, Boolean>> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    int loop = 1;
                    for (TaskInputOutput<T, Boolean> taskInputOutput : list2) {
                        if (exception.size() == 0) {
                            try {
                                taskInputOutput.setOutput(predicate.apply(taskInputOutput.getInput()));
                                if (loop++ == Constants.MAX_LOOP_BEFORE_YIELD) {
                                    Thread.yield();
                                    loop = 1;
                                }
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
            futures[i] = highPriorityTaskThreadPool.submit(threads[i]);
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
    }

    public static <ACCUM, EL> ACCUM foldLeft(Iterable<EL> list, ACCUM accum,
                                             Accumulator<ACCUM, EL> accumulator) {

        for (EL element : list) {
            accum = accumulator.accumulate(accum, element);
        }

        return accum;
    }

    public static <ACCUM, EL> ACCUM foldRight(Iterable<EL> list, ACCUM accum,
                                              Accumulator<ACCUM, EL> accumulator) {

        final LinkedList<EL> reverseList = new LinkedList<EL>();

        for (EL element : list) {
            reverseList.addFirst(element);
        }

        return foldLeft(reverseList, accum, accumulator);
    }

    public static <T> T reduce(Iterable<T> list,
                               Accumulator<T, T> accumulator) {
        T current, accum = null;

        final Iterator<T> iterator = list.iterator();

        if (iterator.hasNext()) {
            accum = iterator.next();
        }

        while (iterator.hasNext()) {
            current = iterator.next();
            accum = accumulator.accumulate(accum, current);
        }

        return accum;
    }


    public static <T> T reduce(Iterable<T> inputList, final Accumulator<T, T> accumulator, WorkDivisionStrategy workDivisionStrategy) {

        final Collection<Collection<T>> taskList = workDivisionStrategy.divide(inputList);
        final List<T> outList = new CopyOnWriteArrayList<T>();

        int noOfThread = taskList.size();

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final Collection<T> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {

                    T current, accum = null;

                    Iterator<T> iterator = list2.iterator();

                    if (iterator.hasNext()) {
                        accum = iterator.next();
                    }

                    int loop = 1;
                    while (iterator.hasNext()) {
                        current = iterator.next();

                        if (exception.size() == 0) {
                            try {
                                accum = accumulator.accumulate(accum, current);
                                if (loop++ == Constants.MAX_LOOP_BEFORE_YIELD) {
                                    Thread.yield();
                                    loop = 1;
                                }
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
            futures[i] = highPriorityTaskThreadPool.submit(threads[i]);
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

    public static <T> List<T> sortWith(Iterable<T> inputList, final Comparator<T> comparator) {

        final List<T> outList = new ArrayList<T>();

        for (T element : inputList) {
            outList.add(element);
        }

        Collections.sort(outList, comparator);

        return outList;
    }

    public static <T> List<T> sort(Iterable<T> inputList) {
        return sortWith(inputList, new Comparator<T>() {
            public int compare(T o1, T o2) {
                return ((Comparable) o1).compareTo(o2);
            }
        });
    }

    public static <T> List<T> sortBy(Iterable<T> inputList, String member, String... members) {

        final List<String> memberVars = new LinkedList<String>();
        memberVars.add(member);
        for (String memberVar : members) {
            memberVars.add(memberVar);
        }

        final Iterator<T> iterator = inputList.iterator();
        final List<Field> fieldList = new ArrayList<Field>();
        final List<T> list = new LinkedList<T>();

        if (iterator.hasNext()) {
            final T t = iterator.next();
            list.add(t);
            Class<?> tClass = t.getClass();

            for (String memberVar : memberVars) {
                try {
                    final Field field = tClass.getDeclaredField(memberVar);

                    if (field != null) {
                        field.setAccessible(true);
                        fieldList.add(field);
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

        }

        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        final int fieldLength = fieldList.size();

        Collections.sort(list, new Comparator<T>() {
            public int compare(T o1, T o2) {

                int compareResult = 0;

                for (int i = 0; i < fieldLength; i++) {

                    try {
                        compareResult = ((Comparable) fieldList.get(i).get(o1)).compareTo(fieldList.get(i).get(o2));
                        if (compareResult != 0) {
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                }

                return compareResult;
            }
        });

        return list;
    }

    public static <T> boolean every(Iterable<T> inputList, Function<T, Boolean> predicate) {

        for (T t : inputList) {
            if (!predicate.apply(t))
                return false;
        }
        return true;
    }

    public static <T> boolean any(Iterable<T> inputList, Function<T, Boolean> predicate) {

        for (T t : inputList) {
            if (predicate.apply(t))
                return true;
        }
        return false;
    }

    public static <T> int count(Iterable<T> input, Function<T, Boolean> predicate) {
        int count = 0;
        for (T t : input) {
            if (predicate.apply(t))
                count++;
        }
        return count;
    }


    public static <T> Tuple2<Collection<T>, Collection<T>> partition(Iterable<T> input, Function<T, Boolean> predicate) {

        final Collection<T> list1 = new LinkedList<T>();
        final Collection<T> list2 = new LinkedList<T>();

        final Collection<Collection<T>> out = new LinkedList<Collection<T>>();

        for (T t : input) {
            if (predicate.apply(t))
                list1.add(t);
            else
                list2.add(t);
        }

        out.add(list1);
        out.add(list1);
        return new Tuple2<Collection<T>, Collection<T>>(list1, list2);
    }

    public static <K, V> void each(Map<K, V> map, KeyValueRecordProcessor<K, V> keyValueRecordProcessor) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keyValueRecordProcessor.process(entry.getKey(), entry.getValue());
        }
    }

    public static <T> void each(Iterable<T> list, RecordProcessor<T> recordProcessor) {
        for (T item : list) {
            recordProcessor.process(item);
        }
    }

    public static <T> void eachWithIndex(Iterable<T> list, RecordWithIndexProcessor<T> recordProcessor) {
        int index = 0;
        for (T item : list) {
            recordProcessor.process(item, index++);
        }
    }

    public static <T> void each(Iterable<T> inputList, final RecordProcessor<T> recordProcessor, WorkDivisionStrategy workDivisionStrategy) {
        final Collection<Collection<T>> taskList = workDivisionStrategy.divide(inputList);

        final int noOfThread = taskList.size();

        final Runnable[] threads = new Runnable[noOfThread];
        final Future[] futures = new Future[noOfThread];

        final List<Throwable> exception = new CopyOnWriteArrayList<Throwable>();

        int i = 0;
        for (final Collection<T> list2 : taskList) {
            threads[i++] = new Runnable() {
                public void run() {
                    int loop = 1;
                    for (T task : list2) {
                        if (exception.size() == 0) {
                            try {
                                recordProcessor.process(task);
                                if (loop++ == Constants.MAX_LOOP_BEFORE_YIELD) {
                                    Thread.yield();
                                    loop = 1;
                                }
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
            futures[i] = highPriorityTaskThreadPool.submit(threads[i]);
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

    public static <I> CollectionFunctionChain<I> chain(Iterable<I> iterable) {
        if (iterable instanceof List)
            return new CollectionFunctionChain<I>((List<I>) iterable);
        else
            return new CollectionFunctionChain<I>(CollectionUtil.List(iterable));
    }

    public static <I> ObjectFunctionChain<I> chain(I object) {
        return new ObjectFunctionChain<I>(object);
    }

    public static <I> DynamicIterable<I> stream(Iterable<I> iterable) {
        return new DynamicIterable<I>(iterable);
    }

    public static <I> DynamicIterable<I> stream(Iterator<I> iterator) {
        return new DynamicIterable<I>(iterator);
    }

    public static <INIT, IN> DynamicIterable<IN> lazyStream(INIT initialInput, Function<INIT, Tuple2<INIT, IN>> function, Function<INIT, Boolean> predicate) {
        return new DynamicIterable<IN>(initialInput, function, predicate);
    }

    public static <I, O> CurriedFunction<I, O> curry(Function<List<I>, O> function, List<I> fixedInputs) {
        return new CurriedFunction<I, O>(function, fixedInputs);
    }

    public static <I, O> CurriedFunction<I, O> curry(Function<List<I>, O> function, I... fixedInputs) {
        return new CurriedFunction<I, O>(function, Arrays.asList(fixedInputs));
    }

    public static void executeAsync(final Block codeBlock) {
        mediumPriorityAsyncTaskThreadPool.submit(new Runnable() {
            public void run() {
                try {
                    codeBlock.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T> void executeAsync(final AsyncTask<T> asyncTask, final CallbackTask callbackTask) {
        mediumPriorityAsyncTaskThreadPool.submit(new Runnable() {
            public void run() {
                AsyncTaskHandle<T> asyncTaskHandle = null;
                try {
                    T output = asyncTask.execute();
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, output, null);
                } catch (Throwable e) {
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, null, e);
                } finally {
                    try {
                        callbackTask.execute(asyncTaskHandle);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void executeLater(final Block codeBlock) {
        lowPriorityAsyncTaskThreadPool.submit(new Runnable() {
            public void run() {
                try {
                    codeBlock.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static <T> void executeLater(final AsyncTask<T> asyncTask, final CallbackTask callbackTask) {
        lowPriorityAsyncTaskThreadPool.submit(new Runnable() {
            public void run() {
                AsyncTaskHandle<T> asyncTaskHandle = null;
                try {
                    T output = asyncTask.execute();
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, output, null);
                } catch (Throwable e) {
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, null, e);
                } finally {
                    try {
                        callbackTask.execute(asyncTaskHandle);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void executeWithLock(Operation operation, final Block codeBlock) {

        Lock lock = operationLockMap.get(operation);

        if (lock == null) {

            registerOperation.lock();
            try {
                lock = operationLockMap.get(operation);
                if (lock == null)
                    operationLockMap.put(operation, new ReentrantLock(true));
            } finally {
                registerOperation.unlock();
            }

            executeWithLock(operation, codeBlock);
        } else {
            lock.lock();
            try {
                codeBlock.execute();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void executeWithThrottle(ExecutionThrottler executionThrottler, final Block codeBlock) {
        ExecutorService executorService = throttlerPoolMap.get(executionThrottler);

        if (executorService == null)
            throw new RuntimeException("Please register the Thread Pool for executionThrottler[" + executionThrottler.toString() + "]");

        final LinkedList<Throwable> exception = new LinkedList<Throwable>();
        try {
            executorService.submit(new Runnable() {
                public void run() {
                    try {
                        codeBlock.execute();
                    } catch (Throwable e) {
                        exception.add(e);
                        e.printStackTrace();
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (exception.size() > 0) {
            throw new RuntimeException(exception.get(0));
        }
    }

    public static <T> void executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final AsyncTask<T> asyncTask, final CallbackTask<T> callbackTask) {

        ExecutorService executorService = throttlerPoolMap.get(executionThrottler);

        if (executorService == null)
            throw new RuntimeException("Please register the Thread Pool for executionThrottler[" + executionThrottler.toString() + "]");

        executorService.submit(new Runnable() {
            public void run() {
                AsyncTaskHandle<T> asyncTaskHandle = null;
                try {
                    T output = asyncTask.execute();
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, output, null);
                } catch (Throwable e) {
                    asyncTaskHandle = new AsyncTaskHandle<T>(asyncTask, null, e);
                } finally {
                    try {
                        callbackTask.execute(asyncTaskHandle);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public static void executeAsyncWithThrottle(ExecutionThrottler executionThrottler, final Block codeBlock) {
        ExecutorService executorService = throttlerPoolMap.get(executionThrottler);

        if (executorService == null)
            throw new RuntimeException("Please register the Thread Pool for executionThrottler[" + executionThrottler.toString() + "]");

        executorService.submit(new Runnable() {
            public void run() {
                try {
                    codeBlock.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void registerPool(ExecutionThrottler executionThrottler, int maxPoolSize) {
        if (executionThrottler == null)
            throw new RuntimeException("Please provide ExecutionThrottler for which you wish to create Thread pool.");
        throttlerPoolMap.put(executionThrottler, Executors.newFixedThreadPool(maxPoolSize));
    }

    public static void executeWithGlobalLock(Block codeBlock) {
        globalLock.lock();
        try {
            codeBlock.execute();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            globalLock.unlock();
        }
    }

    public static void executeWithTimeout(final Block codeBlock, Integer time, TimeUnit timeUnit) throws TimeoutException {

        final LinkedList<Throwable> exceptions = new LinkedList<Throwable>();

        try {
            highPriorityTaskThreadPool.submit(new Runnable() {
                public void run() {
                    try {
                        codeBlock.execute();
                    } catch (Throwable e) {
                        exceptions.add(e);
                        e.printStackTrace();
                    }
                }
            }).get(time, timeUnit);
        } catch (TimeoutException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (exceptions.size() > 0) {
                throw new RuntimeException(exceptions.get(0));
            }
        }

    }

    public static void executeAwait(final Block codeBlock, Integer time, TimeUnit timeUnit) {

        final List<Throwable> exception = new LinkedList<Throwable>();
        try {
            highPriorityTaskThreadPool.submit(new Runnable() {
                public void run() {
                    try {
                        codeBlock.execute();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        exception.add(e);
                    }
                }
            }).get(time, timeUnit);
        } catch (TimeoutException e) {
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (exception.size() > 0)
                throw new RuntimeException(exception.get(0));
        }
    }

    public static <I, O> Function<I, O> memoize(final Function<I, O> function) {
        final Map<CacheObject<I>, Future<CacheObject<O>>> memo = new ConcurrentHashMap<CacheObject<I>, Future<CacheObject<O>>>(100, 0.6f, 32);
        return new Function<I, O>() {

            public O apply(final I input) {

                final CacheObject<I> inputCacheObject = new CacheObject<I>(input);
                final Future<CacheObject<O>> memoizedOutput = memo.get(inputCacheObject);
                try {
                    if (memoizedOutput != null && memoizedOutput.get() != null) {
                        return memoizedOutput.get().get();
                    } else {

                        FutureTask<CacheObject<O>> futureTask = new FutureTask<CacheObject<O>>(new Callable<CacheObject<O>>() {
                            public CacheObject<O> call() throws Exception {
                                return new CacheObject<O>(function.apply(input));
                            }
                        });

                        memo.put(inputCacheObject, futureTask);
                        futureTask.run();
                        return futureTask.get().get();
                    }
                } catch (Throwable e) {
                    return function.apply(input);
                }
            }
        };
    }

    public static <I, O> Function<I, O> memoize(final Function<I, O> function, final MemoizeConfig config) {
        final Map<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> memo = new ConcurrentHashMap<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>(100, 0.6f, 32);
        final Long maxPersistenceTime = config.getTimeUnit().toMillis(config.getTimeValue());
        final AtomicBoolean isLRUInProgress = new AtomicBoolean(false);

        return new Function<I, O>() {

            public O apply(final I input) {

                final CacheObject<I> inputCacheObject = new CacheObject<I>(input);
                final long currentTimeinMillis = System.currentTimeMillis();

                try {
                    final Future<CacheObject<Tuple3<Long, Long, O>>> memoizedFutureOutput = memo.get(inputCacheObject);
                    final CacheObject<Tuple3<Long, Long, O>> memoizedOutput;
                    if (memoizedFutureOutput != null
                            && (memoizedOutput = memoizedFutureOutput.get()) != null
                            && (currentTimeinMillis - memoizedOutput.get()._1) <= maxPersistenceTime) {

                        memoizedOutput.get()._2 = currentTimeinMillis;

                        if (memo.size() > config.getSize() && !isLRUInProgress.get()) {
                            highPriorityTaskThreadPool.submit(new Runnable() {
                                public void run() {
                                    isLRUInProgress.set(true);
                                    while (memo.size() > config.getSize()) {
                                        removeLeastRecentlyUsedRecord();
                                    }
                                    isLRUInProgress.set(false);
                                }
                            });
                        }

                        return memoizedOutput.get()._3;
                    } else {

                        FutureTask<CacheObject<Tuple3<Long, Long, O>>> futureTask = new FutureTask<CacheObject<Tuple3<Long, Long, O>>>(new Callable<CacheObject<Tuple3<Long, Long, O>>>() {
                            public CacheObject<Tuple3<Long, Long, O>> call() throws Exception {
                                return new CacheObject<Tuple3<Long, Long, O>>(new Tuple3<Long, Long, O>(currentTimeinMillis, currentTimeinMillis, function.apply(input)));
                            }
                        });

                        memo.put(inputCacheObject, futureTask);
                        futureTask.run();
                        return futureTask.get().get()._3;
                    }
                } catch (Throwable e) {
                    return function.apply(input);
                }
            }

            private void removeLeastRecentlyUsedRecord() {
                try {
                    Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> toBeRemoved = reduce(memo.entrySet(), new Accumulator<Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>, Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>>>() {
                        public Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> accumulate(Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> former, Map.Entry<CacheObject<I>, Future<CacheObject<Tuple3<Long, Long, O>>>> latter) {

                            try {
                                final Long lastAccessTimeFormer = former.getValue().get().get()._2;
                                final Long lastAccessTimeLatter = latter.getValue().get().get()._2;

                                if (lastAccessTimeFormer <= lastAccessTimeLatter) {
                                    return former;
                                } else {
                                    return latter;
                                }

                            } catch (Throwable e) {
                                return former;
                            }
                        }
                    });

                    memo.remove(toBeRemoved.getKey());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static <ACCUM, EL> Accumulator<ACCUM, EL> memoize(final Accumulator<ACCUM, EL> accumulator) {

        final Map<CacheObject<Pair<ACCUM, EL>>, Future<CacheObject<ACCUM>>> memo = new ConcurrentHashMap<CacheObject<Pair<ACCUM, EL>>, Future<CacheObject<ACCUM>>>(100, 0.6f, 32);
        return new Accumulator<ACCUM, EL>() {
            public ACCUM accumulate(final ACCUM accum, final EL el) {
                final CacheObject<Pair<ACCUM, EL>> pairCacheObject = new CacheObject<Pair<ACCUM, EL>>(new Pair<ACCUM, EL>(accum, el));
                final Future<CacheObject<ACCUM>> memoizedOutput = memo.get(pairCacheObject);
                try {
                    if (memoizedOutput != null && memoizedOutput.get() != null) {
                        return memoizedOutput.get().get();
                    } else {

                        FutureTask<CacheObject<ACCUM>> futureTask = new FutureTask<CacheObject<ACCUM>>(new Callable<CacheObject<ACCUM>>() {
                            public CacheObject<ACCUM> call() throws Exception {
                                return new CacheObject<ACCUM>(accumulator.accumulate(accum, el));
                            }
                        });

                        memo.put(pairCacheObject, futureTask);
                        futureTask.run();
                        return futureTask.get().get();
                    }
                } catch (Throwable e) {
                    return accumulator.accumulate(accum, el);
                }
            }
        };

    }

    public static <I, O> Function<I, O> memoize(final Function<I, O> function, final ManagedCache managedCache) {
        return new Function<I, O>() {

            public O apply(final I input) {

                final O memoizedOutput = (O) managedCache.get(input);
                try {
                    if (memoizedOutput != null) {
                        return memoizedOutput;
                    } else {
                        O output = function.apply(input);
                        managedCache.put(input, output);
                        return output;
                    }
                } catch (Throwable e) {
                    return function.apply(input);
                }
            }
        };
    }

    public static Batch batch(int batchSize) {
        return new Batch(batchSize);
    }

    public static WorkDivisionStrategy parallel() {
        return new Parallel();
    }

    public static WorkDivisionStrategy parallel(int threads) {
        return new Parallel(threads);
    }

    public static Operation operation(String operationIdentifier) {
        return new Operation(operationIdentifier);
    }

    public static ExecutionThrottler throttler(String identity) {
        return new ExecutionThrottler(identity);
    }

    private static <I> Collection<I> getCollection(Collection<I> collection) {
        if (collection instanceof Set) {
            return new LinkedHashSet<I>();
        } else {
            return new LinkedList<I>();
        }
    }

    public static <T> ForkAndJoin<T> fork(T object) {
        return new ForkAndJoin<T>(object);
    }

    public static <T> void divideAndConquer(Collection<T> collection, final Task<Collection<T>> task, WorkDivisionStrategy partition) {
        final Collection<Collection<T>> collections = partition.divide(collection);
        each(collections, new RecordProcessor<Collection<T>>() {
            public void process(Collection<T> items) {
                task.execute(items);
            }
        }, parallel(collections.size()));
    }

    public static <T> List<T> extractWithIndex(List<T> list, Function<Integer, Boolean> predicate) {

        List<T> outList = new LinkedList<T>();
        int index = 0;

        for (T t : list) {
            if (predicate.apply(index++))
                outList.add(t);
        }
        return outList;
    }

    private static <T> void eachWithConditionChain(Collection<T> collection, Tuple2<Function<T, Boolean>, RecordProcessor<T>> predicateRecordProcessor, Tuple2<Function<T, Boolean>, RecordProcessor<T>>... predicateRecordProcessors) {
        List<Tuple2<Function<T, Boolean>, RecordProcessor<T>>> predicateRecordProcessorList = List(List(predicateRecordProcessor), Arrays.asList(predicateRecordProcessors));
        for (T t : collection) {
            for (Tuple2<Function<T, Boolean>, RecordProcessor<T>> tuple : predicateRecordProcessorList) {
                if (tuple._1.apply(t)) {
                    tuple._2.process(t);
                }
            }
        }
    }

    private static <T> void eachWithOptionChain(Collection<T> collection, Tuple2<Function<T, Boolean>, RecordProcessor<T>> predicateRecordProcessor, Tuple2<Function<T, Boolean>, RecordProcessor<T>>... predicateRecordProcessors) {
        List<Tuple2<Function<T, Boolean>, RecordProcessor<T>>> predicateRecordProcessorList = List(List(predicateRecordProcessor), Arrays.asList(predicateRecordProcessors));
        for (T t : collection) {
            for (Tuple2<Function<T, Boolean>, RecordProcessor<T>> tuple : predicateRecordProcessorList) {
                if (tuple._1.apply(t)) {
                    tuple._2.process(t);
                    break;
                }
            }
        }
    }

    public static <T1, T2> Collection<Tuple2<T1, T2>> zip(Collection<T1> first, Collection<T2> second) {

        if (first.size() > second.size() || first.size() < second.size()) {
            throw new IllegalArgumentException("Both collections should be of same size.");
        }

        List<Tuple2<T1, T2>> mergedList = new LinkedList<Tuple2<T1, T2>>();

        Iterator<T1> T1 = first.iterator();
        Iterator<T2> T2 = second.iterator();

        while (T1.hasNext()) {
            mergedList.add(new Tuple2<T1, T2>(T1.next(), T2.next()));
        }

        return mergedList;
    }

    public static void scheduleAtFixedRate(final Block block, long period) {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    block.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, period, period);
    }

    public static void scheduleAtFixedDelay(final Block block, long period) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    block.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, period, period);
    }

    public static void scheduleOnce(final Block block, long period) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    block.execute();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, period);
    }

    public static <T> RepeatCondition<T> repeat(RepeatableTask<T> task) {
        return new RepeatCondition<T>(task);
    }

    public static <T> WhenChecker<T> check(T object) {
        return new WhenChecker<T>(object);
    }

}

final class Batch implements WorkDivisionStrategy {
    private final int size;

    public Batch(int size) {
        this.size = size;
    }

    public <T> List<Collection<T>> divide(Iterable<T> work) {
        int counter = size;
        int collectionsIndex = 0;
        final List<Collection<T>> workDivisor = new ArrayList<Collection<T>>();

        workDivisor.add(new LinkedList<T>());

        for (T t : work) {

            if (counter == 0) {
                workDivisor.add(new LinkedList<T>());
                collectionsIndex++;
                counter = size;
            }

            workDivisor.get(collectionsIndex).add(t);
            counter--;
        }

        return workDivisor;
    }
}

final class Parallel implements WorkDivisionStrategy {
    protected static final int affinity = Config.getParallelDegree();

    private final int threads;

    public Parallel() {
        super();
        threads = affinity;
    }

    public Parallel(int threads) {
        this.threads = threads;
    }

    public <T> List<Collection<T>> divide(Iterable<T> work) {
        final List<Collection<T>> workDivisor = new ArrayList<Collection<T>>();

        int size = 0;

        if (work instanceof Collection) {
            size = ((Collection) work).size();
        } else {
            for (T t : work) {
                size++;
            }
        }

        int counter = threads > size ? size : threads;
        int collectionsIndex = 0;

        for (int i = 0; i < counter; i++) {
            workDivisor.add(new LinkedList<T>());
        }

        for (T t : work) {
            workDivisor.get(collectionsIndex % counter).add(t);
            collectionsIndex++;
        }

        return workDivisor;
    }

}

final class Operation {

    private final String operationIdentifier;

    public Operation(String operationIdentifier) {
        this.operationIdentifier = operationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Operation)) return false;

        Operation operation = (Operation) o;

        if (operationIdentifier != null ? !operationIdentifier.equals(operation.operationIdentifier) : operation.operationIdentifier != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return operationIdentifier != null ? operationIdentifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return operationIdentifier;
    }
}


final class ExecutionThrottler {
    private String identity;

    public ExecutionThrottler(String identity) {
        this.identity = identity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionThrottler)) return false;

        ExecutionThrottler that = (ExecutionThrottler) o;

        if (identity != null ? !identity.equals(that.identity) : that.identity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identity != null ? identity.hashCode() : 0;
    }
}