package org.atemsource.atem.utility.transform.impl.builder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;


/**
 * A base type for transforming one source attribute to one target attribute.
 */
public abstract class OneToOneAttributeTransformationBuilder<A, B, T extends OneToOneAttributeTransformationBuilder<A, B, T>>
	extends AbstractAttributeTransformationBuilder<A, B, T>
{

	private Converter<?, ?> converter;

	private String sourceAttribute;

	private String targetAttribute;

	protected void addMetaData(Attribute<?, ?> attribute)
	{
		for (Map.Entry<String, Object> metaEntry : meta.entrySet())
		{
			attribute.setMetaValue(metaEntry.getKey(), metaEntry.getValue());
		}
	}

	/**
	 * specify converter.
	 */
	public T convert(Converter<?, ?> converter)
	{
		this.converter = converter;
		return (T) this;
	}

	/**
	 * specify converter.
	 */
	public T convert(JavaConverter<?, ?> javaConverter)
	{
		this.converter = ConverterUtils.create(javaConverter);
		return (T) this;
	}

	/**
	 * specify uni directional converter .
	 */
	public T convert(JavaUniConverter<?, ?> javaConverter)
	{
		this.converter = ConverterUtils.create(javaConverter);
		return (T) this;
	}

	/**
	 * if the source type is not known at build time (e.g. it is null which means the java type is Object), then use the
	 * TypeNameConverter to find the target type code for the actual source type at transformtion time. The
	 * transformation is defined on the meta attribute "derived_object" of the resolved target type.//
	 * TODO the TypeNameConverter should be the same that is used to create the target types. It should be possile to add the
	 * TypeNameConverter to the TransformationBuilderFactory und thus this method does not need to be called in the usual
	 * situation.
	 */
	public T convertDynamically(TypeNameConverter typeCodeConverter)
	{
		Attribute metaAttribute =
			entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
		if (metaAttribute == null)
		{
			throw new IllegalStateException("cannot convert dynamically if metaAttribute is missing");
		}
		this.converter =
			new DynamicTransformation(typeCodeConverter, sourceType, entityTypeRepository,
				(SingleAttribute) metaAttribute, converterFactory);
		return (T) this;
	}

	@Override
	public T from(String sourceAttribute)
	{
		this.sourceAttribute = sourceAttribute;
		return (T) this;
	}

	@Override
	public A fromMethod()
	{
		Enhancer enhancer = new Enhancer();
		return (A) enhancer.create(sourceType.getJavaType(), new PathRecorder(this));
	}

	protected Converter<?, ?> getConverter(Type<?> type)
	{
		if (converter != null)
		{
			return converter;
		}
		else
		{
			return converterFactory.get(type);
		}
	}

	public String getSourceAttribute()
	{
		return sourceAttribute;
	}

	public String getTargetAttribute()
	{
		if (targetAttribute == null)
		{
			return sourceAttribute;
		}
		return targetAttribute;
	}

	protected Type<?> getTargetType(AttributePath sourcePath, Converter<?, ?> converter)
	{
		Type<?> attributeTargetType;
		if (converter != null)
		{
			attributeTargetType = converter.getTypeB();

		}
		else
		{
			attributeTargetType = sourcePath.getTargetType().getType();
		}
		return attributeTargetType;
	}

	protected Transformation<?, ?> getTransformation(Type<?> type)
	{
		if (converter != null && converter instanceof Transformation)
		{
			return (Transformation<?, ?>) converter;
		}
		Converter<?, ?> simpleConverter = getConverter(type);
		if (simpleConverter != null)
		{
			return new PrimitiveTransformation(simpleConverter);
		}
		else
		{
			return null;
		}
	}

	protected Type[] getValidTargetTypes(AttributePath sourcePath, Converter<?, ?> converter)
	{
		if (true)
			return null;
		Type[] validTypes;
		if (converter != null)
		{
			Type[] validTargetTypes = sourcePath.getAttribute().getValidTargetTypes();
			if (validTargetTypes == null)
			{
				validTypes = null;
			}
			else
			{
				Set<Type> validTypeSet = new HashSet<Type>();
				for (Type validTargetType : validTargetTypes)
				{
					if (converter instanceof EntityTypeTransformation
						&& ((EntityTypeTransformation) converter).getTypeA() == null)
					{
						// TODO in the case of binding the converter might not be
						// fully initialized.
					}
					else
					{
						Type targetType = converter.getAB().getTargetType(validTargetType);
						validTypeSet.add(targetType);
					}
				}
				validTypes = validTypeSet.toArray(new Type[validTypeSet.size()]);
			}

		}
		else
		{

			validTypes = sourcePath.getAttribute().getValidTargetTypes();
		}
		return validTypes;
	}

	/**
	 * define the target attribute.
	 */
	public T to(String targetAttribute)
	{
		this.targetAttribute = targetAttribute;
		return (T) this;
	}

}
