package org.jhighfun.util.stream;


public abstract class CustomizedStreamIterator<IN, OUT> extends AbstractStreamIterator<OUT> {

    private AbstractStreamIterator<IN> customizedIterator;

    public void setCustomizedIterator(AbstractStreamIterator<IN> customizedIterator) {
        this.customizedIterator = customizedIterator;
    }
}
