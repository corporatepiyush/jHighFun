package org.jhighfun.util;


import java.util.ArrayList;
import java.util.List;

/**
 * A curried form of function with ability of currying further and getting invoked
 *
 * @author Piyush Katariya
 */

public final class CurriedFunction<I, O> {

    private final List<I> fixedInputs;
    private final Function<List<I>, O> function;

    public CurriedFunction(Function<List<I>, O> function, List<I> fixedInputs) {
        this.function = function;
        this.fixedInputs = fixedInputs;
    }

    public CurriedFunction curry(List<I> dynamicInputs) {
        return new CurriedFunction(this.function, CollectionUtil.FlattenList(this.fixedInputs, dynamicInputs));
    }

    public CurriedFunction curry(I... dynamicInputs) {

        final List<I> argList = new ArrayList<I>();

        if (fixedInputs != null) {
            for (I i : fixedInputs) {
                argList.add(i);
            }
        }

        if (dynamicInputs != null) {
            for (I i : dynamicInputs) {
                argList.add(i);
            }
        }

        return new CurriedFunction(this.function, argList);
    }

    public O call(List<I> dynamicInputs) {

        final List<I> argList = new ArrayList<I>();

        if (fixedInputs != null) {
            for (I i : fixedInputs) {
                argList.add(i);
            }
        }

        if (dynamicInputs != null) {
            for (I i : dynamicInputs) {
                argList.add(i);
            }
        }

        return function.apply(argList);
    }

    public O call(I... dynamicInputs) {

        final List<I> argList = new ArrayList<I>();

        if (fixedInputs != null) {
            for (I i : fixedInputs) {
                argList.add(i);
            }
        }

        if (dynamicInputs != null) {
            for (I i : dynamicInputs) {
                argList.add(i);
            }
        }

        return function.apply(argList);

    }

}
