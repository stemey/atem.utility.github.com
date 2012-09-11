package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public class InstanceBasedDynamicTransformation<A,B> implements Transformation<A,B> {

	
	private TransformationFinder<A,B> finder;
	private InstanceBasedDynamicUniTransformation ab;
	private InstanceBasedDynamicUniTransformation ba;
	private Type typeA;
	private Type typeB;
	public InstanceBasedDynamicTransformation(TransformationFinder finder,Type typeA,Type typeB) {
		super();
		this.finder = finder;
		this.ab=new InstanceBasedDynamicUniTransformation(finder,true,typeA,typeB);
		this.ba=new InstanceBasedDynamicUniTransformation(finder,false,typeB,typeA);
		this.typeA=typeA;
		this.typeB=typeB;
	}

	@Override
	public Type getTypeA() {
		return typeA;
	}

	@Override
	public Type getTypeB() {
		return typeB;
	}

	@Override
	public UniTransformation<A, B> getAB() {
		return ab;
	}

	@Override
	public UniTransformation<B, A> getBA() {
		return ba;
	}

}
