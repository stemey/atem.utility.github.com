/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.MapAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class TypeTransformationBuilder<A, B>
{

	private final class TypeCreator<A, B> implements Transformation<A, B>
	{
		private EntityType sourceType;

		private EntityType targetType;

		private TypeCreator(EntityType sourceType, EntityType targetType)
		{
			super();
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		@Override
		public UniTransformation<A, B> getAB()
		{
			return new UniTransformation<A, B>() {

				@Override
				public B convert(A a)
				{
					return (B) targetType.createEntity();
				}

				@Override
				public Type<A> getSourceType()
				{
					return sourceType;
				}

				@Override
				public Type<B> getTargetType()
				{
					return targetType;
				}

				@Override
				public B merge(A a, B b)
				{
					return (B) targetType.createEntity();
				}
			};
		}

		@Override
		public UniTransformation<B, A> getBA()
		{
			return new UniTransformation<B, A>() {

				@Override
				public A convert(B a)
				{
					return (A) sourceType.createEntity();
				}

				@Override
				public Type<B> getSourceType()
				{
					return targetType;
				}

				@Override
				public Type<A> getTargetType()
				{
					return sourceType;
				}

				@Override
				public A merge(B a, A b)
				{
					return (A) sourceType.createEntity();
				}
			};
		}

		@Override
		public Type getTypeA()
		{
			return sourceType;
		}

		@Override
		public Type getTypeB()
		{
			return targetType;
		}

	}

	@Inject
	private BeanLocator beanLocator;

	private Map<String, Converter> defaultConverters = new HashMap<String, Converter>();

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Inject
	private AttributePathBuilderFactory pathFactory;

	private EntityType sourceType;

	private EntityTypeBuilder targetTypeBuilder;

	private Transformation<A, B> transformation;

	private List<TransformationBuilder> transformations = new ArrayList<TransformationBuilder>();

	public TypeTransformationBuilder()
	{
	}

	private void addDerivation(Attribute newAttribute, Attribute sourceAttribute)
	{
		DerivationAttribute derivationAttribute = new DerivationAttribute();
		derivationAttribute.setOriginalAttribute(sourceAttribute);
		// derivationAttributeRegistrar.getDerivationAttribute().setValue(newAttribute, derivationAttribute);
	}

	public EntityTypeTransformation<A, B> buildTypeTransformation()
	{
		for (TransformationBuilder transformation : transformations)
		{
			transformation.build(targetTypeBuilder);
		}
		EntityType targetType = targetTypeBuilder.createEntityType();
		EntityTypeTransformation<A, B> entityTypeTransformation = beanLocator.getInstance(EntityTypeTransformation.class);
		if (transformation != null)
		{
			entityTypeTransformation.setTypeConverter(transformation);
		}
		else
		{
			entityTypeTransformation.setTypeConverter(new TypeCreator(sourceType, targetType));
		}

		for (TransformationBuilder transformation : transformations)
		{
			entityTypeTransformation.addTransformation(transformation.create(targetType));
		}
		entityTypeTransformation.setEntityTypeB(targetType);
		entityTypeTransformation.setEntityTypeA(sourceType);
		return entityTypeTransformation;
	}

	public EntityType getSourceType()
	{
		return sourceType;
	}

	public void putDefaultConverter(Converter typeTransformation)
	{
		defaultConverters.put(typeTransformation.getTypeA().getCode(), typeTransformation);
	}

	public void setSourceType(Class sourceType)
	{
		this.sourceType = entityTypeRepository.getEntityType(sourceType);
	}

	public void setSourceType(EntityType sourceType)
	{
		this.sourceType = sourceType;
	}

	public void setTargetTypeBuilder(EntityTypeBuilder targetTypeBuilder)
	{
		this.targetTypeBuilder = targetTypeBuilder;
	}

	public void setTransformation(Transformation<A, B> transformation)
	{
		this.transformation = transformation;
	}

	public AttributeTransformationBuilder transform()
	{
		SingleAttributeTransformationBuilder builder =
			beanLocator.getInstance(SingleAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		transformations.add(builder);
		return builder;
	}

	public AttributeTransformationBuilder transform(Class<? extends Attribute> attributeClass)
	{
		if (CollectionAttribute.class.isAssignableFrom(attributeClass))
		{
			return transformCollection();
		}
		else if (MapAttribute.class.isAssignableFrom(attributeClass))
		{
			return transformMap();
		}
		else
		{
			return transform();
		}
	}

	public CollectionAttributeTransformationBuilder transformCollection()
	{
		CollectionAttributeTransformationBuilder builder =
			beanLocator.getInstance(CollectionAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		transformations.add(builder);
		return builder;
	}

	public MapAttributeTransformationBuilder transformMap()
	{
		MapAttributeTransformationBuilder builder = beanLocator.getInstance(MapAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		transformations.add(builder);
		return builder;
	}
}
