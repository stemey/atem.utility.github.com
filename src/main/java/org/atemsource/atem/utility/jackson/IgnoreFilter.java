package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.codehaus.jackson.annotate.JsonIgnore;


public class IgnoreFilter implements AttributeFilter
{

	@Override
	public boolean isExcluded(Attribute attribute)
	{
		JavaMetaData javaAttribute = (JavaMetaData) attribute;
		return javaAttribute.getAnnotation(JsonIgnore.class) != null;
	}
}