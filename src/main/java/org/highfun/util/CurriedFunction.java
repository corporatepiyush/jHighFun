package org.highfun.util;


import java.util.ArrayList;
import java.util.List;

public final class CurriedFunction<I, O> {

    private List<I> fixedInputs;
    private Function<I, O> function;

    public CurriedFunction(Function<I, O> function, List<I> fixedInputs) {
        this.function = function;
        this.fixedInputs = fixedInputs;
    }

    public O call(List<I> dynamicInputs) {

        List<I> argList = new ArrayList<I>();

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

        List<I> argList = new ArrayList<I>();

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
