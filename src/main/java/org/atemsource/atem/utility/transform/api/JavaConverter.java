package org.atemsource.atem.utility.transform.api;

public interface JavaConverter<A, B>
{

	public B convertAB(A a);

	public A convertBA(B b);

}
