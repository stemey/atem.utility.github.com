package org.atemsource.atem.utility.binding.jackson;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.utility.transform.api.AttributeNameConverter;
import org.codehaus.jackson.annotate.JsonProperty;

public class JacksonAttributeNameConverter implements AttributeNameConverter {

	@Override
	public String convert(Attribute<?, ?> attribute) {
		JsonProperty jsonProperty = ((JavaMetaData) attribute)
				.getAnnotation(JsonProperty.class);
		if (jsonProperty != null) {
			return jsonProperty.value();
		} else {
			return attribute.getCode();
		}
	}

}
