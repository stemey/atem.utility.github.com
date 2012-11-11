package org.atemsource.atem.utility.transform.impl.builder;

import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.GenericAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class GenericTransformationBuilder<A, B> extends
	AbstractAttributeTransformationBuilder<A, B, GenericTransformationBuilder<A, B>>
{
	private final Set<String> sourceAttributes = new HashSet<String>();

	private final Set<Attribute<?, ?>> targetAttributes = new HashSet<Attribute<?, ?>>();

	private EntityTypeBuilder targetTypeBuilder;

	private JavaTransformation<A, B> transformation;

	void addTargetAttribute(Attribute<?, ?> attribute)
	{
		targetAttributes.add(attribute);
	}

	@Override
	public void build(EntityTypeBuilder targetTypeBuilder)
	{
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		GenericAttributeTransformation<A, B> instance = beanLocator.getInstance(GenericAttributeTransformation.class);
		instance.setTransformation(transformation);
		Set<AttributePath> attributeAs = new HashSet<AttributePath>();
		for (String attribute : sourceAttributes)
		{
			attributeAs.add(sourcePathFactory.create(attribute, getSourceType()));
		}
		instance.setAttributeAs(attributeAs);
		Set<AttributePath> attributeBs = new HashSet<AttributePath>();
		for (Attribute<?, ?> attribute : targetAttributes)
		{
			attributeAs.add(targetPathFactory.create(attribute.getCode(), targetType));
		}
		instance.setAttributeBs(attributeBs);
		return instance;
	}

	@Override
	public GenericTransformationBuilder<A, B> from(String attributePath)
	{
		sourceAttributes.add(attributePath);
		return this;
	}


	@Override
	public EntityTypeBuilder getTargetTypeBuilder()
	{
		return targetTypeBuilder;
	}

	@Override
	public void setTargetTypeBuilder(EntityTypeBuilder targetTypeBuilder)
	{
		this.targetTypeBuilder = targetTypeBuilder;
	}

	public TransformationTargetTypeBuilder to()
	{
		return new TransformationTargetTypeBuilder(this);
	}

	public GenericTransformationBuilder<A, B> transform(JavaTransformation<A, B> transformation)
	{
		this.transformation = transformation;
		return this;
	}

}
