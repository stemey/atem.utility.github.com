/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.CollectionAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.MapAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.SingleAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TypeTransformationBuilder<A, B> {

	private final class TypeCreator<A, B> implements Transformation<A, B> {
		private EntityType sourceType;

		private EntityType targetType;

		private TypeCreator(EntityType sourceType, EntityType targetType) {
			super();
			this.sourceType = sourceType;
			this.targetType = targetType;
		}

		@Override
		public UniTransformation<A, B> getAB() {
			return new UniTransformation<A, B>() {

				@Override
				public Type<A> getSourceType() {
					return sourceType;
				}

				@Override
				public Type<B> getTargetType() {
					return targetType;
				}

				@Override
				public B merge(A a, B b, TransformationContext ctx) {
					if (ctx.isTransformed(a)) {
						return (B) ctx.getTransformed(a);
					} else {
						return (B) targetType.createEntity();
					}
				}

				@Override
				public B convert(A a, TransformationContext ctx) {
					if (ctx.isTransformed(a)) {
						return (B) ctx.getTransformed(a);
					} else {
						return (B) targetType.createEntity();
					}
				}
			};
		}

		@Override
		public UniTransformation<B, A> getBA() {
			return new UniTransformation<B, A>() {

				@Override
				public Type<B> getSourceType() {
					return targetType;
				}

				@Override
				public Type<A> getTargetType() {
					return sourceType;
				}

				@Override
				public A merge(B a, A b, TransformationContext ctx) {
					if (ctx.isTransformed(a)) {
						return (A) ctx.getTransformed(a);
					} else {
						return (A) sourceType.createEntity();
					}
				}

				@Override
				public A convert(B a, TransformationContext ctx) {
					if (ctx.isTransformed(a)) {
						return (A) ctx.getTransformed(a);
					} else {
						return (A) sourceType.createEntity();
					}
				}
			};
		}

		@Override
		public Type getTypeA() {
			return sourceType;
		}

		@Override
		public Type getTypeB() {
			return targetType;
		}

	}

	@Inject
	private BeanLocator beanLocator;

	private ConverterFactory converterFactory;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Logger logger = Logger.getLogger(getClass());

	@Inject
	private AttributePathBuilderFactory pathFactory;

	private EntityTypeTransformation<A, B> selfReference;

	private EntityType<A> sourceType;

	private EntityTypeTransformation<?, ?> superTransformation;

	private EntityTypeBuilder targetTypeBuilder;

	private Transformation<A, B> transformation;

	private List<TransformationBuilder> transformations = new ArrayList<TransformationBuilder>();

	public TypeTransformationBuilder() {
	}

	private void addDerivation(EntityTypeTransformation<?, ?> transformation,
			EntityType<?> newType, EntityType<?> originalType) {
		DerivedType deriveType = new DerivedType();
		deriveType.setOriginalType(originalType);
		deriveType.setTransformation(transformation);
		Attribute metaAttribute = entityTypeRepository.getEntityType(
				EntityType.class).getMetaAttribute(
				DerivationMetaAttributeRegistrar.DERIVED_FROM);
		if (metaAttribute != null) {
			metaAttribute.setValue(newType, deriveType);
		}
		logger.info("finished init of " + newType.getCode());
	}

	public EntityTypeTransformation<A, B> buildTypeTransformation() {
		for (TransformationBuilder transformation : transformations) {
			transformation.build(targetTypeBuilder);
		}
		EntityType targetType = targetTypeBuilder.createEntityType();
		if (transformation != null) {
			selfReference.setTypeConverter(transformation);
		} else {
			selfReference.setTypeConverter(new TypeCreator(sourceType,
					targetType));
		}
		if (superTransformation != null) {
			EntityTypeTransformation<A, B> superEntityTypeTransformation = (EntityTypeTransformation<A, B>) superTransformation;
			selfReference.setSuperTransformation(superEntityTypeTransformation);
			superEntityTypeTransformation.addSubTransformation(selfReference);
		}
		for (TransformationBuilder transformation : transformations) {
			selfReference.addTransformation((AttributeTransformation<A, B>) transformation.create(targetType));
		}
		selfReference.setEntityTypeB(targetType);
		selfReference.setEntityTypeA(sourceType);
		addDerivation(selfReference, targetType, sourceType);
		return selfReference;
	}

	public ConverterFactory getConverterFactory() {
		return converterFactory;
	}

	public EntityTypeTransformation<?, ?> getReference() {
		return selfReference;
	}

	public EntityType<A> getSourceType() {
		return sourceType;
	}

	public void includeSuper(EntityTypeTransformation<?, ?> transformation) {
		targetTypeBuilder.superType((EntityType<?>) transformation.getTypeB());
		superTransformation = transformation;
	}

	@PostConstruct
	public void initialize() {
		selfReference = beanLocator.getInstance(EntityTypeTransformation.class);

	}

	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}

	public void setSourceType(Class sourceType) {
		this.sourceType = entityTypeRepository.getEntityType(sourceType);
	}

	public void setSourceType(EntityType sourceType) {
		this.sourceType = sourceType;
	}

	public void setTargetTypeBuilder(EntityTypeBuilder targetTypeBuilder) {
		this.targetTypeBuilder = targetTypeBuilder;
	}

	public AttributeTransformationBuilder<A, B> transform() {
		SingleAttributeTransformationBuilder builder = beanLocator
				.getInstance(SingleAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	public AttributeTransformationBuilder<A, B> transform(
			Class<? extends Attribute> attributeClass) {
		if (CollectionAttribute.class.isAssignableFrom(attributeClass)) {
			return transformCollection();
		} else if (MapAttribute.class.isAssignableFrom(attributeClass)) {
			return transformMap();
		} else {
			return transform();
		}
	}

	public CollectionAttributeTransformationBuilder<A, B> transformCollection() {
		CollectionAttributeTransformationBuilder<A, B> builder = beanLocator
				.getInstance(CollectionAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	public MapAttributeTransformationBuilder transformMap() {
		MapAttributeTransformationBuilder builder = beanLocator
				.getInstance(MapAttributeTransformationBuilder.class);
		builder.setSourceType(sourceType);
		builder.setConverterFactory(converterFactory);
		transformations.add(builder);
		return builder;
	}

	public void transformPrimitives(String... excludedAttributes) {
		for (Attribute<?, ?> attribute : getSourceType().getAttributes()) {
			if (ArrayUtils.contains(excludedAttributes, attribute.getCode())
					&& attribute.getTargetType() instanceof PrimitiveType<?>) {
				// TODO add conversion from local and global standard
				// transformers
				Converter converter = converterFactory.get(attribute
						.getTargetType());
				AttributeTransformationBuilder<A, B> transform = transform();
				transform.from(attribute.getCode()).to(attribute.getCode());// .convert(converter);
				if (converter != null) {
					transform.convert(converter);
				}
			}
		}
	}
}
