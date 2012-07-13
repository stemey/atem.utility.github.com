package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.Type;


public interface ConverterFactory
{

	Converter<?, ?> create(JavaConverter<?, ?> javaConverter);

	Converter<?, ?> get(Type<?> targetType);

	Converter<?, ?> getConverter(Class<?> a, Class<?> b);

	UniConverter<?, ?> getUniConverter(Class<?> a, Class<?> b);

	void register(Converter<?, ?> converter);

	Converter<?, ?> register(JavaConverter<?, ?> javaConverter);

}
