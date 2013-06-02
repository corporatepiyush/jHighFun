package org.jhighfun.util;

/**
 * A monoid which gets called as result of iteration over Collection with current index
 *
 * @author Piyush Katariya
 */

public interface RecordWithIndexProcessor<I> {
    public void process(I item, int index);
}
