package org.jhighfun.util.stream;


public abstract class CustomizedStreamer<IN, OUT> extends AbstractStreamer<OUT> {

    private AbstractStreamer<IN> iterator;

    public void setIterator(AbstractStreamer<IN> iterator) {
        this.iterator = iterator;
    }
}
