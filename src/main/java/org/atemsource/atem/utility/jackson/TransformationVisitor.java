package org.atemsource.atem.utility.jackson;

import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


public class TransformationVisitor implements ViewVisitor<TransformationContext>
{

	private List<AttributeFilter> filters;

	private JavaUniConverter<String, String> nameConverter;

	public TransformationVisitor(List<AttributeFilter> filters, JavaUniConverter<String, String> nameConverter)
	{
		super();
		this.nameConverter = nameConverter;
		this.filters = filters;
	}

	public <A, B> Converter<A, B> cast(Converter<A, B> c)
	{
		return c;
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
		if (jsonProperty == null)
		{
			context.getCurrent().transform().from(attribute.getCode()).to(getTargetAttributeName(attribute));
		}
		else
		{
			context.getCurrent().transform().from(attribute.getCode()).to(jsonProperty.value());
		}

	}

	@Override
	public void visit(TransformationContext context, Attribute attribute,
		AttributeVisitor<TransformationContext> attributeVisitor)
	{
		JavaMetaData javaAttribute = (JavaMetaData) attribute;
		if (javaAttribute.getAnnotation(JsonIgnore.class) == null)
		{
			EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
			Converter transformation = context.getTransformation(targetType);
			if (transformation == null)
			{
				context.cascade(targetType, attributeVisitor);
			}
			transformation = context.getTransformation(targetType);
			JsonProperty jsonProperty = javaAttribute.getAnnotation(JsonProperty.class);
			if (jsonProperty == null)
			{
				context.getCurrent().transform().from(attribute.getCode()).to(getTargetAttributeName(attribute))
					.convert(transformation);
			}
			else
			{
				context.getCurrent().transform().from(attribute.getCode()).to(jsonProperty.value()).convert(transformation);
			}
		}
	}
}
