package org.atemsource.atem.utility.binding;

import java.util.Iterator;
import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.transform.api.AttributeNameConverter;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;

public class TransformationVisitor implements ViewVisitor<TransformationContext> {

	private final AttributeNameConverter attributeNameConverter;

	private final List<AttributeFilter> filters;

	private final TypeNameConverter typeNameConverter;

	private List<AttributeConverter> attributeConverters;

	public TransformationVisitor(TypeNameConverter typeNameConverter, List<AttributeFilter> filters,
			AttributeNameConverter attributeNameConverter, List<AttributeConverter> attributeConverters) {
		super();
		this.typeNameConverter = typeNameConverter;
		this.attributeNameConverter = attributeNameConverter;
		this.filters = filters;
		this.attributeConverters = attributeConverters;
	}

	private <A, B> void convert(TransformationContext context, Attribute<?, A> attribute, Converter<A, B> converter) {
		String targetAttributeName;
		targetAttributeName = getTargetAttributeName(attribute);

		OneToOneAttributeTransformationBuilder<A, B, ?> builder;
		if (attribute instanceof SingleAttribute<?>) {
			builder = (OneToOneAttributeTransformationBuilder) context.getCurrent().transform();
		} else if (attribute instanceof CollectionAttribute) {
			builder = context.getCurrent().transformCollection()
					.sort(((CollectionAttribute) attribute).getCollectionSortType());
		} else if (attribute instanceof MapAttribute) {
			MapAttribute map = (MapAttribute) attribute;
			if (map.getKeyType() != null) {
				builder = (OneToOneAttributeTransformationBuilder<A, B, ?>) context.getCurrent().transformMap();
			} else {
				return;
			}
		} else {
			return;
		}
		builder.from(attribute.getCode()).to(targetAttributeName);
		if (attribute.getTargetType() == null) {
			builder.convertDynamically(typeNameConverter);
		} else {
			if (converter != null) {
				builder.convert(converter);
			}
		}
	}

	private String getTargetAttributeName(Attribute<?, ?> attribute) {
		if (attributeNameConverter == null) {
			return attribute.getCode();
		} else {
			return attributeNameConverter.convert(attribute);
		}
	}

	@Override
	public void visit(TransformationContext context, Attribute attribute) {
		if (filters != null) {
			for (AttributeFilter filter : filters) {
				if (filter.isExcluded(attribute)) {
					return;
				}
			}
		}
		Converter<?, ?> converter = null;
		if (attributeConverters != null) {
			Iterator<AttributeConverter> attributeConverterIterator = attributeConverters.iterator();
			while (converter == null && attributeConverterIterator.hasNext()) {
				converter = attributeConverterIterator.next().createConverter(context, attribute,null);
			}
		}

		convert(context, attribute, converter);

	}

	@Override
	public void visit(TransformationContext context, Attribute attribute,
			Visitor<TransformationContext> attributeVisitor) {
		if (filters != null) {
			for (AttributeFilter filter : filters) {
				if (filter.isExcluded(attribute)) {
					return;
				}
			}
		}
		boolean converted = false;
		if ( attributeConverters != null) {
			Converter converter = null;
			Iterator<AttributeConverter> attributeConverterIterator = attributeConverters.iterator();
			while (converter == null && attributeConverterIterator.hasNext()) {
				converter = attributeConverterIterator.next().createConverter(context, attribute,attributeVisitor);
			}
			if (converter != null) {
				converted = true;
				convert(context, attribute, converter);
			}

		}
		if (!converted) {
			Converter<?, ?> converter = context.getCurrent().getConverterFactory().get(attribute.getTargetType());
			if (converter != null) {
				convert(context, attribute, converter);
			} else {
				EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
				// TODO in case of circular dependencies we need to lazily
				// transform
				// this attribute or create another
				// transformation

				context.cascade(targetType, attributeVisitor);
				Transformation transformation = context.getTransformation(targetType);
				if (transformation == null) {
					throw new IllegalStateException("cannot tranform " + attribute);
				}
				convert(context, attribute, transformation);
			}
		}
	}

	@Override
	public void visitSubView(TransformationContext context, View view, Visitor<TransformationContext> subViewVisitor) {

		EntityType<?> subType = (EntityType<?>) view;
		context.visitSubview(subType);
		// we handle the visiting ourselves
	}

	@Override
	public void visitSuperView(TransformationContext context, View view, Visitor<TransformationContext> superViewVisitor) {
		EntityType<?> superType = (EntityType<?>) view;
		context.visitSuperType(superType, this);
		// we handle the visiting ourselves

	}
}
