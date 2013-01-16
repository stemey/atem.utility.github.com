package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.Type;

/**
* The ConverterFactory provides standard converters for types (usually primitive types). This is helpful for conversion to types that don't support all the types from the source repository. E.g. Json does not support a date type so registering a standard converter for the conversion from a java.util.Date to String makes sense when transforming java types to json types.  
*/
public interface ConverterFactory
{

	Converter<?, ?> create(JavaConverter<?, ?> javaConverter);

/**
* get a standard converter to convert from the given source type.
* @param sourceType source type
*/
	Converter<?, ?> get(Type<?> sourceType);

/**
* get a converter to convert from the given source class to the given target class.
*/
	Converter<?, ?> getConverter(Class<?> a, Class<?> b);


/**
* get a uni converter to convert from the given source class to the given target class.
*/
	UniConverter<?, ?> getUniConverter(Class<?> a, Class<?> b);

/**
* register a standard converter.
*/
	void register(Converter<?, ?> converter);

/**
* register a standard java converter.
*/
	Converter<?, ?> register(JavaConverter<?, ?> javaConverter);

}
