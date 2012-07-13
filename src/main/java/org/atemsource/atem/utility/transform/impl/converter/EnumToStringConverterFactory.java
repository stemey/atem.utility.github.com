package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.InternalConverterFactory;


public class EnumToStringConverterFactory implements InternalConverterFactory
{

	@Override
	public <A> Converter<A, ?> create(Type<A> a)
	{
		if (Enum.class.isAssignableFrom(a.getJavaType()))
		{
			return (Converter<A, ?>) ConverterUtils.create(new EnumToStringConverter((Class<Enum>) a.getJavaType()));
		}
		else
		{
			return null;
		}
	}
}
