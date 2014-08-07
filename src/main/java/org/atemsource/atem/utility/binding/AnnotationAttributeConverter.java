package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;

public class AnnotationAttributeConverter implements AttributeConverter {

	@Override
	public Converter<?, ?> createConverter(TransformationContext context,
			Attribute attribute, Visitor<TransformationContext> attributeVisitor) {
		if (attribute instanceof JavaMetaData) {
			Conversion conversion = ((JavaMetaData) attribute)
					.getAnnotation(Conversion.class);
			if (conversion != null) {
				JavaConverter<?, ?> javaConverter;
				try {
					javaConverter = conversion.value().newInstance();
				} catch (InstantiationException e) {
					throw new TechnicalException(
							"cannot instantiate converter", e);
				} catch (IllegalAccessException e) {
					throw new TechnicalException(
							"cannot instantiate converter", e);
				}
				return ConverterUtils.create(javaConverter);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
