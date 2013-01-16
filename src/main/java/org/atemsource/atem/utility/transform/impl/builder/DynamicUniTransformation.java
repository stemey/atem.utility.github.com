package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;

/**
* this is the unidirectional variant of DynamicTransformation. It resolves the target type at runtime by using the given typeCodeConverter.
*/
public class DynamicUniTransformation<A, B> implements UniTransformation<A, B>
{

	private final boolean aUnkown;

	private final ConverterFactory converterFactory;

	private final SingleAttribute<DerivedType> derivedTypeAttribute;

	private final EntityTypeRepository entityTypeRepository;

	private final Type type;

	private final TypeNameConverter typeCodeConverter;

	public DynamicUniTransformation(TypeNameConverter typeCodeConverter, EntityTypeRepository entityTypeRepository,
		Type<?> type, boolean aUnknown, SingleAttribute<DerivedType> derivedTypeAttribute,
		ConverterFactory converterFactory)
	{
		this.typeCodeConverter = typeCodeConverter;
		this.type = type;
		this.aUnkown = aUnknown;
		this.entityTypeRepository = entityTypeRepository;
		this.derivedTypeAttribute = derivedTypeAttribute;
		this.converterFactory = converterFactory;

	}

	@Override
	public B convert(A a, TransformationContext ctx)
	{
		if (a == null)
		{
			return null;
		}
		else
		{
			Type type = entityTypeRepository.getType(a);
			if (type instanceof EntityType)
			{
				EntityType targetType = entityTypeRepository.getEntityType(typeCodeConverter.convert((EntityType<?>) type));
				DerivedType derivedType = derivedTypeAttribute.getValue(targetType);
				UniTransformation transformation = derivedType.getTransformation().getAB();
				return (B) transformation.convert(a, ctx);
			}
			else
			{
				Converter<A, B> converter = (Converter<A, B>) converterFactory.get(type);
				if (converter != null)
				{
					return converter.getAB().convert(a, ctx);
				}
				else
				{
					return (B) a;
				}
			}
		}
	}

	@Override
	public Type<A> getSourceType()
	{
		if (aUnkown)
		{
			return null;
		}
		else
		{
			return type;
		}
	}

	@Override
	public Type<B> getTargetType()
	{
		if (aUnkown)
		{
			return type;
		}
		else
		{
			return null;
		}
	}

	@Override
	public Type<? extends B> getTargetType(Type<? extends A> sourceType)
	{
		if (aUnkown)
		{
			if (sourceType instanceof EntityType)
			{
				return entityTypeRepository.getEntityType(typeCodeConverter.convert((EntityType<?>) sourceType));
			}
			else
			{
				return type;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx)
	{
		if (a == null)
		{
			return null;
		}
		else
		{
			EntityType entityType = entityTypeRepository.getEntityType(a);
			EntityType targetType = entityTypeRepository.getEntityType(typeCodeConverter.convert(entityType));
			DerivedType derivedType = derivedTypeAttribute.getValue(targetType);
			UniTransformation transformation = derivedType.getTransformation().getAB();
			return (B) transformation.merge(a, b, ctx);
		}
	}

}
