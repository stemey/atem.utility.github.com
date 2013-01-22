package org.atemsource.atem.utility.transform.impl.builder;

public interface Filter<T>
{
	boolean isInluded(T t);
}
