package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.OneToOneAttributeTransformation;

public class PrimitiveUniTransformation<A,B> implements UniTransformation<A,B> {
	private UniConverter<A, B> uniConverter;

	public B convert(A a, TransformationContext ctx) {
		return uniConverter.convert(a, ctx);
	}

	public Type<A> getSourceType() {
		return uniConverter.getSourceType();
	}

	public Type<B> getTargetType() {
		return uniConverter.getTargetType();
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx) {
		return convert(a, ctx);
	}

	public PrimitiveUniTransformation(UniConverter<A, B> uniConverter) {
		super();
		this.uniConverter = uniConverter;
	}

	@Override
	public Type<? extends B> getTargetType(Type<? extends A> sourceType) {
		return uniConverter.getTargetType(sourceType);
	}

	@Override
	public OneToOneAttributeTransformation<A, B> getAttributeTransformationByTarget(String attributeCode) {
		return null;
	}

	@Override
	public OneToOneAttributeTransformation<A, B> getAttributeTransformationBySource(String attributeCode) {
		return null;
	}
}
