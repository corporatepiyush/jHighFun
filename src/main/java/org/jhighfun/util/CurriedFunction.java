package org.jhighfun.util;


import java.util.ArrayList;
import java.util.List;

import static org.jhighfun.util.CollectionUtil.List;

public final class CurriedFunction<I, O> {

    private List<I> fixedInputs;
    private Function<I, O> function;

    public CurriedFunction(Function<I, O> function, List<I> fixedInputs) {
        this.function = function;
        this.fixedInputs = fixedInputs;
    }

    public CurriedFunction curry(List<I> dynamicInputs) {
        return new CurriedFunction(this.function, List(this.fixedInputs, dynamicInputs));
    }

    public CurriedFunction curry(I... dynamicInputs) {

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

        return new CurriedFunction(this.function, argList);
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

        return function.execute(argList);
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

        return function.execute(argList);

    }

}
