package org.jhighfun.util.stream;


import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentStreamIterator<T> extends AbstractStreamIterator<T> {

    private final AbstractStreamIterator<T> iterator;
    private final ReentrantLock lock = new ReentrantLock(true);

    public ConcurrentStreamIterator(AbstractStreamIterator<T> iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        if (!lock.isHeldByCurrentThread()) {
            lock.lock();
        }

        try {
            if (iterator.hasNext()) {
                return true;
            }
        } catch (Throwable t) {
            lock.unlock();
            throw new RuntimeException(t);
        }

        lock.unlock();
        return false;
    }

    public T next() {
        if (!lock.isHeldByCurrentThread()) {
            lock.lock();
        }

        try {
            return iterator.next();
        } finally {
            lock.unlock();
        }
    }

}