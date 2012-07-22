package org.atemsource.atem.utility.transform.impl.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.InternalConverterFactory;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.UniConverter;


public class ConverterFactoryImpl implements ConverterFactory
{

	private static class ClassTuple
	{
		String a;

		String b;

		public ClassTuple(String a, String b)
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

	private List<Converter<?, ?>> converters;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Map<String, Converter> entityTypeToConverters = new HashMap<String, Converter>();

	private List<InternalConverterFactory> factories;

	private List<JavaConverter<?, ?>> javaConverters;

	private Map<ClassTuple, Converter> tupleToConverters = new HashMap<ClassTuple, Converter>();

	public Converter<?, ?> create(JavaConverter<?, ?> javaConverter)
	{
		return ConverterUtils.create(javaConverter);
	}

	private ClassTuple createClassTuple(Class<?> a, Class<?> b)
	{
		EntityType<?> entityTypeA = entityTypeRepository.getEntityType(a);
		EntityType<?> entityTypeB = entityTypeRepository.getEntityType(b);
		return new ClassTuple(entityTypeA.getCode(), entityTypeB.getCode());

	}

	@Override
	public Converter<?, ?> get(Type<?> type)
	{
		Converter converter = entityTypeToConverters.get(type.getCode());
		if (converter == null && factories!=null)
		{
			for (InternalConverterFactory factory : factories)
			{
				converter = factory.create(type);
				if (converter != null)
				{
					register(converter);
					return converter;
				}

			}
			return null;
		}
		else
		{
			return converter;
		}
	}

	public Converter<?, ?> getConverter(Class<?> a, Class<?> b)
	{
		EntityType<?> entityTypeA = entityTypeRepository.getEntityType(a);
		EntityType<?> entityTypeB = entityTypeRepository.getEntityType(b);
		return tupleToConverters.get(new ClassTuple(entityTypeA.getCode(), entityTypeB.getCode()));

	}

	public List<Converter<?, ?>> getConverters()
	{
		return converters;
	}

	public List<InternalConverterFactory> getFactories()
	{
		return factories;
	}

	public List<JavaConverter<?, ?>> getJavaConverters()
	{
		return javaConverters;
	}

	public UniConverter<?, ?> getUniConverter(Class<?> a, Class<?> b)
	{
		ClassTuple classTuple = createClassTuple(a, b);
		Converter converter = tupleToConverters.get(classTuple);
		if (converter == null)
		{
			converter = tupleToConverters.get(classTuple);
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

	@PostConstruct
	public void initialize()
	{
		if (converters != null)
		{
			for (Converter<?, ?> converter : converters)
			{
				register(converter);
			}
		}
		if (javaConverters != null)
		{
			for (JavaConverter<?, ?> converter : javaConverters)
			{
				register(converter);
			}
		}

	}

	@Override
	public void register(Converter<?, ?> converter)
	{
		tupleToConverters.put(new ClassTuple(converter.getTypeA().getCode(), converter.getTypeB().getCode()), converter);
		entityTypeToConverters.put(converter.getTypeA().getCode(), converter);
	}

	public Converter<?, ?> register(JavaConverter<?, ?> javaConverter)
	{
		Converter<?, ?> converter = create(javaConverter);
		register(converter);
		return converter;
	}

	public void setConverters(List<Converter<?, ?>> converters)
	{
		this.converters = converters;
	}

	public void setFactories(List<InternalConverterFactory> factories)
	{
		this.factories = factories;
	}

	public void setJavaConverters(List<JavaConverter<?, ?>> javaConverters)
	{
		this.javaConverters = javaConverters;
	}

}
