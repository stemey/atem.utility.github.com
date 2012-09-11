package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public class InstanceBasedDynamicUniTransformation<A,B> implements UniTransformation<A, B>{
private Type typeA;
private Type typeB;
public InstanceBasedDynamicUniTransformation(TransformationFinder finder,
			boolean ab,Type typeA,Type typeB) {
		super();
		this.finder = finder;
		this.ab = ab;
		this.typeA=typeA;
		this.typeB=typeB;
	}

private TransformationFinder finder;
private boolean ab;
	@Override
	public B convert(A a, TransformationContext ctx) {
		if (ab) {
			return (B) finder.getAB(a,  ctx).convert(a, ctx);
		}else{
			return (B) finder.getBA(a,  ctx).convert(a, ctx);
		}
	}

	@Override
	public Type<A> getSourceType() {
		return typeA;
	}

	@Override
	public Type<B> getTargetType() {
		return typeB;
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx) {
		if (ab) {
			return (B) finder.getAB(a,  ctx).merge(a, b,ctx);
		}else{
			return (B) finder.getBA(a,  ctx).merge(a,b, ctx);
		}
	}

}
