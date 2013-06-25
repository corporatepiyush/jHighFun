package org.jhighfun.util;

/**
 * Created with IntelliJ IDEA.
 * User: Piyush
 * Date: 6/25/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface When<I, O> {
    ThenOrOtherwise<I, O> when(Function<I, Boolean> condition);

    ThenOrOtherwise<I, O> when(I input);
}
