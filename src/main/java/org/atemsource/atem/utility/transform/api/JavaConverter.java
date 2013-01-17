package org.atemsource.atem.utility.transform.api;
/**
* Interface for Converters of java types. Retrieve the Atem converter by using CoverterUtils.  
*/
public interface JavaConverter<A, B>
{

	public B convertAB(A a, TransformationContext ctx);

	public A convertBA(B b, TransformationContext ctx);

}
