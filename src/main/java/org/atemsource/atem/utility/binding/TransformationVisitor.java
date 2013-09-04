package org.atemsource.atem.utility.binding;

import java.util.List;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.transform.api.AttributeNameConverter;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;


public class TransformationVisitor implements ViewVisitor<TransformationContext>
{

	private final AttributeNameConverter attributeNameConverter;

	private final List<AttributeFilter> filters;

	private final TypeNameConverter typeNameConverter;

	public TransformationVisitor(TypeNameConverter typeNameConverter, List<AttributeFilter> filters,
		AttributeNameConverter attributeNameConverter)
	{
		super();
		this.typeNameConverter = typeNameConverter;
		this.attributeNameConverter = attributeNameConverter;
		this.filters = filters;
	}

	private <A, B> void convert(TransformationContext context, Attribute<?, A> attribute, Converter<A, B> converter)
	{
		String targetAttributeName;
		targetAttributeName = getTargetAttributeName(attribute);

		OneToOneAttributeTransformationBuilder<A, B, ?> builder;
		if (attribute instanceof SingleAttribute<?>)
		{
			builder = (OneToOneAttributeTransformationBuilder) context.getCurrent().transform();
		}
		else if (attribute instanceof CollectionAttribute)
		{
			builder =
				context.getCurrent().transformCollection().sort(((CollectionAttribute) attribute).getCollectionSortType());
		}
		else if (attribute instanceof MapAttribute)
		{
			MapAttribute map = (MapAttribute) attribute;
			if (map.getKeyType() != null)
			{
				builder = (OneToOneAttributeTransformationBuilder<A, B, ?>) context.getCurrent().transformMap();
			}
			else
			{
				return;
			}
		}
		else
		{
			return;
		}
		builder.from(attribute.getCode()).to(targetAttributeName);
		if (converter != null)
		{
			builder.convert(converter);
		}
		else if (attribute.getTargetType() == null)
		{
			builder.convertDynamically(typeNameConverter);
		}
	}

	private String getTargetAttributeName(Attribute<?, ?> attribute)
	{
		if (attributeNameConverter == null)
		{
			return attribute.getCode();
		}
		else
		{
			return attributeNameConverter.convert(attribute);
		}
	}

	@Override
	public void visit(TransformationContext context, Attribute attribute)
	{
		if (filters != null)
		{
			for (AttributeFilter filter : filters)
			{
				if (filter.isExcluded(attribute))
				{
					return;
				}
			}
		}
		Converter<?, ?> converter = null;
		Conversion conversion = ((JavaMetaData) attribute).getAnnotation(Conversion.class);
		if (conversion != null)
		{
			JavaConverter<?, ?> javaConverter;
			try
			{
				javaConverter = conversion.value().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new TechnicalException("cannot instantiate converter", e);
			}
			catch (IllegalAccessException e)
			{
				throw new TechnicalException("cannot instantiate converter", e);
			}
			converter = ConverterUtils.create(javaConverter);
		}
		convert(context, attribute, converter);

	}

	@Override
	public void visit(TransformationContext context, Attribute attribute,
		Visitor<TransformationContext> attributeVisitor)
	{
		if (filters != null)
		{
			for (AttributeFilter filter : filters)
			{
				if (filter.isExcluded(attribute))
				{
					return;
				}
			}
		}
		Conversion conversion = ((JavaMetaData) attribute).getAnnotation(Conversion.class);
		if (conversion != null)
		{
			JavaConverter<?, ?> javaConverter;
			try
			{
				javaConverter = conversion.value().newInstance();
			}
			catch (InstantiationException e)
			{
				throw new TechnicalException("cannot instantiate converter", e);
			}
			catch (IllegalAccessException e)
			{
				throw new TechnicalException("cannot instantiate converter", e);
			}
			Converter<?, ?> converter = ConverterUtils.create(javaConverter);
			convert(context, attribute, converter);
		}
		else
		{
			Converter<?, ?> converter = context.getCurrent().getConverterFactory().get(attribute.getTargetType());
			if (converter != null)
			{
				convert(context, attribute, converter);
			}
			else
			{
				EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
				// TODO in case of circular dependencies we need to lazily transform
				// this attribute or create another
				// transformation

				context.cascade(targetType, attributeVisitor);
				Transformation transformation = context.getTransformation(targetType);
				if (transformation == null)
				{
					throw new IllegalStateException("cannot tranform " + attribute);
				}
				convert(context, attribute, transformation);
			}
		}
	}

	@Override
	public boolean visitSubView(TransformationContext context, View view)
	{
		
		EntityType<?> subType = (EntityType<?>) view;
		context.visitSubview(subType);
		// we handle the visiting ourselves
		return false;
	}

	@Override
	public boolean visitSuperView(TransformationContext context, View view)
	{
		EntityType<?> superType = (EntityType<?>) view;
		context.visitSuperType(superType, this);
		// we handle the visiting ourselves
		return false;

	}
}
