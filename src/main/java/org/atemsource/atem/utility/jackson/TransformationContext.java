package org.atemsource.atem.utility.jackson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.impl.meta.SingleMetaAttribute;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
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

	public void cascade(EntityType<?> entityType, AttributeVisitor<TransformationContext> visitor)
	{
		for (TypeTransformationBuilder<?, ?> builder : transformationBuilders)
		{
			if (builder.getSourceType().equals(entityType))
			{
				return;
			}
		}

		visitedTypes.add(entityType.getCode());
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

	public EntityType getEntityTypeReference(EntityType<?> originalType)
	{
		return manager.getEntityTypeReference(originalType);
	}

	public <A, B> Converter<A, B> getTransformation(EntityType<A> entityType)
	{
		return transformations.get(entityType.getCode());
	}

	public JavaUniConverter<String, String> getTypeNameConverter()
	{
		return manager.getTypeNameConverter();
	}

	public Transformation getTypeTransformation(EntityType<?> targetType)
	{
		DerivedTypeTransformation transformation = BeanLocator.getInstance().getInstance(DerivedTypeTransformation.class);
		transformation.setTypeA(targetType);
		transformation.setTypeCodeConverter(getTypeNameConverter());
		Attribute metaAttribute =
			BeanLocator.getInstance().getInstance(EntityTypeRepository.class).getEntityType(EntityType.class)
				.getMetaAttribute(DerivationMetaAttributeRegistrar.DERIVED_FROM);
		transformation.setDerivedAttribute((SingleMetaAttribute) metaAttribute);

		transformation.setTypeB(getEntityTypeReference(targetType));
		return transformation;
	}

	public boolean isVisited(EntityType<?> entityType)
	{
		return visitedTypes.contains(entityType.getCode());
	}

	public void visit(EntityType subType)
	{
		visitedTypes.add(subType.getCode());
	}

	public void visitSuperType(EntityType superType)
	{
		getCurrent().includeSuper(getTypeTransformation(superType));
		visitedTypes.add(superType.getCode());
	}
}
