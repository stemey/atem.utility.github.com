package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;


public class ConverterUtils
{

	public static <A, B> Converter<A, B> create(JavaConverter<A, B> javaConverter)
	{
		EntityTypeRepository entityTypeRepository = BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
		Class[] actualTypeParameters =
			ReflectionUtils.getActualTypeParameters(javaConverter.getClass(), JavaConverter.class);
		Type typeB = entityTypeRepository.getType(actualTypeParameters[1]);
		Type typeA = entityTypeRepository.getType(actualTypeParameters[0]);
		LocalConverter localConverter = new LocalConverter(javaConverter, typeA, typeB);
		return localConverter;
	}
}
