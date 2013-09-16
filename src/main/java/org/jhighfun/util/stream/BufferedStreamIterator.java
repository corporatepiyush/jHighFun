package org.jhighfun.util.stream;


import java.util.LinkedList;
import java.util.List;

public class BufferedStreamIterator<T> extends AbstractStreamIterator<T> {

    private final AbstractStreamIterator<T> iterator;
    private final int bufferSize;
    private List<T> buffer = new LinkedList<T>();

    public BufferedStreamIterator(AbstractStreamIterator<T> iterator, int bufferSize) {
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
                i++;
            }
        }
        return this.buffer.isEmpty();
    }

    public T next() {
        return this.buffer.remove(0);
    }

    @Override
    public void closeResources() {
        this.buffer.clear();
        this.iterator.closeResources();
    }

}
