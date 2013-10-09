package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;

public abstract class AbstractLocalConverter<A, B> implements Converter<A,B>{

	private final LocalUniConverter abConverter = new LocalUniConverter(this, true);
	private final LocalUniConverter baConverter = new LocalUniConverter(this, false);
	protected Type<A> typeA;
	protected Type<B> typeB;

	public AbstractLocalConverter() {
		super();
	}
	public abstract B convertAB(A a, TransformationContext ctx);
	public abstract A convertBA(B b, TransformationContext ctx);
	@Override
	public UniConverter<A, B> getAB() {
		return (UniConverter<A, B>) abConverter;
	}

	@Override
	public UniConverter<B, A> getBA() {
		return (UniConverter<B, A>) baConverter;
	}

	@Override
	public Type<A> getTypeA() {
		return typeA;
	}

	@Override
	public Type<B> getTypeB() {
		return typeB;
	}

	public void setTypeA(Type<A> typeA) {
		this.typeA = typeA;
	}

	public void setTypeB(Type<B> typeB) {
		this.typeB = typeB;
	}

}