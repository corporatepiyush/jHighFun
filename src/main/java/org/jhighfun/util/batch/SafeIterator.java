package org.jhighfun.util.batch;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread safe proxy for existing Iterator
 *
 * @author Piyush Katariya
 */
public class SafeIterator<T> implements Iterator<T> {

    private final Iterator<T> iterator;
    private final Lock lock = new ReentrantLock();

    public SafeIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public boolean hasNext() {
        lock.lock();
        boolean hasNext = false;
        try {
            hasNext = iterator.hasNext();
        } catch (Exception e) {
            lock.unlock();
        }
        return hasNext;
    }

    public T next() {
        T next = null;
        try {
            next = iterator.next();
        } finally {
            lock.unlock();
        }
        return next;
    }

    public void remove() {
        lock.lock();
        try {
            iterator.remove();
        } finally {
            lock.unlock();
        }
    }
}
