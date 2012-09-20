package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public class PrimitiveTransformation<A,B> implements Transformation<A,B> {
private Converter<A,B> converter;

public UniTransformation<A, B> getAB() {
	return new PrimitiveUniTransformation(converter.getAB());
}

public UniTransformation<B, A> getBA() {
	return new PrimitiveUniTransformation(converter.getBA());
}

public PrimitiveTransformation(Converter<A, B> converter) {
	super();
	this.converter = converter;
}

public Type<?> getTypeA() {
	return converter.getTypeA();
}

public Type<?> getTypeB() {
	return converter.getTypeB();
}
}
