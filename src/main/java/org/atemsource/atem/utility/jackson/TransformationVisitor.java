package org.atemsource.atem.utility.jackson;

import java.util.List;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


public class TransformationVisitor implements ViewVisitor<TransformationContext>
{

	private EntityTypeRepository entityTypeRepository;

	private List<AttributeFilter> filters;

	private JavaUniConverter<String, String> nameConverter;

	public TransformationVisitor(EntityTypeRepository entityTypeRepository, List<AttributeFilter> filters,
		JavaUniConverter<String, String> nameConverter)
	{
		super();
		this.nameConverter = nameConverter;
		this.filters = filters;
		this.entityTypeRepository = entityTypeRepository;
	}

	public <A, B> Converter<A, B> cast(Converter<A, B> c)
	{
		return c;
	}

	private void convert(TransformationContext context, Attribute attribute, Converter converter,
		JsonProperty jsonProperty)
	{
		String targetAttributeName;
		if (jsonProperty == null)
		{
			targetAttributeName = getTargetAttributeName(attribute);
		}
		else
		{
			targetAttributeName = jsonProperty.value();
		}

		AttributeTransformationBuilder<?, ?> builder;
		if (attribute instanceof SingleAttribute<?>)
		{
			builder = context.getCurrent().transform();
		}
		else if (attribute instanceof CollectionAttribute)
		{
			builder =
				context.getCurrent().transformCollection().sort(((CollectionAttribute) attribute).getCollectionSortType());
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
	}

	private String getTargetAttributeName(Attribute attribute)
	{
		return nameConverter.convert(attribute.getCode());
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
		JavaMetaData javaAttribute = (JavaMetaData) attribute;
		JsonProperty jsonProperty = javaAttribute.getAnnotation(JsonProperty.class);
		convert(context, attribute, null, jsonProperty);

	}

	@Override
	public void visit(TransformationContext context, Attribute attribute,
		AttributeVisitor<TransformationContext> attributeVisitor)
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
		JavaMetaData javaAttribute = (JavaMetaData) attribute;
		if (javaAttribute.getAnnotation(JsonIgnore.class) == null)
		{
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
				JsonProperty jsonProperty = javaAttribute.getAnnotation(JsonProperty.class);
				convert(context, attribute, converter, jsonProperty);

			}
			else
			{
				EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
				// TODO in case of circular dependecies we need to lazily transform this attribute or create another
				// transformation

				JsonProperty jsonProperty = javaAttribute.getAnnotation(JsonProperty.class);
				Transformation transformation = context.getTypeTransformation(targetType);
				convert(context, attribute, transformation, jsonProperty);
			}
		}
	}

	@Override
	public void visitSubView(TransformationContext context, View view)
	{
		EntityType subType = (EntityType) view;
		context.visit(subType);
	}

	@Override
	public void visitSuperView(TransformationContext context, View view)
	{
		EntityType superType = (EntityType) view;
		context.visitSuperType(superType);

	}
}
