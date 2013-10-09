package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.transform.api.Converter;

public interface AttributeConverter {

	Converter<?, ?> createConverter(TransformationContext context, Attribute attribute, Visitor<TransformationContext> attributeVisitor);


}
