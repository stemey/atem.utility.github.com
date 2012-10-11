package org.atemsource.atem.utility.transform.api;

public interface JavaUniConverter<A, B>
{

	public B convert(A a, TransformationContext ctx);

}
