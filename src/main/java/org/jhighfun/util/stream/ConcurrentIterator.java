package org.jhighfun.util.stream;


import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentIterator<T> extends AbstractIterator<T> {

    private final Iterator<T> iterator;
    private final ReentrantLock lock = new ReentrantLock(true);

    public ConcurrentIterator(Iterator<T> iterator) {
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