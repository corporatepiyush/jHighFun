package org.jhighfun.util.stream;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BufferIterator<T> extends AbstractIterator<T> {

    private final Iterator<T> iterator;
    private final int bufferSize;
    private List<T> buffer = new LinkedList<T>();

    public BufferIterator(Iterator<T> iterator, int bufferSize) {
        this.iterator = iterator;
        this.bufferSize = bufferSize;
    }

    public boolean hasNext() {
        if (this.buffer.isEmpty()) {
            int i = 1;
            while (this.iterator.hasNext()) {
                this.buffer.add(this.iterator.next());
                if (i == this.bufferSize) {
                    break;
                }
            }
        }
        return this.buffer.isEmpty();
    }

    public T next() {
        return this.buffer.remove(0);
    }

}
