package org.jhighfun.util.stream;


import java.util.Iterator;

public abstract class CustomizedIterator<IN, OUT> extends AbstractIterator<OUT> {

    private AbstractIterator<IN> iterator;

    public void setIterator(AbstractIterator<IN> iterator) {
        this.iterator = iterator;
    }
}
