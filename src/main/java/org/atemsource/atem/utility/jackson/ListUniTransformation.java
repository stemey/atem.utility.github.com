package org.atemsource.atem.utility.jackson;

import java.util.ArrayList;
import java.util.List;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.UniTransformation;


public class ListUniTransformation<A, B> implements UniTransformation<A, B>
{
	private List<UniTransformation<A, B>> transformations = new ArrayList<UniTransformation<A, B>>();

	public void addTransformation(UniTransformation<A, B> transformation)
	{
		transformations.add(transformation);
	}

	@Override
	public B convert(A a)
	{
		B b = transformations.get(0).convert(a);
		for (int index = 1; index < transformations.size(); index++)
		{
			transformations.get(index).merge(a, b);
		}
		return b;
	}

	@Override
	public Type<A> getSourceType()
	{
		return transformations.get(0).getSourceType();
	}

	@Override
	public Type<B> getTargetType()
	{
		return transformations.get(0).getTargetType();
	}

	@Override
	public B merge(A a, B b)
	{
		B newB = b;
		for (int index = 0; index < transformations.size(); index++)
		{
			newB = transformations.get(index).merge(a, newB);
		}
		return newB;

	}
}
