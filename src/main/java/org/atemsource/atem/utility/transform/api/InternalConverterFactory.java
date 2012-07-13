package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.Type;


public interface InternalConverterFactory
{
	<A> Converter<A, ?> create(Type<A> a);
}
