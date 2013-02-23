package org.highfun.util;

public interface Converter<I, O> {

    public O convert(I input);

}
