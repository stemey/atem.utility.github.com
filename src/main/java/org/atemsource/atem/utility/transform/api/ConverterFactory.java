package org.atemsource.atem.utility.transform.api;



public interface ConverterFactory
{

	Converter<?, ?> create(JavaConverter<?, ?> javaConverter);

	Converter<?, ?> getConverter(Class<?> a, Class<?> b);

	UniConverter<?, ?> getUniConverter(Class<?> a, Class<?> b);

	Converter<?, ?> register(JavaConverter<?, ?> javaConverter);

}
