package org.atemsource.atem.utility.jackson;

import java.util.List;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
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

	private void convert(TransformationContext context, Attribute attribute, Transformation<?, ?> transformation,
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
		if (transformation != null)
		{
			builder.convert(transformation);
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
		JavaMetaData javaAttribute = (JavaMetaData) attribute;
		if (javaAttribute.getAnnotation(JsonIgnore.class) == null)
		{
			EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
			DerivedType derivedType = context.getDerivedType(targetType);
			if (derivedType == null)
			{
				context.cascade(targetType, attributeVisitor);
			}
			Transformation<?, ?> transformation = context.getDerivedType(targetType).getTransformation();
			JsonProperty jsonProperty = javaAttribute.getAnnotation(JsonProperty.class);
			convert(context, attribute, transformation, jsonProperty);
		}
	}
}
