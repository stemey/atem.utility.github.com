package org.atemsource.atem.utility.jackson;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class TransformationContext
{
	private RepositoryManager manager;

	private Stack<TypeTransformationBuilder<?, ?>> transformationBuilders = new Stack<TypeTransformationBuilder<?, ?>>();

	private Map<String, EntityTypeTransformation<?, ?>> transformations =
		new HashMap<String, EntityTypeTransformation<?, ?>>();

	public TransformationContext(RepositoryManager manager, TypeTransformationBuilder<?, ?> transformationBuilder)
	{
		super();
		this.manager = manager;
		this.transformationBuilders.push(transformationBuilder);
	}

	public void cascade(EntityType<?> entityType, AttributeVisitor<TransformationContext> visitor)
	{
		for (TypeTransformationBuilder<?, ?> builder : transformationBuilders)
		{
			if (builder.getSourceType().equals(entityType))
			{
				return;
			}
		}
		TypeTransformationBuilder<?, ?> transformationBuilder = manager.createTransformationBuilder(entityType);
		transformationBuilders.push(transformationBuilder);
		visitor.visit(this);
		TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders.pop();
		EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder.buildTypeTransformation();
		transformations.put(entityType.getCode(), transformation);
		manager.onTypeCreated((EntityType<?>) transformation.getTypeB());
	}

	public TypeTransformationBuilder<?, ?> getCurrent()
	{
		return transformationBuilders.peek();
	}

	public DerivedType getDerivedType(EntityType<?> targetType)
	{
		return manager.getDerivedType(targetType);
	}

	public <A, B> Converter<A, B> getTransformation(EntityType<A> entityType)
	{
		return transformations.get(entityType.getCode());
	}

}
