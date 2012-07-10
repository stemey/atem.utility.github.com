package org.atemsource.atem.utility.transform.impl.converter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.UniConverter;


public class ConverterFactoryImpl
{

	private static class ClassTuple
	{
		Class<?> a;

		Class<?> b;

		public ClassTuple(Class<?> a, Class<?> b)
		{
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ClassTuple other = (ClassTuple) obj;
			if (a == null)
			{
				if (other.a != null)
					return false;
			}
			else if (!a.equals(other.a))
				return false;
			if (b == null)
			{
				if (other.b != null)
					return false;
			}
			else if (!b.equals(other.b))
				return false;
			return true;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			result = prime * result + ((b == null) ? 0 : b.hashCode());
			return result;
		}
	}

	private Map<ClassTuple, Converter> converters = new HashMap<ClassTuple, Converter>();

	@Inject
	private EntityTypeRepository entityTypeRepository;

	public Converter<?, ?> create(JavaConverter<?, ?> javaConverter)
	{
		Class[] actualTypeParameters =
			ReflectionUtils.getActualTypeParameters(javaConverter.getClass(), JavaConverter.class);
		Type typeB = entityTypeRepository.getType(actualTypeParameters[1]);
		Type typeA = entityTypeRepository.getType(actualTypeParameters[0]);
		LocalConverter localConverter = new LocalConverter(javaConverter, typeA, typeB);
		return localConverter;
	}

	public Converter<?, ?> getConverter(Class<?> a, Class<?> b)
	{
		return converters.get(new ClassTuple(a, b));

	}

	public UniConverter<?, ?> getUniConverter(Class<?> a, Class<?> b)
	{
		Converter converter = converters.get(new ClassTuple(a, b));
		if (converter == null)
		{
			converter = converters.get(new ClassTuple(b, a));
			if (converter == null)
			{
				return null;
			}
			else
			{
				return converter.getBA();
			}
		}
		else
		{
			return converter.getAB();
		}
	}

	public Converter<?, ?> register(JavaConverter<?, ?> javaConverter)
	{
		Class[] actualTypeParameters =
			ReflectionUtils.getActualTypeParameters(javaConverter.getClass(), JavaConverter.class);
		Type typeB = entityTypeRepository.getType(actualTypeParameters[1]);
		Type typeA = entityTypeRepository.getType(actualTypeParameters[0]);
		LocalConverter localConverter = new LocalConverter(javaConverter, typeA, typeB);
		converters.put(new ClassTuple(actualTypeParameters[0], actualTypeParameters[1]), localConverter);
		return localConverter;
	}

}
