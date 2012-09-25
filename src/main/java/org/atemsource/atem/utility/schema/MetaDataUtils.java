package org.atemsource.atem.utility.schema;

import java.lang.annotation.Annotation;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.DerivedAttribute;

public class MetaDataUtils {

	private EntityType<Attribute<?, ?>> attributeType;

	private SingleAttribute<DerivedAttribute> derivedAttribute;

	public <A extends Annotation> A getMetaData(Attribute<?, ?> attribute,
			Class<A> annotationClass) {

		return getMetaData(attribute, annotationClass.getName());
	}

	public <A > A getMetaData(Attribute<?, ?> attribute,
			String name) {

		A a = getAnnotationFromMetaAttribute(attribute, name);
		if (a!=null) return a;
		a = getAnnotationFromDerivedAttribute(attribute, name);
		return a;
	}

	protected <A extends Annotation> A getAnnotationFromJavaMetaData(
			Attribute<?, ?> attribute, Class<A> annotationClass) {
		if (attribute instanceof JavaMetaData) {
			return ((JavaMetaData) attribute).getAnnotation(annotationClass);
		} else {
			return null;
		}
	}

	protected <A extends Annotation> A getAnnotationFromMetaAttribute(
			Attribute<?, ?> attribute, String name) {
		Attribute<?, ?> metaAttribute = attributeType
				.getMetaAttribute(name);
		if (metaAttribute != null) {
			return (A) metaAttribute.getValue(attribute);
		} else {
			return null;
		}
	}

	protected <A extends Annotation> A getAnnotationFromDerivedAttribute(
			Attribute<?, ?> attribute, String name) {
		DerivedAttribute derivedAttributeValue = derivedAttribute
				.getValue(attribute);
		Attribute<?, ?> originalAttribute = derivedAttributeValue
				.getOriginalAttribute();
		return getMetaData(originalAttribute, name);
	}
}
