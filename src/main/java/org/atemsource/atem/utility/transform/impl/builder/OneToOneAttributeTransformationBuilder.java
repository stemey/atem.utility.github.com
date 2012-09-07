package org.atemsource.atem.utility.transform.impl.builder;

import net.sf.cglib.proxy.Enhancer;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;

public abstract class OneToOneAttributeTransformationBuilder<A, B, T extends OneToOneAttributeTransformationBuilder<A, B, T>>
		extends AbstractAttributeTransformationBuilder<A, B, T> {

	private Converter<?, ?> converter;
	private String sourceAttribute;
	private String targetAttribute;

	public String getSourceAttribute() {
		return sourceAttribute;
	}

	public String getTargetAttribute() {
		return targetAttribute;
	}

	public T convert(Converter<?, ?> converter) {
		this.converter = converter;
		return (T) this;
	}

	public T convertDynamically(TypeNameConverter typeCodeConverter) {
		Attribute metaAttribute = entityTypeRepository.getEntityType(
				EntityType.class).getMetaAttribute(
				DerivationMetaAttributeRegistrar.DERIVED_FROM);
		if (metaAttribute == null) {
			throw new IllegalStateException(
					"cannot convert dynamically if metaAttribute is missing");
		}
		this.converter = new DynamicTransformation(typeCodeConverter,
				sourceType, entityTypeRepository,
				(SingleAttribute) metaAttribute);
		return (T) this;
	}

	@Override
	public T from(String sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
		return (T) this;
	}

	@Override
	public A fromMethod() {
		Enhancer enhancer = new Enhancer();
		return (A) enhancer.create(sourceType.getJavaType(), new PathRecorder(
				this));
	}

	protected Converter<?, ?> getConverter(Type<?> type) {
		if (converter != null) {
			return converter;
		} else {
			return (Converter<?, ?>) converterFactory.get(type);
		}
	}

	protected Transformation<?, ?> getTransformation(Type<?> type) {
		if (type instanceof EntityType<?>) {

			if (converter != null && converter instanceof Transformation) {
				return (Transformation<?, ?>) converter;
			} else {
				return null;
			}
		} else {
			Converter<?, ?> simpleConverter = getConverter(type);
			if (simpleConverter != null) {
				return new PrimitiveTransformation(simpleConverter);
			} else {
				return null;
			}
		}
	}

	public T to(String targetAttribute) {
		this.targetAttribute = targetAttribute;
		return (T) this;
	}

}
