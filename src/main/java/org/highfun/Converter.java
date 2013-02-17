package org.highfun;

public interface Converter<I, O> {

	public O convert(I input);

}
