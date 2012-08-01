package org.atemsource.atem.utility.binding.jackson;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.utility.binding.AttributeFilter;
import org.codehaus.jackson.annotate.JsonIgnore;

public class JacksonIgnoreFilter implements AttributeFilter {

	@Override
	public boolean isExcluded(Attribute attribute) {
		return ((JavaMetaData) attribute).getAnnotation(JsonIgnore.class) != null;
	}

}
