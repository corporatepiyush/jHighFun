package org.jhighfun.util;


import java.util.Collection;
import java.util.List;

public interface WorkDivisionStrategy {

    <T> List<Collection<T>> divide(Collection<T> work);
}
