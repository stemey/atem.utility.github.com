package org.atemsource.atem.utility.jackson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class TransformationContext
{
	private RepositoryManager manager;

	private Stack<TypeTransformationBuilder<?, ?>> transformationBuilders = new Stack<TypeTransformationBuilder<?, ?>>();

	private Map<String, EntityTypeTransformation<?, ?>> transformations =
		new HashMap<String, EntityTypeTransformation<?, ?>>();

	private Set<String> visitedTypes = new HashSet<String>();

	public TransformationContext(RepositoryManager manager, TypeTransformationBuilder<?, ?> transformationBuilder)
	{
		super();
		this.manager = manager;
		this.transformationBuilders.push(transformationBuilder);
	}

	public void addTransformationBuilder(TypeTransformationBuilder<?, ?> transformationBuilder)
	{
		transformationBuilders.add(transformationBuilder);

	}

	public void cascade(EntityType<?> entityType, AttributeVisitor<TransformationContext> visitor)
	{
		// for (TypeTransformationBuilder<?, ?> builder :
		// transformationBuilders)
		// {
		// if (builder.getSourceType().equals(entityType))
		// {
		// return;
		// }
		// }

		switch (getState(entityType))
		{
			case FINISHED:
			// visitor.visit(this);
			break;
			case NOT_STARTED:
				visitedTypes.add(entityType.getCode());
				TypeTransformationBuilder<?, ?> transformationBuilder = manager.createTransformationBuilder(entityType);
				transformationBuilders.push(transformationBuilder);
				visitor.visit(this);
				TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders.pop();
				EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder.buildTypeTransformation();
				transformations.put(entityType.getCode(), transformation);
				manager.onTypeCreated((EntityType<?>) transformation.getTypeB());
			break;
			case STARTED:
			// visitor.visit(this);
			break;
		}
	}

	public void finishTransformation(EntityType entityType)
	{
		TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders.pop();
		EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder.buildTypeTransformation();
	}

	public TypeTransformationBuilder<?, ?> getCurrent()
	{
		return transformationBuilders.peek();
	}

	public DerivedType getDerivedType(EntityType<?> targetType)
	{
		return manager.getDerivedType(targetType);
	}

	public EntityType getEntityTypeReference(EntityType<?> originalType)
	{
		return manager.getEntityTypeReference(originalType);
	}

	public State getState(EntityType sourceType)
	{
		if (transformations.containsKey(sourceType.getCode()))
		{
			return State.FINISHED;
		}
		for (TypeTransformationBuilder<?, ?> builder : transformationBuilders)
		{
			if (builder.getSourceType().equals(sourceType))
			{
				return State.STARTED;
			}
		}
		return State.NOT_STARTED;
	}

	public <A, B> Converter<A, B> getTransformation(EntityType<A> entityType)
	{
		return transformations.get(entityType.getCode());
	}

	public JavaUniConverter<String, String> getTypeNameConverter()
	{
		return manager.getTypeNameConverter();
	}

	public EntityTypeTransformation getTypeTransformation(EntityType<?> targetType)
	{
		return transformations.get(targetType.getCode());
	}

	public boolean isCreated(EntityType sourceType)
	{
		for (TypeTransformationBuilder<?, ?> builder : transformationBuilders)
		{
			if (builder.getSourceType().equals(sourceType))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isVisited(EntityType<?> entityType)
	{
		return visitedTypes.contains(entityType.getCode());
	}

	public void setTransformations(Map<String, EntityTypeTransformation<?, ?>> transformations)
	{
		this.transformations = transformations;
	}

	public void startTransformation(EntityType entityType)
	{
		TypeTransformationBuilder<?, ?> transformationBuilder = manager.createTransformationBuilder(entityType);
		transformationBuilders.push(transformationBuilder);
		transformations.put(entityType.getCode(), transformationBuilder.getReference());
	}

	public void visit(EntityType subType)
	{
		visitedTypes.add(subType.getCode());
	}

	public void visitSuperType(EntityType superType, TransformationVisitor visitor)
	{
		switch (getState(superType))
		{
			case FINISHED:
			break;
			case NOT_STARTED:
				visitedTypes.add(superType.getCode());
				TypeTransformationBuilder<?, ?> transformationBuilder = manager.createTransformationBuilder(superType);
				transformationBuilders.push(transformationBuilder);
				transformations.put(superType.getCode(), transformationBuilder.getReference());
				superType.visit(visitor, this);
				TypeTransformationBuilder<?, ?> typeTransformationBuilder = transformationBuilders.pop();
				EntityTypeTransformation<?, ?> transformation = typeTransformationBuilder.buildTypeTransformation();
				manager.onTypeCreated((EntityType<?>) transformation.getTypeB());
			break;
			case STARTED:
			break;
		}
		getCurrent().includeSuper(getTypeTransformation(superType));
	}
}
