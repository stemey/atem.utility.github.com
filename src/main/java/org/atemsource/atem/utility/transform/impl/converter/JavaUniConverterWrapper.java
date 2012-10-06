package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;

public class JavaUniConverterWrapper implements UniConverter<Object, Object> {
	private JavaConverterWrapper<Object, Object> converter;

	private boolean direction;

	public JavaUniConverterWrapper(JavaConverterWrapper converter, boolean direction) {
		super();
		this.direction = direction;
		this.converter = converter;
	}

	public Object convert(Object a, TransformationContext ctx) {
		if (direction) {
			return converter.convertAB(a);
		} else {
			return converter.convertBA(a);
		}
	}

	public Type<Object> getSourceType() {
		if (direction) {
			return converter.getTypeA();
		} else {
			return converter.getTypeB();
		}
	}

	public Type<Object> getTargetType() {
		if (direction) {
			return converter.getTypeB();
		} else {
			return converter.getTypeA();
		}
	}

	@Override
	public Type<? extends Object> getTargetType(
			Type<? extends Object> sourceType) {
		if (direction) {
			return converter.getTypeB();
		} else {
			return converter.getTypeA();
		}
	}

}
