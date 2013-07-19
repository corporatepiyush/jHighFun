package org.jhighfun.util.stream;


import java.util.Iterator;

public abstract class CustomizedIterator<IN, OUT> extends AbstractIterator<OUT> {

    private Iterator<IN> iterator;

    public void setIterator(Iterator<IN> iterator){
        this.iterator = iterator;
    }
}
