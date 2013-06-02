package org.jhighfun.util;

import java.util.Collection;
import java.util.List;

/**
 * Abstraction to implement work(Collection of Units to operate on) division strategy
 *
 * @author Piyush Katariya
 */

public interface WorkDivisionStrategy {

    <T> List<Collection<T>> divide(Collection<T> work);
}
