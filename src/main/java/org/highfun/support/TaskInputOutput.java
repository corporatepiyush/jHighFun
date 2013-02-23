package org.highfun.support;


public final class TaskInputOutput<I, O> {

    private I input;
    private O output;

    public TaskInputOutput(I input) {
        this.input = input;
    }

    public void setOutput(O output) {
        this.output = output;
    }

    public I getInput() {
        return input;
    }

    public O getOutput() {
        return output;
    }
}
