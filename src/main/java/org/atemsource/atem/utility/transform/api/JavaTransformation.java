package org.atemsource.atem.utility.transform.api;

/**
* A java transformation.
* TODO why does merge not return a new instance
* TODO why does it not extend JavaConverter 
*/
public interface JavaTransformation<A, B>
{
	void mergeAB(A a, B b, TransformationContext ctx);

	void mergeBA(B b, A a, TransformationContext ctx);
}
