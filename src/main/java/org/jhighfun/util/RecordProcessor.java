package org.jhighfun.util;

/**
 * A monoid which gets called as result of iteration over Collection
 *
 * @author Piyush Katariya
 */

public interface RecordProcessor<I> {
    public void process(I record);
}
