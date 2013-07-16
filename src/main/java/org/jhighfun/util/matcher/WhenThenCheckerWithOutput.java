package org.jhighfun.util.matcher;


import org.jhighfun.util.Function;

import java.util.LinkedList;
import java.util.List;

public final class WhenThenCheckerWithOutput<IN, OUT> implements WhenFunctionExecutor<IN, OUT>, ThenFunctionExecutor<IN, OUT> {

    private final IN inputObject;
    private final List<Function<IN, Boolean>> conditions = new LinkedList<Function<IN, Boolean>>();
    private final List<Function<IN, OUT>> outputFunctions = new LinkedList<Function<IN, OUT>>();

    public WhenThenCheckerWithOutput(IN inputObject, Function<IN, Boolean> condition, Function<IN, OUT> outputFunction) {
        this.inputObject = inputObject;
        this.conditions.add(condition);
        this.outputFunctions.add(outputFunction);
    }

    public ThenFunctionExecutor<IN, OUT> whenMatchesWith(final IN matchingInput) {
        this.conditions.add(new Function<IN, Boolean>() {
            public Boolean apply(IN arg) {
                return matchingInput != null && matchingInput.equals(inputObject);
            }
        });
        return this;
    }

    public ThenFunctionExecutor<IN, OUT> whenMatchesWith(Function<IN, Boolean> condition) {
        this.conditions.add(condition);
        return this;
    }

    public WhenFunctionExecutor<IN, OUT> thenReturn(final OUT outputObject) {
        this.outputFunctions.add(new Function<IN, OUT>() {
            public OUT apply(IN arg) {
                return outputObject;
            }
        });
        return this;
    }

    public WhenFunctionExecutor<IN, OUT> thenReturn(Function<IN, OUT> function) {
        this.outputFunctions.add(function);
        return this;
    }

    public OUT otherwiseReturn(OUT outputObject) {
        Function<IN, OUT> thenFunction = getFunctionToExecute();
        return thenFunction != null ? thenFunction.apply(this.inputObject) : outputObject;
    }

    public OUT otherwiseReturn(Function<IN, OUT> function) {
        Function<IN, OUT> thenFunction = getFunctionToExecute();
        return thenFunction != null ? thenFunction.apply(this.inputObject) : function.apply(this.inputObject);
    }

    private Function<IN, OUT> getFunctionToExecute() {
        int index = 0;

        for (Function<IN, Boolean> condition : conditions) {
            if (condition.apply(inputObject)) {
                break;
            }
            index++;
        }

        return index == conditions.size() ? null : outputFunctions.get(index);
    }
}
