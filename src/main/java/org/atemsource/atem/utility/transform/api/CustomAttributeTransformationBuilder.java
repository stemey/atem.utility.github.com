package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;

public interface CustomAttributeTransformationBuilder<A,B> extends
		AttributeTransformationBuilder<A,B> {
	void setSourceType(EntityType<A> a);

	void setConverterFactory(ConverterFactory converterFactory);
}
