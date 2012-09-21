package org.atemsource.atem.utility.transform.impl.transformation;

import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class GenericAttributeTransformation<A, B> extends AbstractAttributeTransformation<A, B>
{
	private JavaTransformation<A, B> transformation;

	public JavaTransformation<A, B> getTransformation()
	{
		return transformation;
	}

	@Override
	public void mergeAB(A a, B b, TransformationContext ctx)
	{
		transformation.mergeAB(a, b, ctx);
	}

	@Override
	public void mergeBA(B b, A a, TransformationContext ctx)
	{
		transformation.mergeBA(b, a, ctx);
	}

	public void setTransformation(JavaTransformation<A, B> transformation)
	{
		this.transformation = transformation;
	}

}
