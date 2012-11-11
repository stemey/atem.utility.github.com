package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.path.AttributePathFactory;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;

public interface CustomAttributeTransformationBuilder<A, B, T extends AttributeTransformationBuilder<A, B, T>>
		extends AttributeTransformationBuilder<A, B, T> {
	void setSourceType(EntityType<A> a);

	void setConverterFactory(ConverterFactory converterFactory);

	void setTargetTypeBuilder(EntityTypeBuilder builder);

	void setSourcePathFactory(AttributePathFactory sourcePathFactory);

	void setTargetPathFactory(AttributePathFactory targetPathFactory);
}
