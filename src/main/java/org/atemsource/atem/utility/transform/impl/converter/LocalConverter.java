package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.UniConverter;

public class LocalConverter<A, B> implements Converter<A, B>, Constraining {

	private LocalUniConverter abConverter = new LocalUniConverter(this, true);

	private LocalUniConverter baConverter = new LocalUniConverter(this, false);

	private JavaConverter javaConverter;

	private Type<A> typeA;

	private Type<B> typeB;

	public LocalConverter(JavaConverter javaConverter, Type<A> typeA,
			Type<B> typeB) {
		super();
		this.javaConverter = javaConverter;
		this.constraining = javaConverter instanceof Constraining;
		this.typeA = typeA;
		this.typeB = typeB;
	}

	public Object convertAB(Object a) {
		return javaConverter.convertAB(a);
	}

	public Object convertBA(Object a) {
		return javaConverter.convertBA(a);

	}

	public UniConverter<A, B> getAB() {
		return (UniConverter<A, B>) abConverter;
	}

	public UniConverter<B, A> getBA() {
		return (UniConverter<B, A>) baConverter;
	}

	public Type<A> getTypeA() {
		return typeA;
	}

	public Type<B> getTypeB() {
		return typeB;
	}

	public void setTypeA(Type<A> typeA) {
		this.typeA = typeA;
	}

	public void setTypeB(Type<B> typeB) {
		this.typeB = typeB;
	}

	private boolean constraining;

	@Override
	public String[] getConstraintNamesAB() {
		if (constraining) {
			return ((Constraining) javaConverter).getConstraintNamesAB();
		} else {
			return null;
		}
	}

	@Override
	public Object getConstraintAB(String name) {
		if (constraining) {
			return ((Constraining) javaConverter).getConstraintAB(name);
		} else {
			return null;
		}
	}

	@Override
	public String[] getConstraintNamesBA() {
		if (constraining) {
			return ((Constraining) javaConverter).getConstraintNamesBA();
		} else {
			return null;
		}
	}

	@Override
	public Object getConstraintBA(String name) {
		if (constraining) {
			return ((Constraining) javaConverter).getConstraintBA(name);
		} else {
			return null;
		}
	}

}
