package org.jhighfun.util;

import java.util.Collection;

/**
 * Abstraction to implement work(Collection of Units to operate on) division strategy
 *
 * @author Piyush Katariya
 */

public interface WorkDivisionStrategy {

    <T> Collection<Collection<T>> divide(Iterable<T> work);
}
