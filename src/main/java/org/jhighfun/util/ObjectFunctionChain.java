package org.jhighfun.util;


import java.util.LinkedList;
import java.util.List;

public class ObjectFunctionChain<I> {

    private I object;

    public ObjectFunctionChain(I object) {
        this.object = object;
    }

    public <O> ObjectFunctionChain<O> transform(Converter<I, O> converter) {
        return new ObjectFunctionChain<O>(converter.convert(object));
    }

    public CollectionFunctionChain<I> toCollection() {
        final List<I> collection = new LinkedList<I>();
        collection.add(object);
        return new CollectionFunctionChain<I>(collection);
    }

    public ObjectFunctionChain<I> execute(final Task<I> task) {
        task.execute(object);
        return this;
    }

    public ObjectFunctionChain<I> executeAsync(final Task<I> task) {
        FunctionUtil.executeAsync(new Block() {
            public void execute() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeLater(final Task<I> task) {
        FunctionUtil.executeLater(new Block() {
            public void execute() {
                task.execute(object);
            }
        });
        return this;
    }

    public ObjectFunctionChain<I> executeWithGlobalLock(final Task<I> task) {
        FunctionUtil.executeWithGlobalLock(new Block() {
            public void execute() {
                task.execute(object);
            }
        });
        return this;
    }

    public I extract() {
        return object;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
