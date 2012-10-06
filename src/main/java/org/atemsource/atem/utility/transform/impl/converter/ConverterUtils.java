package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.codehaus.jackson.JsonNode;


public class ConverterUtils
{
	
	private static final class JavaBiConverter<A,B> implements JavaConverter<A, B>{
		private JavaUniConverter<A, B> uniConverter;

		public JavaBiConverter(JavaUniConverter<A, B> uniConverter) {
			super();
			this.uniConverter = uniConverter;
		}

		@Override
		public B convertAB(A a) {
			return uniConverter.convert(a);
		}

		@Override
		public A convertBA(B b) {
			throw new UnsupportedOperationException("this is a unidrirectional convreter");
		}
		
	}

	public static <A, B> Converter<A, B> create(JavaUniConverter<A, B> javaConverter)
	{
		EntityTypeRepository entityTypeRepository = BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
		Class[] actualTypeParameters =
			ReflectionUtils.getActualTypeParameters(javaConverter.getClass(), JavaUniConverter.class);
		Class classB = actualTypeParameters[1];
		Type typeB = entityTypeRepository.getType(classB);
		Class classA = actualTypeParameters[0];
		Type typeA = entityTypeRepository.getType(classA);
		if (typeA == null && !classA.equals(Object.class))
		{
			throw new IllegalArgumentException("cannot create converter. " + classA.getName() + " is not an atem type");
		}
		if (typeB == null && !classB.equals(Object.class) && !classB.equals(JsonNode.class))
		{
			// TODO fix dependency to JsonNode. Provide proper type for JsonNode
			throw new IllegalArgumentException("cannot create converter. " + classB.getName() + " is not an atem type");
		}
		JavaConverterWrapper javaConverterWrapper = new JavaConverterWrapper(new JavaBiConverter<A, B>(javaConverter), typeA, typeB);
		return javaConverterWrapper;
	}
	public static <A, B> Converter<A, B> create(JavaConverter<A, B> javaConverter)
	{
		EntityTypeRepository entityTypeRepository = BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
		Class[] actualTypeParameters =
			ReflectionUtils.getActualTypeParameters(javaConverter.getClass(), JavaConverter.class);
		Class classB = actualTypeParameters[1];
		Type typeB = entityTypeRepository.getType(classB);
		Class classA = actualTypeParameters[0];
		Type typeA = entityTypeRepository.getType(classA);
		if (typeA == null && !classA.equals(Object.class))
		{
			throw new IllegalArgumentException("cannot create converter. " + classA.getName() + " is not an atem type");
		}
		if (typeB == null && !classB.equals(Object.class) && !classB.equals(JsonNode.class))
		{
			// TODO fix dependency to JsonNode. Provide proper type for JsonNode
			throw new IllegalArgumentException("cannot create converter. " + classB.getName() + " is not an atem type");
		}
		JavaConverterWrapper javaConverterWrapper = new JavaConverterWrapper(javaConverter, typeA, typeB);
		return javaConverterWrapper;
	}
}
